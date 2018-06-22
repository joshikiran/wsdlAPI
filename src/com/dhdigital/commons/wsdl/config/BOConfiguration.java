package com.dhdigital.commons.wsdl.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dhdigital.commons.wsdl.api.commons.Operations;

/**
 * This class would be specific to IBM BPM to provide minimal configuration to
 * be exposed to the actual process of creating/modifying a WSDL.
 * 
 * @author joshi
 *
 */
public class BOConfiguration extends WSDLConfiguration {

	private static Set<String> xsds = new HashSet<>();
	private static List<Operations> operations = new ArrayList<Operations>();

	/**
	 * This would create a new Operations object along with request and response
	 * elements set. What this would do is capture xsds specific to request and
	 * response. These xsds are captured in set so that they are not duplicated.
	 * 
	 * @param operationName
	 * @param requestXSD
	 * @param responseXSD
	 * @return
	 */
	public void createNewOperation(String operationName, String requestXSD, String responseXSD, boolean override) {
		Operations op = null;
		if (override) {
			op = new Operations();
			op.setOperationName(operationName);
			op.setOverride(override);
			xsds.add(requestXSD);
			xsds.add(responseXSD);

			String element = requestXSD.replace(".xsd", "");
			op.setRequestElement(element);

			element = responseXSD.replace(".xsd", "");
			op.setResponseElement(element);
			operations.add(op);
		}
	}

	/**
	 * All the operations which are to be either created/modified are to be returned
	 * 
	 */
	public List<Operations> getOperations() {
		return operations;
	}

	/**
	 * All captured xsds from the above method would be responded from this method.
	 * The field would be specific to one instance of BOConfiguration and hence all
	 * operations must be created first in order to access this variable.
	 * 
	 * @return
	 */
	public Map<String, String> getXsdInformation() {
		Map<String, String> xsdInfo = new HashMap<String, String>();
		int i = 1;
		for (String xsd : xsds) {
			xsdInfo.put("q" + i++, xsd);
		}
		return xsdInfo;
	}

}
