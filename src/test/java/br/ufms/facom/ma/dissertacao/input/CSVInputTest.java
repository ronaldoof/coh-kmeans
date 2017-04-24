package br.ufms.facom.ma.dissertacao.input;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufms.facom.ma.dissertacao.kmeans.Point;

public class CSVInputTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testInputFile() throws URISyntaxException {
		File file = new File(getClass().getResource("/data.csv").getFile());
		List<Point> data = new CSVInput().inputData(file);
//		data.forEach(p->{
//			System.out.println(p);
//		});
		assertEquals(149, data.size());

	}

	@Test
	public void testInputString() throws IOException, URISyntaxException {
		//String content = new String(Files.readAllBytes(Paths.get(getClass().getResource("/data.csv").toURI())));
		List<Point> data = new CSVInput().inputData(Paths.get(getClass().getResource("/data.csv").toURI()).toFile(), true);
		assertEquals(149,data.size());
	}

}
