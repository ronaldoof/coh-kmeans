package br.ufms.facom.ma.dissertacao.kmeans;

public class HCluster extends Cluster {

	private Cluster father;

	private HCluster(){
		super(0,null);
		this.father = null;
	}
	
	public HCluster(int id, Point centroid) {
		super(id, centroid);
		this.father = this;
	}

	public HCluster(int id, Point centroid, Cluster father) {
		super(id, centroid);
		this.father = father;
	}

	public Cluster getFather() {
		return father;
	}

	public void setFather(Cluster father) {
		this.father = father;
	}
	
	public int getHeight(){
		HCluster actCluster = this;
		int height = 1;
		while(actCluster.father!=null && actCluster.father!=actCluster){
			actCluster = (HCluster) actCluster.father;
			height++;
		}
		return height;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("");
		for (Point point : super.getPoints()) {
			buffer.append(point.toString()+"; "+this.father.getId()+"\n");
		}
		return buffer.toString();
	}

	public static class HClusterBuilder {
        
		private HCluster c;
		
		public HClusterBuilder(){
			this.c = new HCluster();
		}
		
		public HClusterBuilder father(Cluster father){
			this.c.father = father;
			return this;
		}
		
		public HCluster build(){
			return this.c;
		}
		
		public HClusterBuilder id(int id){
			this.c.setId(id);
			return this;
		}
		
		public HClusterBuilder centroid(double... coords){
			this.c.setCentroid(new Point("centroid_"+this.c.getId(), coords));
			return this;
		}
		
		public HClusterBuilder centroid(Point p){
			this.c.setCentroid(p);
			return this;
		}
		
		public HClusterBuilder point(double... coords){
			this.c.addPoint(new Point("centroid_"+this.c.getId(), coords));
			return this;
		}
		
		public HClusterBuilder point(String id, double... coords){
			this.c.addPoint(new Point(id, coords));
			return this;
		}
		
		public HClusterBuilder point(Point p){
			this.c.addPoint(p);
			return this;
		}
		
	}

}
