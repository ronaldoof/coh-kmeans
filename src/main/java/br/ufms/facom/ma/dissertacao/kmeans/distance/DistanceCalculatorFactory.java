package br.ufms.facom.ma.dissertacao.kmeans.distance;

public class DistanceCalculatorFactory {

	public static DistanceCalculator fromString(String name){
		if(name.equalsIgnoreCase("euclidean")){
			return new Euclidean();
		} else if(name.equalsIgnoreCase("cosine")){
			return new Cosine();
		}
		return null;
	}
	
}
