package br.ufms.facom.ma.dissertacao.kmeans.distance;

import br.ufms.facom.ma.dissertacao.kmeans.Point;

public interface DistanceCalculator {

	public double calculateDistance(Point a, Point b);
	
}
