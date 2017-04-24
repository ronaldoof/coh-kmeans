package br.ufms.facom.ma.dissertacao.input;

import java.io.File;
import java.util.List;

import br.ufms.facom.ma.dissertacao.kmeans.Point;

public interface IInput {
	
	public List<Point> input(File file);
	
	public List<Point> input(File content, boolean hasId);
	
}
