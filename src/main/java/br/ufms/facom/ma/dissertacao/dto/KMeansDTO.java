package br.ufms.facom.ma.dissertacao.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import br.ufms.facom.ma.dissertacao.kmeans.Point;

public class KMeansDTO {
	
	private Point [] data;
	private Integer clustersNumber;
	private Integer iterations;
	private String calculator;
	
	
	public Point[] getData() {
		return data;
	}
	public void setData(Point[] data) {
		this.data = data;
	}
	public Integer getClustersNumber() {
		return clustersNumber;
	}
	public void setClustersNumber(Integer clustersNumber) {
		this.clustersNumber = clustersNumber;
	}
	public Integer getIterations() {
		return iterations;
	}
	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}
	public String getCalculator() {
		return calculator;
	}
	public void setCalculator(String calculator) {
		this.calculator = calculator;
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this).
			       append("data", this.data).
			       append("clustersNumber", this.clustersNumber).
			       append("iterations", this.iterations).
			       append("calculator", this.calculator).
			       toString();
		
	}
}
