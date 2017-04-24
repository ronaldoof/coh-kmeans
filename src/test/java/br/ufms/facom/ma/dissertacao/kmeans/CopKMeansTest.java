package br.ufms.facom.ma.dissertacao.kmeans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.ufms.facom.ma.dissertacao.kmeans.Constraint.Type;
import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculator;
import br.ufms.facom.ma.dissertacao.kmeans.distance.Euclidean;

public class CopKMeansTest {

	private List<Point> localData;
	private List<Constraint> constraints;
	CopKMeans cop = new CopKMeans();

	@Before
	public void setUp() throws Exception {
		this.localData = new ArrayList<Point>();

		// build data array for test
		localData.add(new Point("1",0.3, 10));
		localData.add(new Point("2",-2, 9));
		localData.add(new Point("3",-4, 4.5));
		localData.add(new Point("4",2, 9.9));
		localData.add(new Point("5",10, -2));
		localData.add(new Point("6",0.8, 7.4));
		localData.add(new Point("7",3, 4.9));
		localData.add(new Point("8",2, 5.3));

		constraints = new ArrayList<Constraint>();
		constraints.add(new Constraint(localData.get(1).getId(), localData.get(2).getId(), Type.CANNOT_LINK));
		constraints.add(new Constraint("7","8", Type.MUST_LINK));
	}

	@Test
	public void testCopKmeans() {
		Result result = cop.copKmeans(localData, this.constraints, 3, 100, new Euclidean());
		System.out.println(result.toString());
	}

	@Test
	public void testOptmusCopKmeans() {
		Result result = cop.optimusCopKmeans(localData, this.constraints, 3, 100, new Euclidean(),10);
		System.out.println(result.toString());
	}
	
	@Test
	public void testAssignClusters() {
		List<Cluster> clusters = cop.initClusters(localData, 4);
		cop.assignClusters(localData, clusters, this.constraints, new Euclidean());
		assertEquals(4, clusters.size());
	}

	@Test
	public void testViolateConstraint() {

		
		Cluster c = cop.initClusters(localData, 1).get(0);
		c.addPoint(localData.get(2));
		localData.get(2).setCluster(c);
		boolean violates = cop.violateConstraint(localData, localData.get(1), c, this.constraints);

		assertTrue(violates);
	}

	@Test
	public void testAssignClosestCluster() {
		DistanceCalculator calculator = new Euclidean();

		CopKMeans cop = new CopKMeans();
		List<Cluster> clusters = cop.initClusters(localData, 1);
		cop.assignClosestCluster(localData, localData.get(1), clusters, this.constraints, calculator);
		cop.assignClosestCluster(localData, localData.get(2), clusters, this.constraints, calculator);

		assertNotEquals(localData.get(2).getCluster(), localData.get(1).getCluster());

	}

}
