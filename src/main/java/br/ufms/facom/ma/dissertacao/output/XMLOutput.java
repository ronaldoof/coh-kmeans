package br.ufms.facom.ma.dissertacao.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.ufms.facom.ma.dissertacao.kmeans.HCluster;
import br.ufms.facom.ma.dissertacao.kmeans.MetaData;
import br.ufms.facom.ma.dissertacao.kmeans.Point;

public class XMLOutput {

	private static final Logger log = Logger.getLogger(XMLOutput.class);

	public void outputAsXML(String path, MetaData metaData, List<HCluster> clusters) {
		try {
			File file = new File(path);
			if (!file.exists())
				file.createNewFile();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			Element treeElement = doc.createElement("tree");
			doc.appendChild(treeElement);
			clusters.stream().forEach(c -> {
				Element node = buildNode(c, doc, metaData);
				treeElement.appendChild(node);
			});

			writeToFile(file, doc);
		} catch (IOException e) {
			log.error("Arquivo não encontrado.", e);
		} catch (ParserConfigurationException e) {
			log.error("Erro de configuracao no parser do xml.", e);
		}
	}

	private void writeToFile(File file, Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			FileWriter sw = new FileWriter(file);
			StreamResult sr = new StreamResult(sw);
			transformer.transform(domSource, sr);
		} catch (IllegalArgumentException | TransformerFactoryConfigurationError | IOException
				| TransformerException e) {
			log.error("Erro ao serializar o xml.", e);
		}

	}

	private Element buildNode(HCluster c, Document doc, MetaData metaData) {
		Element node = doc.createElement("node");
		Element id = doc.createElement("id");
		Element parent = doc.createElement("parent");
		Element topic = doc.createElement("topic");
		Element documents = doc.createElement("documents");
		Element descriptors = doc.createElement("descriptors");

		node.appendChild(id);
		node.appendChild(parent);
		node.appendChild(topic);
		node.appendChild(documents);
		node.appendChild(descriptors);

		id.setTextContent(String.valueOf(c.getId()));
		parent.setTextContent(String.valueOf(c.getFather().getId()));
		documents.setTextContent(buildDocumentsString(c.getPoints()));
		descriptors.setTextContent(buildDescriptor(c.getCentroid(), metaData));

		return node;
	}

	private String buildDocumentsString(List<Point> points) {
		if (points.isEmpty())
			return "[]";
		StringBuilder builder = new StringBuilder("[");
		for (Point point : points) {
			builder.append(point.getId());
			builder.append(", ");
		}
		builder.replace(builder.length() - 2, builder.length(), "]");

		return builder.toString();
	}

	private String buildDescriptor(Point p, MetaData metaData) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i = 1; i < p.getCoords().length; i++) {
			double coord = p.getCoord(i);
			if (coord > 0) {
				indexes.add(i);
			}
		}
		StringBuilder builder = new StringBuilder("[");
		for (Integer i : indexes) {
			builder.append(metaData.get(i - 1) + " [" + p.getCoord(i) + "], ");
		}
		String desc = builder.toString();
		if (desc.length()>1) {
			desc = desc.substring(0, desc.length() - 2);
			desc += "]";
		} else {
			desc = "[x [0.0]]";
		}
		return desc;
	}
}