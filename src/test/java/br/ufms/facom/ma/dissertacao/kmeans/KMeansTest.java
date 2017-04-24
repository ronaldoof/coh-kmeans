package br.ufms.facom.ma.dissertacao.kmeans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.ufms.facom.ma.dissertacao.input.CSVInput;
import br.ufms.facom.ma.dissertacao.kmeans.distance.Euclidean;

public class KMeansTest {

	private List<Point> data = new ArrayList<Point>(10);
	private KMeans kmeans = new KMeans();

	@Before
	public void setUp() throws Exception {
		File file = new File(getClass().getResource("/data.csv").getFile());
		data = new CSVInput().inputData(file);
	}

	@Test
	public void testFindUpperLimit() {
//		double input[] = new double[] { 0.2, 13, -0.6, -10 };
//
//		double upperLimit = kmeans.findUpperLimit(input);
//		assertEquals(13, upperLimit, 0);
	}

	@Test
	public void testFindLowerLimit() {
//		double input[] = new double[] { 0.2, -0.6, -10.0, 13 };
//
//		double lowerLimit = kmeans.findLowerLimit(input);
//		assertEquals(-10, lowerLimit, 0);
	}


	@Test
	public void testBuildCentroid() {
		Point centroid = kmeans.buildCentroid(data, 2);
		assertNotNull(centroid);
	}

	@Test
	public void testCreateRandomCentroids() {
		List<Point> centroids = kmeans.createRandomCentroids(data, 5);

		assertEquals(5, centroids.size());
	}

	@Test
	public void testInitClusters() {
		List<Cluster> clusters = kmeans.initClusters(data, 3);
		// kmeans.plotClusters(clusters);
		assertEquals(3, clusters.size());
	}

	@Test
	public void testAssignClusters() {
		List<Cluster> clusters = kmeans.initClusters(data, 4);
		kmeans.assignClusters(data, clusters, new Euclidean());
		// kmeans.plotClusters(clusters);
		assertEquals(4, clusters.size());
	}

	@Test
	public void testRepositionClusters() {
		List<Cluster> clusters = kmeans.initClusters(data, 3);
		kmeans.assignClusters(data, clusters, new Euclidean());
		// kmeans.plotClusters(clusters);
		kmeans.repositionClusters(clusters);
		// kmeans.plotClusters(clusters);
		assertEquals(3, clusters.size());
	}

	@Test
	public void testKMeans() {
		Result result = kmeans.kmeans(data, 3, 100, new Euclidean());
		System.out.println(result.toString());
	}

	@Test
	public void testOptimusKmeans() {
		Result result = kmeans.optimusKmeans(data, 3, 100, new Euclidean(), 10);
		System.out.println(result.toString());
	}
}
