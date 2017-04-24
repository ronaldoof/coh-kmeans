package br.ufms.facom.ma.dissertacao.kmeans;

import static org.junit.Assert.*;

import org.junit.Test;

import br.ufms.facom.ma.dissertacao.kmeans.Constraint.Type;

public class ConstraintTest {

	@Test
	public void testContains() {
		Point a = new Point("a", 0.2d, 0.3d);
		Point b = new Point("b",1d, 2d);
		
		Constraint c = new Constraint (a.getId(),b.getId(),Type.MUST_LINK);
		
		assertTrue(c.contains(a));
	}

	@Test
	public void testGetPair() {
		Point a = new Point("a",0.2d, 0.3d);
		Point b = new Point("b",1d, 2d);
		
		Constraint c = new Constraint (a.getId(),b.getId(),Type.MUST_LINK);
		
		assertEquals(b.getId(), c.getPair(a.getId()));
	}
}
