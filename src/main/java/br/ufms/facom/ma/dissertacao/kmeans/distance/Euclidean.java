package br.ufms.facom.ma.dissertacao.kmeans.distance;

import br.ufms.facom.ma.dissertacao.kmeans.Point;

public class Euclidean implements DistanceCalculator {

	@Override
	public double calculateDistance(Point a, Point b) {
		double sum = 0.0d;
		for (int i=1; i<= a.dimensions(); i++) {
			sum += Math.pow(b.getCoord(i) - a.getCoord(i),2);			
		}
		return Math.sqrt(sum);
	}

}
