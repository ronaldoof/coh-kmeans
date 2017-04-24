package br.ufms.facom.ma.dissertacao.kmeans;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.ufms.facom.ma.dissertacao.kmeans.Constraint.Type;
import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculator;
public class CopKMeans extends KMeans{

	public Result copKmeans(List<Point> data, List<Constraint> constraints, int clustersNumber, int iterations, DistanceCalculator calculator){
		
		List<Cluster> clusters = initClusters(data, clustersNumber);
		int i=1;
		// inicia as iteracoes
		while (true) {
			
			//limpa os clusters
			clusters.forEach(c -> c.clear());
			
			// associa cada ponto ao cluster mais perto
			assignClusters(data, clusters, constraints, calculator);
			
			//recalcula a posicao dos clusters
			boolean changed = repositionClusters(clusters);
			if(!changed || i==iterations){
				break;
			}
			i++;
		}
		
		return new Result(data,clusters, null);
	}
	
	public Result optimusCopKmeans(List<Point> data, List<Constraint> constraints, int clustersNumber, int iterations, DistanceCalculator calculator, int instances){
		Result bestResult = kmeans(data,clustersNumber, iterations, calculator );
		
		for(int i=0; i<=instances; i++){
			Result tmpResult = copKmeans(data, constraints,clustersNumber, iterations, calculator );
			if(tmpResult.getQuadraticError() < bestResult.getQuadraticError()){
				bestResult = tmpResult;
			}
		}
		return bestResult;
	}
	
	void assignClusters(List<Point> data, List<? extends Cluster> clusters, List<Constraint> constraints, DistanceCalculator calculator) {
		for (Point point : data) {
			assignClosestCluster(data, point, clusters, constraints, calculator);
		}
	}


	/**
	 * Verifica se existe uma violacao de constraint para esse ponto nesse cluster
	 * @param p Ponto a ser associado a um cluster
	 * @param c Cluster ao qual o ponto vai ser associado
	 * @param constraints Lista de restricoes (constraints)
	 * @return
	 */
	boolean violateConstraint(List<Point> data, Point p, Cluster c, List<Constraint> constraints){
		boolean violates = false;
		
		// filtra a colecao de constraints para todas as do tipo must_link e que possuem o ponto P 
		List<Constraint> mustLink =	constraints.stream().filter(co-> (co.getType()==Type.MUST_LINK && (co.contains(p)))).collect(Collectors.toList());
		
		// filtra a colecao de constraints para todas as do tipo cannot_link e que possuem o ponto p
		List<Constraint> cannotLink = constraints.stream().filter(co-> (co.getType()==Type.CANNOT_LINK && (co.contains(p)))).collect(Collectors.toList());;
		
		for (Constraint constraint : mustLink) {
			Point pair = findPointById(data, constraint.getPair(p.getId()));
			if(pair.getCluster()!= null && !pair.getCluster().equals(c)){
				violates = true;
				break;
			}
		}
		
		for (Constraint constraint : cannotLink) {
			Point pair = findPointById(data, constraint.getPair(p.getId()));
			
			if(pair.getCluster()!= null && pair.getCluster().equals(c)){
				violates = true;
				break;
			}
		} 
		return violates;
	}
	
	protected Point findPointById(List<Point> data, String id){
		Optional<Point> result = data.stream().filter(p-> p.getId().equals(id)).findFirst();
		if(result.isPresent())
			return result.get();
		return null;
	}
	
	 /**
     * Acha o cluster mais proximo do ponto e atribui o ponto a ele.
     * @param point Ponto a ser atribuido a um cluster
     * @param clusters Lista de clusters criados
     */
	void assignClosestCluster(List<Point> data, Point point, List<? extends Cluster> clusters, List<Constraint> constraints, DistanceCalculator calculator){
		double min = Double.MAX_VALUE;
		int nearestID = -1;
		for (int i = 0; i < clusters.size(); i++) {
			double distance = point.distanceTo(clusters.get(i).getCentroid(), calculator);
			if(distance < min && !violateConstraint(data, point, clusters.get(i), constraints)){
				min = distance;
				nearestID = i;
			}
		}
		if(nearestID != -1){
		  point.setCluster(clusters.get(nearestID));
		  clusters.get(nearestID).addPoint(point);
		}
	}
	
}
