package br.ufms.facom.ma.dissertacao.kmeans;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PointTest {

	@Test
	public void testDistanceTo() {
		
		Point pointA = new Point("", 0.3,12, 16);
		Point pointB = new Point("", 0.4, 15, -4);
		double distanceAB = pointA.distanceTo(pointB);
		assertEquals(20.22, distanceAB, 0.009);
	}

}
