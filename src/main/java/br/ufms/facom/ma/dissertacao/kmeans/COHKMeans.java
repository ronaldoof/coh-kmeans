package br.ufms.facom.ma.dissertacao.kmeans;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.ufms.facom.ma.dissertacao.kmeans.Constraint.ConstraintBuilder;
import br.ufms.facom.ma.dissertacao.kmeans.Constraint.Type;
import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculator;
import br.ufms.facom.ma.dissertacao.util.Sequence;

public class COHKMeans extends CopKMeans {

	public static final double R = 6372.8; // In kilometers

	private List<Point> data;
	private List<Constraint> mustLink;
	private int iterations;
	private DistanceCalculator calculator;
	private List<HCluster> clusters;
	private List<String[]> geotags;
	private float threshold = 1000; // In kilometers

	public Result cohKmeans(List<Point> data, MetaData metaData, List<Constraint> mustLink, List<String[]> geotags,
			int iterations, DistanceCalculator calculator, float threshold) {

		this.data = data;

		this.mustLink = mustLink;
		this.iterations = iterations;
		this.calculator = calculator;
		this.geotags = geotags;

		List<HCluster> clusterHistory = new ArrayList<HCluster>(100);
		HCluster root = new HCluster(Sequence.getSequence().next(), new Point("root_point", 0, 0));
		List<Constraint> cannotLink = buildCannotLink(this.data, 1);

		this.clusters = initClusters(cannotLink);
		int i = 1;
		clusterHistory.add(root);

		// a partir do segundo nivel K é sempre 2. Bisect-KMeans
		int k = 2;

		Deque<HCluster> stack = new ArrayDeque<HCluster>();
		this.clusters.forEach(cl -> {
			stack.addLast(cl);
			clusterHistory.add(cl);
			cl.setFather(root);
		});

		this.buildRoot(cannotLink);

		while (true) {

			if (stack.isEmpty())
				break;

			// pega o primeiro cluster da pilha
			HCluster actCluster = stack.removeLast();
			if (actCluster.getPoints().size() <= 1 || actCluster.quadraticError() == 0)
				continue;

			// inicializa os cluster apenas para os dados do cluster atual
			List<HCluster> cls = initHClusters(actCluster.getPoints(), k);

			// calcula as constraints desse cluster
			List<Constraint> actCannotLink = buildCannotLink(actCluster.getPoints(), actCluster.getHeight());

			// marca o cluster atual como pai de todos os clusters criados e
			// adiciona esses clusters para a pilha
			cls.forEach(cl -> {
				cl.setFather(actCluster);
				stack.addLast(cl);
			});

			// inicia as iteracoes
			while (true) {

				// limpa os clusters
				cls.forEach(c -> c.clear());

				// associa cada ponto ao cluster mais perto
				assignHClusters(actCluster.getPoints(), cls, actCannotLink, calculator);

				// recalcula a posicao dos clusters
				boolean changed = repositionClusters(cls);
				if (!changed || i == iterations) {
					break;
				}
				i++;
			}
			i = 1;
			cls.forEach(cl -> {
				if (!cl.getPoints().isEmpty()) {
					clusterHistory.add(cl);
				}
			});

		}
		return new Result(data, clusterHistory, metaData);
	}

	List<HCluster> initClusters(List<Constraint> cannotLink) {

		List<HCluster> clusters = new ArrayList<HCluster>();

		for (Constraint constraint : mustLink) {

			Point pointA = findPointById(data, constraint.getPointA());
			Point pointB = findPointById(data, constraint.getPointB());

			if (pointA.getCluster() == null && pointB.getCluster() == null) {
				HCluster c = new HCluster(Sequence.getSequence().next(), pointA);
				c.addPoint(pointA);
				c.addPoint(pointB);
				pointA.setCluster(c);
				pointB.setCluster(c);
				clusters.add(c);
			} else if (pointA.getCluster() != null && pointB.getCluster() == null) {
				HCluster c = null;
				if (canAddToCluster(pointA.getCluster(), pointB, cannotLink)) {
					c = (HCluster) pointA.getCluster();
				} else {
					c = new HCluster(Sequence.getSequence().next(), pointB);
					clusters.add(c);
				}
				c.addPoint(pointB);
				pointB.setCluster(c);

			} else if (pointA.getCluster() == null && pointB.getCluster() != null) {
				HCluster c = null;
				if (canAddToCluster(pointB.getCluster(), pointA, cannotLink)) {
					c = (HCluster) pointB.getCluster();
				} else {
					c = new HCluster(Sequence.getSequence().next(), pointA);
					clusters.add(c);
				}
				c.addPoint(pointA);
				pointA.setCluster(c);

			}
		}

		return clusters;
	}

