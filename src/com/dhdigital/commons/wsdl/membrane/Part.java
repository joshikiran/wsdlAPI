package com.dhdigital.commons.wsdl.membrane;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.predic8.schema.Element;
import com.predic8.schema.Schema;

/**
 * 
 * All methods pertaining to WSDL Parts are implemented here.
 * 
 * @author joshi
 *
 */
public class Part {

	private static Logger logger = LoggerFactory.getLogger(Part.class);

	/**
	 * 
	 * This method would basically either create/modify part information for WSDL
	 * that is to be either modified or created. A part is associated with an
	 * element, this would be identified in two ways one with prefixes and one
	 * without. If a message (either request/response) is provided with prefix, it
	 * basically means, API has been told specifically to search for the element in
	 * a particular schema. This would be basically helpful if there are chances of
	 * having same name for elements in multiple schemas. But if that is not the
	 * case, API can be told the element names without prefixes, in which case it
	 * would try to find out particular element in all possible schemas available
	 * for the WSDL. This method would also take care of updating existing part
	 * information with messages being sent.
	 * 
	 * @param op
	 * @param message
	 * @param schemaMap
	 * @param partObj
	 * @return
	 */
	public com.predic8.wsdl.Part createOrModifyPart(String message, Map<String, Schema> schemaMap,
			com.predic8.wsdl.Part partObj) {
		logger.debug("Msg is {} ", message);
		Schema sc = null;
		Element ele = null;
		Map<String, String> partNs = null;
		Map<String, Object> schemaElements = null;
		schemaElements = getSchemaForMessage(message, schemaMap);
		sc = (Schema) schemaElements.get("SCHEMA");
		ele = (Element) schemaElements.get("ELEMENT");

		// Preparing Request Part
		partNs = new HashMap<>();
		// Attaching namespaces for Part
		partNs.put("q1", sc.getTargetNamespace());
		if (null == partObj)
			partObj = new com.predic8.wsdl.Part();
		partObj.setName("parameters");
		partObj.setProperty("namespaces", partNs);
		partObj.setElement(ele);
		return partObj;
	}

	/**
	 * This method would respond both the schema part and element part from provided
	 * schemas in the application. A message here in the request part is represented
	 * in two ways one with prefix and other a direct. Prefixed message would
	 * indicate that the element should be referred to a particular schema and not
	 * all the schemas. This would be helpful if same element name is present in
	 * more than one XSD, in that case we can convey in message part by mentioning
	 * the prefix.
	 * 
	 * @param message
	 * @param schemaMap
	 * @return
	 */
	private Map<String, Object> getSchemaForMessage(String message, Map<String, Schema> schemaMap) {
		Map<String, Object> retObj = new HashMap<String, Object>();
		Schema sc = null;
		String elementName = null;
		if (message.indexOf(":") >= 0) {
			String[] msgParts = message.split(":");
			{
				// Validating msgParts
				if (msgParts.length < 2) {
					logger.error("Message is provided in wrong format for {}", message);
					throw new RuntimeException("Message is provided in wrong format for " + message + ". Please check");
				}
			}
			sc = getSchemaForPrefix(schemaMap, msgParts[0]);
			elementName = msgParts[1];
		} else {
			elementName = message;
			sc = getSchemaFromAll(schemaMap, elementName);
		}
		if (sc == null) {
			logger.error("Unable to find schema reference to the element that has to be mapped");
			throw new RuntimeException("Unable to find element from the added schemas " + elementName);
		}
		if (null == sc.getElement(elementName)) {
			logger.error("Unable to find the requested element in the schemas provided by you");
			throw new RuntimeException("Unable to find requested element in the schemas provided for " + message);
		}
		retObj.put("ELEMENT", sc.getElement(elementName));
		retObj.put("SCHEMA", sc);
		return retObj;
	}

	/**
	 * 
	 * This method would basically search for the element in all possible schemas
	 * available. If an element is found then the associated schema is returned.
	 * This would basically remove the necessary of providing prefixes for
	 * request/response elements of operations but this would be necessary if they
	 * both refer to same element but the elements are present in different schemas.
	 * In such cases we would prefer the usage of prefixes in request/response
	 * elements.
	 * 
	 * @param schemaMap
	 * @param elementName
	 * @return
	 */
	private Schema getSchemaFromAll(Map<String, Schema> schemaMap, String elementName) {
		Schema sc = null;
		Set<String> keys = schemaMap.keySet();
		Element ele = null;
		for (String key : keys) {
			ele = schemaMap.get(key).getElement(elementName);
			if (ele != null) {
				sc = schemaMap.get(key);
				break;
			}
		}
		return sc;
	}

	/**
	 * This would fetch schema information for a particular prefix. Request/Response
	 * element of Operation can also be identified by prefixes to look up associated
	 * schema for them. Hence this method would be required.
	 * 
	 * @param schemaMap
	 * @param prefix
	 * @return
	 */
	private Schema getSchemaForPrefix(Map<String, Schema> schemaMap, String prefix) {
		Schema sc = null;
		if (!schemaMap.containsKey(prefix)) {
			throw new RuntimeException(
					"Unable to find schema for prefix mentioned. Please correct the prefix and send it");
		}
		sc = schemaMap.get(prefix);
		return sc;
	}
}
