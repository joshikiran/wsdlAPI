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
	 * for the WSDL. Since currently modification of Operation is not in the scope,
	 * we would be going ahead with only creating a part, as this would be present
	 * only during creation of operation.
	 * 
	 * @param op
	 * @param message
	 * @param schemaMap
	 * @return
	 */
	public com.predic8.wsdl.Part createPart(String message, Map<String, Schema> schemaMap) {
		logger.debug("Msg is {} ", message);
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
		Map<String, String> partNs = null;
		// Preparing Request Part
		partNs = new HashMap<>();
		// Get the schema for msgParts[0]
		if (sc == null) {
			logger.error("Unable to find schema reference to the element that has to be mapped");
			throw new RuntimeException("Unable to find element from the added schemas " + elementName);
		}
		// Attaching namespaces for Part
		partNs.put("q1", sc.getTargetNamespace());
		com.predic8.wsdl.Part partObj = new com.predic8.wsdl.Part();
		partObj.setName("parameters");
		partObj.setProperty("namespaces", partNs);
		Element ele = sc.getElement(elementName);
		if (null == ele) {
			logger.error("Unable to find the requested element in the schemas provided by you");
			throw new RuntimeException("Unable to find requested element in the schemas provided for " + message);
		}
		partObj.setElement(ele);
		return partObj;
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