	void buildRoot(List<Constraint> cannotLink) {
		int i = 1;

		Map<Integer, List<Point>> clusterMemory = new HashMap<Integer, List<Point>>();
		this.clusters.forEach(c -> clusterMemory.put(c.getId(), c.getPoints()));

		// inicia as iteracoes
		while (true) {

			// limpa os clusters mas mantem a memoria dos originais
			this.clusters.forEach(c -> {
				c.clear();
				List<Point> memoryPoints = clusterMemory.get(c.getId());
				c.setPoints(memoryPoints);
				memoryPoints.forEach(p -> p.setCluster(c));
			});

			// associa cada ponto ao cluster mais perto
			assignHClusters(cannotLink);

			// recalcula a posicao dos clusters
			boolean changed = repositionClusters(this.clusters);
			if (!changed || i == this.iterations) {
				break;
			}
			i++;
		}

	}

	/**
	 * Associa os pontos ao cluster cujo centroide esteja mais proximo. A
	 * distancia é calculada de acordo com o calculador de distancia passado.
	 * 
	 * @param data
	 *            Lista de pontos
	 * @param clusters
	 *            Lista de Clusters
	 * @param calculator
	 *            Calculador de Distancia - define como será calculada a
	 *            distância entre os pontos.
	 * @see DistanceCalculator
	 */
	void assignHClusters(List<Constraint> cannotLink) {
		// pega os pontos que nao tem cluster
		List<Point> clusterless = this.data.stream().filter(p -> p.getCluster() == null).collect(Collectors.toList());

		for (Point point : clusterless) {
			assignClosestHCluster(point, cannotLink);
		}
	}

	/**
	 * Acha o cluster mais proximo do ponto ao qual esse ponto pode ser
	 * atribuido e atribui o ponto a ele.
	 * 
	 * @param point
	 *            Ponto a ser atribuido a um cluster
	 * @param clusters
	 *            Lista de clusters criados
	 */
	void assignClosestHCluster(Point point, List<Constraint> filteredCannot) {
		double min = Double.MAX_VALUE;
		int nearestID = 0;
		for (int i = 0; i < clusters.size(); i++) {
			double distance = point.distanceTo(clusters.get(i).getCentroid(), calculator);
			if (distance < min && this.canAddToCluster(clusters.get(i), point, filteredCannot)) {
				min = distance;
				nearestID = i;
			}
		}

		point.setCluster(clusters.get(nearestID));
		clusters.get(nearestID).addPoint(point);
	}

