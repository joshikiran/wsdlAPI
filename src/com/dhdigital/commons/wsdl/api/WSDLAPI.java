package com.dhdigital.commons.wsdl.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dhdigital.commons.wsdl.api.commons.Operations;
import com.dhdigital.commons.wsdl.config.WSDLConfiguration;
import com.dhdigital.commons.wsdl.membrane.MembraneAPI;

/**
 * 
 * This is one other way of calling the MembraneAPI where in you would be adding
 * specific element from XSD rather than considering XSD name as the element
 * name. This also supports calling a specific element of specific XSD using
 * named value pairs.
 * 
 * @author joshi
 *
 */
public class WSDLAPI {

	public static void main(String args[]) {

		WSDLConfiguration config = new WSDLConfiguration();
		Map<String, String> xsds = new HashMap<String, String>();
		List<Operations> operations = new ArrayList<Operations>();
		Operations op = new Operations();
		op.setOperationName("FundInformation");
		op.setRequestElement("CaronteServices");
		op.setResponseElement("TradexServices");
		operations.add(op);

		op = new Operations();
		op.setOperationName("MFPortfolioList");
		op.setRequestElement("CaronteServices");
		op.setResponseElement("TradexServices");

		operations.add(op);
		config.setOperations(operations);
		config.setDefaultFolderLocationForWSDLs("/Users/joshi/Documents/Joshi/Office/Projects/DH/WSDLAPI");

		xsds.put("q1", "CaronteServices.xsd");
		xsds.put("q2", "TradexServices.xsd");
		config.setXsds(xsds);

		config.setWsdlName("SampleBO1");
		config.setTargetNamespace("http://predic8.com/wsdl/AddService/1/");

		MembraneAPI membObj = new MembraneAPI(MembraneAPI.getMembraneAPIBuilder());
		membObj = membObj.generateWSDL(config);
		System.out.println(membObj.toString());
	}
}
