package br.ufms.facom.ma.dissertacao.util;

public class Sequence {

	private static Sequence sequence;
	
	private static int seed = 0;
	
	private Sequence(){
	}
	
	public static Sequence getSequence(){
		if(sequence == null){
			sequence = new Sequence();
		}
		return sequence;
	}
	
	public int next(){
		return seed++;
	}
	
}
