package br.ufms.facom.ma.dissertacao.kmeans;

import static br.ufms.facom.ma.dissertacao.kmeans.Constraint.Type.CANNOT_LINK;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufms.facom.ma.dissertacao.input.CSVInput;
import br.ufms.facom.ma.dissertacao.kmeans.Constraint.ConstraintBuilder;
import br.ufms.facom.ma.dissertacao.kmeans.HCluster.HClusterBuilder;
import br.ufms.facom.ma.dissertacao.kmeans.distance.Cosine;

public class COHKMeansTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testHCopKmeans() {
		CSVInput input = new CSVInput();
		List<Point> localData = input.inputData(new File("/Users/ronaldoflorence/Documents/Mestrado/Dissertacao/seguranca_publica/seguranca.csv"), true);
		List<Constraint> cons = input.inputConstraint("/Users/ronaldoflorence/Documents/Mestrado/Dissertacao/seguranca_publica/restricoes-ml.csv");
		MetaData metadata = input.inputMetaData(new File("/Users/ronaldoflorence/Documents/Mestrado/seguranca_publica/seguranca.csv"));
		List<String[]> geotag = input.inputGeotags("/Users/ronaldoflorence/Documents/Mestrado/seguranca_publica/seguranca.csv");
		COHKMeans kmeans = new COHKMeans();
		kmeans.cohKmeans(localData, metadata, cons, geotag, 100, new Cosine(), 1000);
//		
//		List<Point> localData = new ArrayList<Point>();
//
//		// build data array for test
//		localData.add(new Point("1",0.3, 10));
//		localData.add(new Point("2",-2, 9));
//		localData.add(new Point("3",-4, 4.5));
//		localData.add(new Point("4",2, 9.9));
//		localData.add(new Point("5",10, -2));
//		localData.add(new Point("6",0.8, 7.4));
//		localData.add(new Point("7",3, 4.9));
//		localData.add(new Point("8",2, 5.3));
//
//		List<Constraint> constraints = new ArrayList<Constraint>();
//		constraints.add(new Constraint(localData.get(1).getId(), localData.get(2).getId(), Type.CANNOT_LINK));
//		constraints.add(new Constraint("7","8", Type.MUST_LINK));
//		List<String> columns = new ArrayList<String>();
//		columns.add("a");
//		columns.add("b");
//		
//		MetaData metadata = new MetaData(columns);
//		COHKMeans kmeans = new COHKMeans();
//		Result result = kmeans.hCopKmeans(localData, metadata, constraints, 100, new Euclidean());
		//System.out.println(result.toString());
	}


	@Test
	public void testSearchClusters() {
		COHKMeans kmeans = new COHKMeans();
		
		List<HCluster> clusters = new ArrayList<HCluster>();
		clusters.add(new HClusterBuilder().id(0).centroid(1,5).point(2,4).point(3,5).build());
		clusters.add(new HClusterBuilder().id(1).centroid(2,4).point(3,3).point(6,3).build());
		clusters.add(new HClusterBuilder().id(2).centroid(3,3).point(5,5).point(2,7).build());
		clusters.add(new HClusterBuilder().id(3).centroid(4,2).point(2,4).point(1,7).build());

	
		HCluster result = kmeans.searchClusters(clusters, new Point("", 2,4));
		assertNotNull(result);

	    result = kmeans.searchClusters(clusters, new Point("",6,8));
		assertNull(result);

	}
	
	@Test
	public void testCanAssignToCluster(){
		COHKMeans kmeans = new COHKMeans();
		Cluster c = new HClusterBuilder().id(0).centroid(0,1).point("a",1,2).point("b",3,4).build();
		List<Constraint> cannots = new ArrayList<Constraint>();
		cannots.add(new ConstraintBuilder().type(CANNOT_LINK).pointA("b").pointB("c").build());
		Point p = new Point("d",5,6);
		boolean can = kmeans.canAddToCluster(c, p, cannots);
		
		assertTrue(can);
	}


}
