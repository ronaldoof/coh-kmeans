package br.ufms.facom.ma.dissertacao.dto;

public class OptimusKMeansDTO extends KMeansDTO {

	private Integer instances;

	public OptimusKMeansDTO(){
		super();
	}
	
	public Integer getInstances() {
		return instances;
	}

	public void setInstances(Integer instances) {
		this.instances = instances;
	}
	
	
	
}
