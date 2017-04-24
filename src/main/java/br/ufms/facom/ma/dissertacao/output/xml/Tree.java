package br.ufms.facom.ma.dissertacao.output.xml;

import java.util.ArrayList;
import java.util.List;

public class Tree {

	private List<Node> nodes;

	public Tree(){
		this.nodes = new ArrayList<Node>();
	}
	
	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	public void addNode(Node node){
		this.nodes.add(node);
	}
	
}
