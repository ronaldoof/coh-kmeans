package br.ufms.facom.ma.dissertacao.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.util.Precision;

import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculator;

public class KMeans {

	public Result kmeans(List<Point> data, int clustersNumber, int iterations, DistanceCalculator calculator) {

		List<Cluster> clusters = initClusters(data, clustersNumber);
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

		return new Result(data, clusters, null);
	}

	public Result optimusKmeans(List<Point> data, int clustersNumber, int iterations, DistanceCalculator calculator,
			int instances) {
		Result bestResult = kmeans(data, clustersNumber, iterations, calculator);

		for (int i = 0; i <= instances; i++) {
			Result tmpResult = kmeans(data, clustersNumber, iterations, calculator);
			if (tmpResult.getQuadraticError() < bestResult.getQuadraticError()) {
				bestResult = tmpResult;
			}
		}
		return bestResult;
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

	/**
	 * Inicializa os clusters
	 * 
	 * @param data
	 *            Lista de pontos que vao ser clusterizados
	 * @param clustersNumber
	 *            Quantidade de Clusters a serem criados
	 * @return Lista de clusters iniciais
	 */
	List<Cluster> initClusters(List<Point> data, int clustersNumber) {
		// build clusters
		List<Point> centroids = createRandomCentroids(data, clustersNumber);
		List<Cluster> clusters = new ArrayList<Cluster>();
		int count = 0;
		for (Point centroid : centroids) {
			clusters.add(new Cluster(count, centroid));
			count++;
		}

		return clusters;
	}

	/**
	 * Reposiciona todos os clusters
	 * 
	 * @param clusters
	 */
	boolean repositionClusters(List<? extends Cluster> clusters) {
		boolean changed = false;
		int i = 0;
		while (i < clusters.size()) {
			Cluster cluster = clusters.get(i);
			changed = repositionCluster(cluster) || changed;
			i++;
		}
		return changed;
	}

	/**
	 * Reposiciona o cluster para um ponto medio dentre os pontos que fazem
	 * parte dele
	 * 
	 * @param cluster
	 *            Custer que vai ser reposicionado
	 */
	boolean repositionCluster(Cluster cluster) {
		if (cluster.getPoints().isEmpty())
			return false;
		boolean changed = false;
		for (int i = 1; i <= cluster.getCentroid().dimensions(); i++) {
			double dimensionCoord = 0.0d;
			for (Point point : cluster.getPoints()) {
				dimensionCoord += point.getCoord(i);
			}
			dimensionCoord = Precision.round((dimensionCoord / cluster.getPoints().size()), 2);
			changed = dimensionCoord != cluster.getCentroid().getCoord(i);
			cluster.getCentroid().setCoord(dimensionCoord, i);
		}
		return changed;
	}

	/**
	 * Initialize the random clusters
	 * 
	 * @param data
	 *            points with N dimensions
	 * @param clusters
	 *            number of clusters to build
	 * @return List of centroids built
	 */
	List<Point> createRandomCentroids(List<Point> data, int clusters) {
		List<Point> centroids = new ArrayList<Point>();

		for (int i = 1; i <= clusters; i++) {
			int dimensions = data.get(0).dimensions();
			centroids.add(buildCentroid(data, dimensions));
		}

		return centroids;
	}

	Point buildCentroid(List<Point> data, int dimensions) {
		int randomNum = ThreadLocalRandom.current().nextInt(0, data.size());
		return data.get(randomNum);

		// double [] coords = new double[data.get(0).dimensions()];
		// for(int i=1; i<= dimensions; i++){
		// double upperLimit = findUpperLimit(data, i);
		// double lowerLimit = findLowerLimit(data, i);
		// if(lowerLimit != upperLimit)
		// coords[i-1] = createRandomCoord(lowerLimit, upperLimit);
		// else
		// coords[i-1] = lowerLimit;
		// }
		// return new Point("", coords);
	}

	// Creates random point
	double createRandomCoord(double min, double max) {
		return Precision.round(ThreadLocalRandom.current().nextDouble(min, max), 2);
	}

	double findUpperLimit(List<Point> points, int i) {
		double max = Double.MIN_NORMAL;
		for (Point point : points) {
			if (point.getCoord(i) > max) {
				max = point.getCoord(i);
			}
		}
		return max;
	}

	double findLowerLimit(List<Point> points, int i) {
		double min = Double.MAX_VALUE;
		for (Point point : points) {
			if (point.getCoord(i) < min) {
				min = point.getCoord(i);
			}
		}
		return min;
	}

	/**
	 * Acha o cluster mais proximo do ponto e atribui o ponto a ele.
	 * 
	 * @param point
	 *            Ponto a ser atribuido a um cluster
	 * @param clusters
	 *            Lista de clusters criados
	 */
	void assignClosestCluster(Point point, List<? extends Cluster> clusters, DistanceCalculator calculator) {
		double min = Double.MAX_VALUE;
		int nearestID = 0;
		for (int i = 0; i < clusters.size(); i++) {
			double distance = point.distanceTo(clusters.get(i).getCentroid(), calculator);
			if (distance < min) {
				min = distance;
				nearestID = i;
			}
		}
		System.out.printf("Associando o ponto %s para o cluster %d\n", point.getId(), clusters.get(nearestID).getId());
		point.setCluster(clusters.get(nearestID));
		clusters.get(nearestID).addPoint(point);
	}

	void plotClusters(List<Cluster> clusters) {
		for (Cluster cluster : clusters) {
			System.out.println(cluster.toString());
		}
	}
}
