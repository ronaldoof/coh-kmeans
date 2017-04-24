package br.ufms.facom.ma.dissertacao.input;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import br.ufms.facom.ma.dissertacao.kmeans.Constraint;
import br.ufms.facom.ma.dissertacao.kmeans.MetaData;
import br.ufms.facom.ma.dissertacao.kmeans.Point;
import br.ufms.facom.ma.dissertacao.kmeans.Constraint.ConstraintBuilder;

public class CSVInput {

	private Logger log = Logger.getLogger(CSVInput.class);

	// private NumberFormat format =
	// NumberFormat.getCurrencyInstance(Locale.US);

	/**
	 * Pega um arquivo no formato csv e retorna como uma colecao de pontos.
	 */
	
	public List<Point> inputData(File file) {
		try {
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withDelimiter(';').parse(new FileReader(file));
			return parseContent(records, false);
		} catch (IOException e) {
			log.error("Arquivo de entrada não encontrado no caminho especificado.", e);
		}
		return null;
	}

	List<Point> parseContent(Iterable<CSVRecord> records, boolean hasId) {
		List<Point> data = new ArrayList<Point>();
		records.forEach(p -> {
			// Cria um novo ponto usando as coordenadas lidas no csv e adiciona
			// ele na lista de pontos
			if (hasId) {
				data.add(buildPointWithId(p));
			} else {
				data.add(buildPointWithoutId(p));
			}

		});
		return data;
	}

	Point buildPointWithId(CSVRecord record) {
		List<Double> coords = new ArrayList<Double>();
		int i = 0;
		String id = "";
		for (String field : record) {
			if (i == 0) {
				id = field;
				i++;
			} else
				coords.add(Double.valueOf(field).doubleValue());
		}
		return new Point(id, coords.stream().mapToDouble(d -> d).toArray());
	}

	Point buildPointWithoutId(CSVRecord p) {
		List<Double> coords = new ArrayList<Double>();
		p.forEach(c -> {
			coords.add(Double.valueOf(c).doubleValue());
		});
		// Cria um novo ponto usando as coordenadas lidas no csv e adiciona
		// ele na lista de pontos
		return new Point("", coords.stream().mapToDouble(d -> d).toArray());
	}


	public List<Point> inputData(File file, boolean hasId) {
		Iterable<CSVRecord> records;
		try {
			records = CSVFormat.RFC4180.withFirstRecordAsHeader().withDelimiter(';').parse(new FileReader(file));

			return parseContent(records, hasId);
		} catch (IOException e) {
			log.error("Conteudo de entrada mal formatado. Não foi possível realizar o Parsing.", e);
		}
		return null;
	}

	public MetaData inputMetaData(File file) {
		Map<String, Integer> header;
		try {
			header = CSVFormat.RFC4180.withFirstRecordAsHeader().withDelimiter(';').parse(new FileReader(file))
					.getHeaderMap();
			Map<Integer, String> columns = header.entrySet().stream()
					.collect(Collectors.toMap((entry) -> entry.getValue(), (entry) -> entry.getKey()));

			MetaData metaData = new MetaData(columns);
			return metaData;
		} catch (IOException e) {
			log.error("Ocorreu um erro ao ler as colunas.",e);
			return null;
		}

	}
	
	public List<Constraint> inputConstraint(String path){
		try {
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new FileReader(path));
			return parseConstraintContent(records);
		} catch (IOException e) {
			log.error("Arquivo de entrada não encontrado no caminho especificado.", e);
		}
		return null;
	}
	
	private List<Constraint> parseConstraintContent(Iterable<CSVRecord> records){
		
		List<Constraint> data = new ArrayList<Constraint>();
		records.forEach(p -> {
			if(p.size()<3){
				System.out.printf("Ocorreu um erro na restricao %s\n",p.toString());
			}
			data.add(new ConstraintBuilder().pointA(p.get(0)).pointB(p.get(1)).type(Integer.parseInt(p.get(2))).build());
		});
		return data;
		
	}
	
	public List<String[]> inputGeotags(String path){
		try {
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withDelimiter(';').parse(new FileReader(path));
			return parseGeotags(records);
		} catch (IOException e) {
			log.error("Arquivo de entrada não encontrado no caminho especificado.", e);
		}
		return null;
	}
	
	private List<String[]> parseGeotags(Iterable<CSVRecord> records){
		
		List<String[]> geotag = new ArrayList<String[]>();
		records.forEach(p -> {
			if(p.size()<3){
				System.out.printf("Ocorreu um erro na restricao %s\n",p.toString());
			}
			geotag.add(new String[]{p.get(0), p.get(1), p.get(2)});
		});
		return geotag;
		
	}
}
