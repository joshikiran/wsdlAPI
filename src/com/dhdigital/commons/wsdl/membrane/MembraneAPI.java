package com.dhdigital.commons.wsdl.membrane;

import java.io.File;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dhdigital.commons.wsdl.api.commons.Operations;
import com.dhdigital.commons.wsdl.config.WSDLConfiguration;
import com.dhdigital.commons.wsdl.config.WSDLResponseObject;
import com.dhdigital.commons.wsdl.interfaces.WSDLOperations;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;

/**
 * MembraneAPI which explains full implementation of how to generate a WSDL.
 * This would implement basically WSDLOperations so that all necessary
 * operations are to be implemented.
 * 
 * @author joshi
 *
 */
public class MembraneAPI extends WSDLResponseObject implements WSDLOperations {

	private static MembraneAPIBuilder builder = null;

	public MembraneAPI(MembraneAPIBuilder builder) {
		this.status = builder.status;
		this.statusMessage = builder.statusMessage;
		this.isValidXSD = builder.isValidXSD;
		this.isValidWSDL = builder.isValidWSDL;
		this.defaultFolderLocationForWSDLs = builder.defaultFolderLocationForWSDLs;
		this.wsdlFilePath = builder.wsdlFilePath;
		this.xsds = builder.xsds;
	}

	@Override
	public String toString() {
		return this.status + " : " + this.statusMessage + " : " + this.isValidXSD;
	}

	public static MembraneAPIBuilder getMembraneAPIBuilder() {
		if (null == builder) {
			builder = new MembraneAPIBuilder(Status.getStatus(Status.FAILED));
		}
		return builder;
	}

	private static Logger logger = LoggerFactory.getLogger(MembraneAPI.class);

	/**
	 * Enums to capture the status of WSDL operation (either creation/Modification)
	 * 
	 * @author joshi
	 *
	 */
	private enum Status {
		FAILED("FAILED"), SUCCESS("SUCCESS");

		private static Map<String, String> statusMap = EnumSet.allOf(Status.class).stream()
				.collect(Collectors.toMap(Status::name, e -> e.getStatus()));

		private String status;

		private String getStatus() {
			return status;
		}

		private Status(String status) {
			this.status = status;
		}

		public static String getStatus(Status status) {
			return Optional.ofNullable(statusMap.get(status.getStatus())).orElse(null);
		}

	}

	public String getStatus() {
		return this.status;
	}

	/**
	 * This implemented method would be basically reasonable to generate WSDL in the
	 * file system. It basically takes care of either creating/modifying by calling
	 * low level methods accordingly. It starts with validating the configuration
	 * initially along with validating XSDs present and there by generating WSDL or
	 * modifying existing WSDL by adding additional operations. Modifying operation
	 * currently is not in the scope of this API, it would be released in subsequent
	 * API releases.
	 * 
	 */
	@Override
	public MembraneAPI generateWSDL(WSDLConfiguration config) {
		// TODO Auto-generated method stub
		Map<String, Schema> schema = null;
		Definitions wsdl = null;
		WSDL memObj = null;
		com.dhdigital.commons.wsdl.membrane.Schema sc = null;
		// Setting initial status to "FAILED" status
		MembraneAPIBuilder wsdlObj = new MembraneAPI.MembraneAPIBuilder(Status.getStatus(Status.FAILED));
		logger.debug("Generating WSDL for configuration Object {}", config);
		try {
			wsdlObj.setStatusMessage("Validating the configuration object");
			validateConfiguration(config);
			wsdlObj.setStatusMessage("Validating the XSD provided in config");
			sc = new com.dhdigital.commons.wsdl.membrane.Schema();
			schema = sc.validateXSDs(config);
			wsdlObj.isValidXSD(true);
			wsdlObj.setStatusMessage("Creating WSDL Object using the schema provided");
			memObj = new WSDL();
			wsdl = memObj.generateWSDLUsingMembrane(schema, config);
			wsdlObj.setWSDLPath(memObj.writeWSDLtoFile(wsdl, config));
			wsdlObj.isValidWSDL(true);
			wsdlObj.setStatus(Status.getStatus(Status.SUCCESS));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception while generating WSDL {}", e);
			wsdlObj.setStatusMessage(e.getMessage());
		}
		return wsdlObj.build();
	}

