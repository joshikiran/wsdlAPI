package com.dhdigital.commons.wsdl.api;

import java.util.ArrayList;
import java.util.List;

import com.dhdigital.commons.wsdl.api.commons.Operations;
import com.dhdigital.commons.wsdl.config.BOConfiguration;
import com.dhdigital.commons.wsdl.membrane.MembraneAPI;

/**
 * 
 * This is a sample main class which depicts how to call the MembraneAPI
 * specifically for IBM BPM using BOs.
 * 
 * Create as many as operations first using request and response XSDs and
 * configure other things to be able to either cretae or modify the WSDL.
 * 
 * @author joshi
 *
 */
public class BOAPI {
	public static void main(String args[]) {
		BOConfiguration config = new BOConfiguration();
		List<Operations> operations = new ArrayList<Operations>();
		config.createNewOperation("GetCustomerRisk", "CustomerRiskRequest.xsd", "CustomerRiskResponse.xsd", false);
		config.createNewOperation("CreateMFPortfolio", "MFPortfolioRequest.xsd", "MFPortfolioResponse.xsd", false);
		config.createNewOperation("AddMFPortfolioCurrency", "AddMFPortfolioCurrencyRequest.xsd", "AddMFPortfolioCurrencyResponse.xsd", true);
		operations  = config.getOperations();
		
		config.setOperations(operations);
		config.setXsds(config.getXsdInformation());
		config.setDefaultFolderLocationForWSDLs("/Users/joshi/Documents/Joshi/Office/Projects/DH/WSDLAPI/useCases");
		config.setWsdlName("TradexService");
		config.setTargetNamespace("http://corp.dhdigital.com/middlewareservices/tradexservice/1.0/");

		MembraneAPI membObj = new MembraneAPI(MembraneAPI.getMembraneAPIBuilder());
		membObj = membObj.generateWSDL(config);
		System.out.println(membObj.toString());
	}
}
