package br.ufms.facom.ma.dissertacao.kmeans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetaData {

	private Map<Integer, String> columns = new HashMap<Integer, String>();

	public MetaData(List<String> columnsNames) {
		int i = 0;
		for (String name : columnsNames) {
			columns.put(i, name);
		}
	}

	public MetaData(Map<Integer, String> columns) {
		this.columns = columns;
	}

	public String get(Integer i) {
		return this.columns.get(i);
	}

	public void put(Integer i, String name) {
		this.columns.put(i, name);
	}

	public List<String> getColumnsNames(List<Integer> indexes) {
		return this.columns.entrySet().stream().filter((entry) -> indexes.contains(entry.getKey()))
				.map((entry) -> entry.getValue()).collect(Collectors.toList());

	}
	
}