	private static class MembraneAPIBuilder extends WSDLConfiguration {
		private String status;
		private String statusMessage;
		private boolean isValidXSD;
		private boolean isValidWSDL;

		private MembraneAPIBuilder setStatus(String status) {
			this.status = status;
			return this;
		}

		private MembraneAPIBuilder isValidXSD(boolean isValidXSD) {
			this.isValidXSD = isValidXSD;
			return this;
		}

		private MembraneAPIBuilder isValidWSDL(boolean isValidWSDL) {
			this.isValidWSDL = isValidWSDL;
			return this;
		}

		private MembraneAPIBuilder setWSDLPath(String wsdlPath) {
			this.wsdlFilePath = wsdlPath;
			return this;
		}

		MembraneAPIBuilder(String status) {
			this.status = status;
		}

		public MembraneAPIBuilder setStatusMessage(String statusMessage) {
			logger.debug("Status Message is : {}", statusMessage);
			this.statusMessage = statusMessage;
			return this;
		}

		public MembraneAPI build() {
			MembraneAPI apiObj = new MembraneAPI(this);
			return apiObj;

		}
	}

	/**
	 * This would check the configuration provided for this API Implementation. All
	 * basic checks which are necessary for this implementation should be done
	 * specifically here.
	 * 
	 */
	@Override
	public void validateConfiguration(WSDLConfiguration config) {
		// TODO Auto-generated method stub
		if (config == null)
			throw new RuntimeException("Please provide the configuration Object");
		if (null == config.defaultFolderLocationForWSDLs || "".equals(config.defaultFolderLocationForWSDLs))
			throw new RuntimeException("Please provide default folder locations where XSD is present");
		File folder = new File(config.defaultFolderLocationForWSDLs);
		if (!folder.isDirectory())
			throw new RuntimeException(
					"The name provided by you in defaultFolderLocationForWSDLs is not actually a folder");
		if (null == config.xsds || config.xsds.size() == 0)
			throw new RuntimeException("Please provide atlease one xsd which must be used");

		if (null == config.getWsdlName() || "".equals(config.getWsdlName()))
			throw new RuntimeException("Please provide WSDL Service name to generate a WSDL");
		if (null == config.getTargetNamespace() || "".equals(config.getTargetNamespace()))
			throw new RuntimeException("Please provide the namespace to be used for WSDL definitions");

		if (null == config.getOperations() || config.getOperations().size() == 0)
			throw new RuntimeException("Please add atleast one operation");

		{
			// Validate all operations
			for (Operations op : config.getOperations()) {
				if (null == op.getOperationName() || "".equals(op.getOperationName()))
					throw new RuntimeException("Operation Name cannot be empty. Please correct it.");

				if (null == op.getRequestElement() || "".equals(op.getRequestElement()))
					throw new RuntimeException("Request Element of the operation cannot be empty");

				if (null == op.getResponseElement() || "".equals(op.getResponseElement()))
					throw new RuntimeException("Response Element of the operation cannot be empty");
			}
		}

		{
			// Validating the WSDL if present
			String fullWsdlPath = config.getDefaultFolderLocationForWSDLs() + File.separatorChar + config.getWsdlName()
					+ ".wsdl";
			File file = new File(fullWsdlPath);
			if (file.exists()) {
				WSDLParser parser = new WSDLParser();
				parser.parse(fullWsdlPath);
				logger.debug("Validating the WSDL and the WSDL provided is valid.");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends WSDLResponseObject> T validateWSDL(T resObj) {
		// TODO Auto-generated method stub
		MembraneAPI obj = (MembraneAPI) resObj;

		if (null == obj.wsdlFilePath || "".equals(obj.wsdlFilePath))
			throw new RuntimeException("WSDL File is not generated or the path mentioned is not valid");

		WSDLParser parser = new WSDLParser();
		parser.parse(obj.wsdlFilePath);

		return (T) obj;
	}

}
