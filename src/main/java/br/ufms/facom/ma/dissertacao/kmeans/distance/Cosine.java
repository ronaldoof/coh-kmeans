package br.ufms.facom.ma.dissertacao.kmeans.distance;

import br.ufms.facom.ma.dissertacao.kmeans.Point;

public class Cosine implements DistanceCalculator {

	@Override
	public double calculateDistance(Point a, Point b) {
		double normA = euclideanNorm(a);
		double normB = euclideanNorm(b);
		
		double dotProduct = dotProduct(a,b);
		
		double cosine = dotProduct / (normA*normB);
		
		return 1-cosine;
	}

	double dotProduct(Point a, Point b) {
		double dotProduct = 0.0d;
		for (int i = 1; i <= a.dimensions(); i++) {
			dotProduct += a.getCoord(i) * b.getCoord(i);
		}
		return dotProduct;
	}

	double euclideanNorm(Point point) {
		double norm = 0.0d;
		for (int i = 1; i <= point.dimensions(); i++) {
			norm += Math.pow(point.getCoord(i), 2);
		}
		return Math.sqrt(norm);
	}
}
