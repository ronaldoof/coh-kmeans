package br.ufms.facom.ma.dissertacao.kmeans;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Result {

	
	private List<Point> data;
	
	@JsonIgnore
	private List<? extends Cluster> clusters;
	
	@JsonIgnore
	private MetaData metaData;
	
	public Result() {
		this.data = new ArrayList<Point>();
		this.clusters = new ArrayList<Cluster>();
	}
	
	public Result(List<Point> data, List<? extends Cluster> clusters, MetaData metaData){
		this.data = data;
		this.clusters = clusters;
		this.metaData = metaData;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public List<Point> getData() {
		return data;
	}

	public void setData(List<Point> data) {
		this.data = data;
	}

	public List<? extends Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(List<Cluster> clusters) {
		this.clusters = clusters;
	}

	/**
	 * Retorna o erro quadratico de um resultado do agrupamento kmeans.
	 * @return
	 */
	public double getQuadraticError() {
		double error = 0.0d;
		for (Cluster cluster : clusters) {
			error += cluster.quadraticError();
		}
		return error;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("");
		for (Point point : data) {
			buffer.append(point.toString()+"\n");
		}
		return buffer.toString();
	}
}
