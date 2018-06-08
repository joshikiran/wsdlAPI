package com.dhdigital.commons.wsdl.membrane;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dhdigital.commons.wsdl.config.WSDLConfiguration;
import com.predic8.schema.SchemaParser;

/**
 * All methods pertaining to WSDL Schema are to be kept here.
 * 
 * @author joshi
 *
 */
public class Schema {

	/**
	 * 
	 * This would basically validate all XSDs provided in the configuration and
	 * maintains a key value pair of prefix along with associated Schemas.
	 * 
	 * @param config
	 * @return
	 */
	public Map<String, com.predic8.schema.Schema> validateXSDs(WSDLConfiguration config) {
		Map<String, com.predic8.schema.Schema> schemas = new HashMap<String, com.predic8.schema.Schema>();
		SchemaParser parser = new SchemaParser();
		Set<String> keySet = config.getXsds().keySet();
		for (String xsd : keySet) {
			com.predic8.schema.Schema schema = parser
					.parse(config.defaultFolderLocationForWSDLs + File.separatorChar + config.getXsds().get(xsd));
			schemas.put(xsd, schema);
		}
		return schemas;
	}

}
