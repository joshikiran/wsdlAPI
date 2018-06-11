package com.dhdigital.commons.wsdl.api.commons;

/**
 * Each operation that is to be created in the WSDL API is represented as below.
 * 
 * @author joshi
 *
 */
public class Operations {
	String operationName;
	String requestElement;
	String responseElement;
	boolean override; 

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getRequestElement() {
		return requestElement;
	}

	public void setRequestElement(String requestElement) {
		this.requestElement = requestElement;
	}

	public String getResponseElement() {
		return responseElement;
	}

	public void setResponseElement(String responseElement) {
		this.responseElement = responseElement;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}
	
}
