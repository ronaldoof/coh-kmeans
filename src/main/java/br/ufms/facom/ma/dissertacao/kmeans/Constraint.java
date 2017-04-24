package br.ufms.facom.ma.dissertacao.kmeans;

public class Constraint {

	private String pointA;
	private String pointB;
	private Type type;
	
	public static enum Type { 

		MUST_LINK, CANNOT_LINK;
		
	}

	public Constraint(){
		
	}
	
	public Constraint(String a, String b, Type type) {
		super();
		this.pointA = a;
		this.pointB = b;
		this.type = type;
	}

	public String getPointA() {
		return pointA;
	}

	public void setPointA(String pointA) {
		this.pointA = pointA;
	}

	public String getPointB() {
		return pointB;
	}

	public void setPointB(String pointB) {
		this.pointB = pointB;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	};
	
	/**
	 * Verifica se a constraint possui o ponto
	 * @param p
	 * @return
	 */
	public boolean contains(Point p){
		return p.equals(this.pointA) || p.equals(this.pointB);
	}
	
	/**
	 * Retorna o par de um ponto nessa constraint
	 * @param p
	 * @return
	 */
	public String getPair(String p){
		if(p.equals(this.pointA)) return this.pointB;
		if(p.equals(this.pointB)) return this.pointA;
		return null;
	}
	
	public static class ConstraintBuilder {
		private Constraint c;
		
		public ConstraintBuilder (){
			this.c = new Constraint();
		}
		
		public ConstraintBuilder pointA(String a){
			this.c.setPointA(a);
			return this;
		}

		public ConstraintBuilder pointB(String b){
			this.c.setPointB(b);
			return this;
		}
		
		public ConstraintBuilder type(Type type){
			this.c.setType(type);
			return this;
		}

		public ConstraintBuilder type(int i){
			if(i==1){
				this.c.setType(Type.MUST_LINK);
			} else {
				this.c.setType(Type.CANNOT_LINK);
			}
			return this;
		}
		
		public Constraint build(){
			return this.c;
		}
	}
	
}