	boolean canAddToCluster(Cluster c, Point p, List<Constraint> filteredCannot) {
		// traz todos os pontos que possuem restricao cannot link com P
		List<String> cannotPoints = getCannotLinkPairs(filteredCannot, p.getId());

		long count = c.getPoints().parallelStream().filter(po -> cannotPoints.contains(po.getId())).count();
		// Verifica se algum desses pontos esta no cluster
		if (count == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param filteredCannot
	 * @param p
	 * @return
	 */
	List<String> getCannotLinkPairs(List<Constraint> filteredCannot, String p) {
		List<String> pairs = filteredCannot.stream().map(c -> c.getPair(p)).collect(Collectors.toList());
		return pairs;
	}

	/**
	 * Encontra os clusters que possuem ao menos 1 dos P pontos passados
	 * 
	 * @param clusters
	 *            Lista de cluster na qual sera feita a busca
	 * @param points
	 *            Lista de pontos a buscar
	 * @return
	 */
	HCluster searchClusters(List<HCluster> clusters, Point p) {
		HCluster cluster = clusters.stream().filter(c -> c.getPoints().stream().filter(po -> po.equals(p)).count() > 0)
				.findFirst().orElse(null);
		return cluster;

	}

	List<Constraint> findAllConstraints(List<Constraint> constraints, Point p) {
		List<Constraint> contains = constraints.stream()
				.filter(c -> (c.getPointA().equals(p) || c.getPointB().equals(p))).collect(Collectors.toList());

		return contains;
	}

	/**
	 * Inicializa os clusters
	 * 
	 * @param data
	 *            Lista de pontos que vao ser clusterizados
	 * @param clustersNumber
	 *            Quantidade de Clusters a serem criados
	 * @return Lista de clusters iniciais
	 */
	List<HCluster> initHClusters(List<Point> points, int clustersNumber) {
		// build clusters
		List<Point> centroids = createRandomCentroids(points, clustersNumber);
		List<HCluster> newClusters = new ArrayList<HCluster>();
		for (Point centroid : centroids) {
			newClusters.add(new HCluster(Sequence.getSequence().next(), centroid));
		}

		return newClusters;
	}

	/**
	 * Associa os pontos ao cluster cujo centroide esteja mais proximo. A
	 * distancia é calculada de acordo com o calculador de distancia passado.
	 * 
	 * @param data
	 *            Lista de pontos
	 * @param clusters
	 *            Lista de Clusters
	 * @param calculator
	 *            Calculador de Distancia - define como será calculada a
	 *            distância entre os pontos.
	 * @see DistanceCalculator
	 */
	void assignHClusters(List<Point> points, List<HCluster> cls, List<Constraint> cannotLink,
			DistanceCalculator calculator) {
		if (points.size() == 2) {
			int i = 0;
			for (Cluster cluster : cls) {
				Point p = points.get(i);
				cluster.setCentroid(p);
				cluster.addPoint(p);
				p.setCluster(cluster);
				i++;
			}
		} else {
			for (Point point : points) {
				assignClosestHCluster(point, cls, cannotLink, calculator);
			}
		}

	}

	/**
	 * Acha o cluster mais proximo do ponto e atribui o ponto a ele.
	 * 
	 * @param point
	 *            Ponto a ser atribuido a um cluster
	 * @param clusters
	 *            Lista de clusters criados
	 */
	void assignClosestHCluster(Point point, List<HCluster> clusters, List<Constraint> cannotLink,
			DistanceCalculator calculator) {
		double min = Double.MAX_VALUE;
		int nearestID = 0;
		for (int i = 0; i < clusters.size(); i++) {
			double distance = point.distanceTo(clusters.get(i).getCentroid(), calculator);
			if ((distance < min) && (canAddToCluster(clusters.get(i), point, cannotLink))) {
				min = distance;
				nearestID = i;
			}
		}
		System.out.printf("Associando o ponto %s para o cluster %d\n", point.getId(), clusters.get(nearestID).getId());
		point.setCluster(clusters.get(nearestID));
		clusters.get(nearestID).addPoint(point);
	}

	double haversine(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	List<Constraint> buildCannotLink(List<Point> points, int scale) {
		List<Constraint> cannotLinks = new ArrayList<Constraint>();
		List<String[]> filteredGeotags = filterGeotags(this.geotags, points);
		for (int i = 0; i < filteredGeotags.size() - 1; i++) {
			for (int j = i + 1; j < filteredGeotags.size(); j++) {

				double distance = haversine(Double.valueOf(filteredGeotags.get(i)[1]),
						Double.valueOf(filteredGeotags.get(i)[2]), Double.valueOf(filteredGeotags.get(j)[1]),
						Double.valueOf(filteredGeotags.get(j)[2]));
				if (distance >= (this.threshold / scale)) {
					Constraint cl = new ConstraintBuilder().pointA(filteredGeotags.get(i)[0])
							.pointB(filteredGeotags.get(j)[0]).type(Type.CANNOT_LINK).build();
					cannotLinks.add(cl);
				}

			}
		}
		return cannotLinks;
	}

	List<String[]> filterGeotags(List<String[]> geotags, List<Point> points) {
		List<String[]> newGeotags = new ArrayList<String[]>();

		for (String[] geotag : geotags) {
			if (containsId(points, geotag[0])) {
				newGeotags.add(geotag);
			}
		}
		return newGeotags;
	}

	boolean containsId(List<Point> points, String id) {
		return points.stream().anyMatch(p -> p.getId().equals(id));
	}

	boolean isLeaf(HCluster c) {
		List<Cluster> children = this.clusters.stream().filter(cls -> cls.getFather().getId() == c.getId())
				.collect(Collectors.toList());
		return children.isEmpty();
	}

	void plotHClusters(List<HCluster> clusters) {
		for (HCluster cluster : clusters) {
			System.out.println(cluster.toString());
		}
	}

}
