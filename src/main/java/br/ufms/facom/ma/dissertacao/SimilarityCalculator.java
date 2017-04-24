package br.ufms.facom.ma.dissertacao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import br.ufms.facom.ma.dissertacao.input.CSVInput;
import br.ufms.facom.ma.dissertacao.kmeans.Constraint;
import br.ufms.facom.ma.dissertacao.kmeans.Point;
import br.ufms.facom.ma.dissertacao.kmeans.distance.Cosine;
import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculator;

public class SimilarityCalculator {

	private int sampleSize = 50; 
	private List<Point> data;
	private List<Constraint> cons;
	private List<String[]> result;
	private String output;
	
	
	public SimilarityCalculator(String input, String inputConstraints, String output){
		this.output = output;
		cons = new CSVInput().inputConstraint(new File(inputConstraints).getAbsolutePath());
		this.data = new CSVInput().inputData(new File(input),true);
		this.result = new ArrayList<String[]>();
	}
	
	double calculateSimilarity(Point a, Point b, boolean plusOne){
		DistanceCalculator calculator = new Cosine();
		if(!plusOne){
			return calculator.calculateDistance(a, b);
		}else {
			return 1+calculator.calculateDistance(a, b); 
		}
	}
	
	Constraint chooseRandomConstraint(){
		int randomNum = ThreadLocalRandom.current().nextInt(0, cons.size());
		return this.cons.get(randomNum);
	}
	
	Point chooseRandomPoint(){
		boolean isInConstraint = true;
		Point p = null;
		while(isInConstraint){
			int randomNum = ThreadLocalRandom.current().nextInt(0, this.data.size());
			p = this.data.get(randomNum);
			for(Constraint c : this.cons){
				if(c.contains(p)){
					isInConstraint = true;
					break;
				} else {
					isInConstraint = false;
				}
			}
		}
		return p;
	}
	
	protected Point findPointById(String id){
		return this.data.stream().filter(p-> p.getId().equals(id)).findFirst().orElse(null);
	}
	
	
	public void doCalc(){
		calcConstraint();
		calcPoint();
		save();
	}
	
	void calcConstraint(){
		for(int i=0; i<this.sampleSize;i++){
			Constraint c = this.chooseRandomConstraint();
			Point a = this.findPointById(c.getPointA());
			Point b = this.findPointById(c.getPointB());
			String[] record = new String[3];
			record[0] = c.getPointA();
			record[1] = c.getPointB();
			record[2] = String.valueOf(this.calculateSimilarity(a, b, false));
			this.result.add(record);
		}
	}
	
	void calcPoint(){
		for(int i=0; i<this.sampleSize;i++){
			
			Point a = this.chooseRandomPoint();
			Point b = this.chooseRandomPoint();
			if(a.getId().equals(b.getId())){
				b = this.chooseRandomPoint();
			}
			String[] record = new String[3];
			record[0] = a.getId();
			record[1] = b.getId();
			record[2] = String.valueOf(this.calculateSimilarity(a, b, true));
			this.result.add(record);
		}
	}

	void save() {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(this.output));
			StringBuilder sb = new StringBuilder();
			this.result.stream().forEachOrdered(r -> {
				sb.append(r[0]);
				sb.append(';');
				sb.append(r[1]);
				sb.append(';');
				sb.append(r[2]);
				sb.append('\n');
			});
			pw.write(sb.toString());
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		for(int i=6; i<=10; i++){
			SimilarityCalculator calc = new SimilarityCalculator(args[0], args[1], 
				"/Users/ronaldoflorence/Documents/Mestrado"
				+ "/Dissertacao/seguranca_publica/sa"+i+".csv");
			
			calc.doCalc();
		}
	}
	
}
