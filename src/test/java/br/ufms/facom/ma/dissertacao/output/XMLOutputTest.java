package br.ufms.facom.ma.dissertacao.output;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufms.facom.ma.dissertacao.input.CSVInput;
import br.ufms.facom.ma.dissertacao.kmeans.COHKMeans;
import br.ufms.facom.ma.dissertacao.kmeans.Constraint;
import br.ufms.facom.ma.dissertacao.kmeans.HCluster;
import br.ufms.facom.ma.dissertacao.kmeans.MetaData;
import br.ufms.facom.ma.dissertacao.kmeans.Point;
import br.ufms.facom.ma.dissertacao.kmeans.Result;
import br.ufms.facom.ma.dissertacao.kmeans.distance.Cosine;

public class XMLOutputTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testOutputAsXML() {

		CSVInput input = new CSVInput();

		
		for (int i = 1; i <= 10; i++) {
			List<Point> data = input.inputData(
					new File("/Users/ronaldoflorence/Documents/Mestrado/Dissertacao/seguranca_publica/seguranca.csv"),
					true);
			List<Constraint> cons = input.inputConstraint(
					"/Users/ronaldoflorence/Documents/Mestrado/Dissertacao/seguranca_publica/restricoes-ml.csv");
			MetaData metaData = input.inputMetaData(
					new File("/Users/ronaldoflorence/Documents/Mestrado/Dissertacao/seguranca_publica/seguranca.csv"));
			List<String[]> geotag = input
					.inputGeotags("/Users/ronaldoflorence/Documents/Mestrado/Dissertacao/seguranca_publica/geotags.csv");
			
			COHKMeans kmeans = new COHKMeans();
			Result result = kmeans.cohKmeans(data, metaData, cons, geotag, 100, new Cosine(), 1000);
			String content = "/Users/ronaldoflorence/Documents/Mestrado/Dissertacao/seguranca_publica/coh-"+i+".xml";

			XMLOutput output = new XMLOutput();
			output.outputAsXML(content, metaData, (List<HCluster>) result.getClusters());
		}
	}

}
