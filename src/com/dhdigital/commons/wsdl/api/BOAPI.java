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
		Operations op = config.createNewOperation("GetFundInformation", "TradexServices.xsd", "TradexServices.xsd", true);
		operations.add(op);
		op = config.createNewOperation("GetMFPortfolioList", "TradexServices.xsd", "TradexServices.xsd", true);
		operations.add(op);
		config.setOperations(operations);
		config.setXsds(config.getXsdInformation());
		config.setDefaultFolderLocationForWSDLs("/Users/joshi/Documents/Joshi/Office/Projects/DH/WSDLAPI");
		config.setWsdlName("First");
		config.setTargetNamespace("http://predic8.com/wsdl/AddService/1/");

		MembraneAPI membObj = new MembraneAPI(MembraneAPI.getMembraneAPIBuilder());
		membObj = membObj.generateWSDL(config);
		System.out.println(membObj.toString());
	}
}
