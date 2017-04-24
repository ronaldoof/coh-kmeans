package br.ufms.facom.ma.dissertacao.kmeans;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ConstraintTest.class, KMeansTest.class, PointTest.class, CopKMeansTest.class, COHKMeansTest.class })
public class AllTests {

}
