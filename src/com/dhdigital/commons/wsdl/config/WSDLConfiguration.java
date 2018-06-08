package com.dhdigital.commons.wsdl.config;

import java.util.List;
import java.util.Map;

import com.dhdigital.commons.wsdl.api.commons.Operations;

/**
 * Configuration object which must be used by underlining API to perform WSDL
 * operations like CreatingWSDL from an existing XSD or validating WSDL.
 * 
 * @author joshi
 *
 */
public class WSDLConfiguration {

	public Map<String, String> xsds;
	public String wsdlFilePath;
	public String defaultFolderLocationForWSDLs;
	public List<Operations> operations;
	public String wsdlName;
	public String targetNamespace;

	public Map<String, String> getXsds() {
		return xsds;
	}

	public void setXsds(Map<String, String> xsds) {
		this.xsds = xsds;
	}

	public String getWsdlFilePath() {
		return wsdlFilePath;
	}

	public void setWsdlFilePath(String wsdlFilePath) {
		this.wsdlFilePath = wsdlFilePath;
	}

	public String getDefaultFolderLocationForWSDLs() {
		return defaultFolderLocationForWSDLs;
	}

	public void setDefaultFolderLocationForWSDLs(String defaultFolderLocationForWSDLs) {
		this.defaultFolderLocationForWSDLs = defaultFolderLocationForWSDLs;
	}

	public List<Operations> getOperations() {
		return operations;
	}

	public void setOperations(List<Operations> operations) {
		this.operations = operations;
	}

	public String getWsdlName() {
		return wsdlName;
	}

	public void setWsdlName(String wsdlName) {
		this.wsdlName = wsdlName;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

}
