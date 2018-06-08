package com.dhdigital.commons.wsdl.config;

import java.util.Map;

/**
 * 
 * This is a sample WSDL response object which must be returned for all WSDL
 * operations. It is abstract as the low level code must create an instance of
 * itself rather than this object.
 * 
 * @author joshi
 *
 */
public abstract class WSDLResponseObject {

	protected String status;
	protected String statusMessage;
	protected Map<String, String> xsds;
	protected String wsdlFilePath;
	protected String defaultFolderLocationForWSDLs;
	protected boolean isValidXSD;
	protected boolean isValidWSDL;

}
