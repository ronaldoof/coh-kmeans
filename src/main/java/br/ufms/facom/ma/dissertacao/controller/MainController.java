package br.ufms.facom.ma.dissertacao.controller;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.ufms.facom.ma.dissertacao.dto.CopKMeansDTO;
import br.ufms.facom.ma.dissertacao.dto.KMeansDTO;
import br.ufms.facom.ma.dissertacao.dto.OptimusKMeansDTO;
import br.ufms.facom.ma.dissertacao.kmeans.CopKMeans;
import br.ufms.facom.ma.dissertacao.kmeans.KMeans;
import br.ufms.facom.ma.dissertacao.kmeans.Result;
import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculator;
import br.ufms.facom.ma.dissertacao.kmeans.distance.DistanceCalculatorFactory;

@RestController
public class MainController {

	
	@RequestMapping(path="/kmeans",method=RequestMethod.POST)
    public ResponseEntity<Result> kmeans(@RequestBody KMeansDTO dto ) {
		// find out the distance calculator
		DistanceCalculator calculator = DistanceCalculatorFactory.fromString(dto.getCalculator());
		if(calculator==null){
			return new ResponseEntity<Result>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		KMeans kmeans = new KMeans();
		Result result = kmeans.kmeans(Arrays.asList(dto.getData()), dto.getClustersNumber(), dto.getIterations(), calculator);
		return new ResponseEntity<Result>(result, HttpStatus.OK);
    }
	
	@RequestMapping(path="/optimus",method=RequestMethod.POST)
    public ResponseEntity<Result> optimusKmeans(@RequestBody OptimusKMeansDTO dto ) {
		// find out the distance calculator
		DistanceCalculator calculator = DistanceCalculatorFactory.fromString(dto.getCalculator());
		if(calculator==null){
			return new ResponseEntity<Result>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		KMeans kmeans = new KMeans();
		Result result = kmeans.optimusKmeans(Arrays.asList(dto.getData()), dto.getClustersNumber(), dto.getIterations(), calculator, dto.getInstances());
		return new ResponseEntity<Result>(result, HttpStatus.OK);
    }
	
	@RequestMapping(path="/copkmeans",method=RequestMethod.POST)
    public ResponseEntity<Result> copKmeans(@RequestBody CopKMeansDTO dto ) {
		// find out the distance calculator
		DistanceCalculator calculator = DistanceCalculatorFactory.fromString(dto.getCalculator());
		if(calculator==null){
			return new ResponseEntity<Result>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		CopKMeans kmeans = new CopKMeans();
		Result result = kmeans.optimusCopKmeans(Arrays.asList(dto.getData()), dto.getConstraints(), dto.getClustersNumber(), dto.getIterations(), calculator, dto.getInstances());
		return new ResponseEntity<Result>(result, HttpStatus.OK);
    }
	
}
