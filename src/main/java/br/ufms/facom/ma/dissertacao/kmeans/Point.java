package br.ufms.facom.ma.dissertacao.kmeans;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculator;
import br.ufms.facom.ma.dissertacao.kmeans.distance.Euclidean;

public class Point {

	private String id;

	private double[] coords;

	private Cluster cluster;

	public Point() {
	}

//	public Point(double... coords) {
//		this.id = Arrays.toString(coords);
//		this.coords = coords;
//	}

	public Point(String id, double... coords) {
		this.id = id;
		this.coords = coords;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public void setCoords(double... coords) {
		this.coords = coords;
	}

	public double[] getCoords() {
		return this.coords;
	}

	public void setCoord(double coord, int dimension) {
		this.coords[dimension - 1] = coord;
	}

	public double getCoord(int dimension) {
		return this.coords[dimension - 1];
	}

	public int dimensions() {
		return coords.length;
	}

	/**
	 * Calculate the distance from this point to the other point. The default
	 * distanceCalculator is the Euclidean.
	 * 
	 * @param point
	 *            Point to calculate the distance to
	 * @return The distance calculated
	 */
	public double distanceTo(Point point) {
		DistanceCalculator calculator = new Euclidean();
		return calculator.calculateDistance(this, point);
	}

	/**
	 * Calculates the distance betwen two points using the passed distance
	 * calculator
	 * 
	 * @param point
	 *            Point to calculate the distance to
	 * @param calculator
	 *            The distance calculator instance
	 * @return
	 */
	public double distanceTo(Point point, DistanceCalculator calculator) {
		return calculator.calculateDistance(this, point);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("");
		for (double d : coords) {
			buffer.append(d + " ; ");
		}
		// remover o ultimo ;
		buffer.delete(buffer.lastIndexOf(";"), buffer.length());
		if (this.cluster != null) {
			buffer.append("; " + this.cluster.getId() + " ");
		} else {
			buffer.append("; {} ");
		}
		return buffer.toString();
	}

	/**
	 * Considera iguais dois pontos que tenham os mesmos valores em todas as
	 * dimensoes
	 */
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Point))
			return false;

		Point other = (Point) obj;
		boolean equal = true;
		if (this.coords.length != other.dimensions())
			return false;

		for (int i = 0; i < coords.length; i++) {
			equal = equal && (coords[i] == other.getCoord(i + 1));
		}
		return equal;
	}

	public List<Integer> listValidDimensions() {
		return IntStream.range(0, this.coords.length).filter(i -> this.coords[i] > 0.0d)
				.mapToObj(i -> Integer.valueOf(i)).collect(Collectors.toList());
	}

}