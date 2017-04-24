package br.ufms.facom.ma.dissertacao.kmeans;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cluster {
	@JsonIgnore
	private List<Point> points;
	private Point centroid;
	private int id;
	private List<String> descriptors;

	private Cluster(){
		this.points = new ArrayList<Point>();
	}
	
	public Cluster(int id, Point centroid) {
		this.id = id;
		this.centroid = centroid;
		this.points = new ArrayList<Point>();
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addPoint(Point point) {
		this.points.add(point);
	}

	public List<String> getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(List<String> descriptors) {
		this.descriptors = descriptors;
	}

	public double quadraticError() {
		double error = 0.0d;
		for (Point point : points) {
			error += point.distanceTo(this.centroid);
		}
		return error;
	}

	public void clear() {
		this.points.forEach(p -> p.setCluster(null));
		this.points = new ArrayList<Point>();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Cluster - " + this.id + " ==========\n");
		buffer.append("Centroid ==== " + this.centroid.toString() + "\n");
		if (points != null && !points.isEmpty()) {
			for (Point point : points) {
				buffer.append(this.id + " " + point.toString() + "\n");
			}
		}
		buffer.append("Total Points on cluster: " + this.points.size());
		return buffer.toString();
	}
	
	public static class ClusterBuilder {
		private Cluster c;
		
		public ClusterBuilder(){
			this.c = new Cluster();
		}
		
		public ClusterBuilder id(int id){
			this.c.setId(id);
			return this;
		}
		
		public ClusterBuilder centroid(double... coords){
			this.c.setCentroid(new Point("", coords));
			return this;
		}
		
		public ClusterBuilder centroid(Point p){
			this.c.setCentroid(p);
			return this;
		}
		
		public ClusterBuilder point(double... coords){
			this.c.addPoint(new Point("", coords));
			return this;
		}
		
		public ClusterBuilder point(Point p){
			this.c.addPoint(p);
			return this;
		}

		public Cluster build(){
			return this.c;
		}
	}
}
