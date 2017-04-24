package br.ufms.facom.ma.dissertacao.dto;

import java.util.List;

import br.ufms.facom.ma.dissertacao.kmeans.Constraint;

public class CopKMeansDTO extends OptimusKMeansDTO {
	
	private List<Constraint> constraints;

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}
}
