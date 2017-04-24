package br.ufms.facom.ma.dissertacao.kmeans;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculator;
import br.ufms.facom.ma.dissertacao.util.Sequence;

public class BisectKMeans extends CopKMeans {

	private List<Point> data;

	private List<Constraint> constraints;
	private int iterations;
	private DistanceCalculator calculator;
	private List<HCluster> clusters;

	public Result bisecKmeans(List<Point> data, MetaData metaData, List<Constraint> constraints, int iterations,
			DistanceCalculator calculator) {

		this.data = data;

		this.constraints = constraints;
		this.iterations = iterations;
		this.calculator = calculator;
		int k = 2;

		List<HCluster> clusterHistory = new ArrayList<HCluster>(100);
		// constroi o cluster root
		HCluster root = new HCluster(Sequence.getSequence().next(), new Point("root_point", 0, 0));
		clusterHistory.add(root);
		
		int i = 1;
		buildRoot();

		Deque<HCluster> stack = new ArrayDeque<HCluster>();
		this.clusters.forEach(cl -> {
			stack.addLast(cl);
			clusterHistory.add(cl);
			cl.setFather(root);
		});

		while (true) {

			if (stack.isEmpty())
				break;

			// pega o primeiro cluster da pilha
			HCluster actCluster = stack.removeLast();
			if (actCluster.getPoints().size() <= 1 || actCluster.quadraticError() == 0)
				continue;

			// inicializa os cluster apenas para os dados do cluster atual
			List<HCluster> cls = initHClusters(actCluster.getPoints(), k);
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
				assignClusters(actCluster.getPoints(), cls, calculator);

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
			this.checkForClusterLess();

		}
		this.validateSchema();
		return new Result(data, clusterHistory, metaData);
	}

	void buildRoot() {

		this.clusters = initHClusters(data, 2);

		int i = 1;
		// inicia as iteracoes
		while (true) {

			// limpa os clusters
			clusters.forEach(c -> c.clear());

			// associa cada ponto ao cluster mais perto
			assignClusters(data, clusters, calculator);

			// recalcula a posicao dos clusters
			boolean changed = repositionClusters(clusters);
			if (!changed || i == iterations) {
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
	void assignHClusters() {
		List<Constraint> cannotLink = this.constraints.stream().filter(c -> c.getType() == Constraint.Type.CANNOT_LINK)
				.collect(Collectors.toList());
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
	void assignClusters(List<Point> points, List<? extends Cluster> cls, DistanceCalculator calculator) {
		for (Point point : points) {
			assignClosestCluster(point, cls, calculator);
		}
	}

	void checkForClusterLess() {
		int count = 0;
		for (Point p : this.data) {
			if (p.getCluster() == null) {
				System.out.printf("O ponto %s não possuir cluster\n", p.getId());
				count++;
			}

		}
		if (count > 0)
			System.out.printf("%d pontos não possuem cluster\n", count);
	}

	void validateSchema() {
		int naoFolha = 0;
		int naoCluster = 0;
		for (Point p : this.data) {
			if (p.getCluster() == null) {
				System.out.printf("O ponto %s não possuir cluster\n", p.getId());
				naoCluster++;
			} else {
				Cluster c = p.getCluster();
				List<Cluster> children = this.clusters.stream().filter(cls -> cls.getFather().getId() == c.getId())
						.collect(Collectors.toList());
				if (children.size() > 0) {
					System.out.printf("O ponto %s não está em um cluster folha\n", p.getId());
					naoFolha++;
				}
			}

		}
		System.out.printf("Existem %d pontos que nao estao em cluster folha\n", naoFolha);
		System.out.printf("Existem %d pontos que nao estao em cluster nenhum\n", naoCluster);
	}

	void plotHClusters(List<HCluster> clusters) {
		for (HCluster cluster : clusters) {
			System.out.println(cluster.toString());
		}
	}

}
