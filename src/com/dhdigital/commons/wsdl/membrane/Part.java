package com.dhdigital.commons.wsdl.membrane;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dhdigital.commons.wsdl.api.commons.Utils;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.Sequence;
import com.predic8.wsdl.Definitions;

import groovy.xml.QName;

/**
 * 
 * All methods pertaining to WSDL Parts are implemented here.
 * 
 * @author joshi
 *
 */
@SuppressWarnings("unchecked")
public class Part {

	private static Logger logger = LoggerFactory.getLogger(Part.class);
	private Utils utils = new Utils();

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
	public com.predic8.wsdl.Part createOrModifyPart(Definitions defs, Schema localSchema, String elementName,
			String message, Map<String, Schema> schemaMap, com.predic8.wsdl.Part partObj) {
		logger.debug("Msg is {} ", message);
		Element ele = null;
		Map<String, String> partNs = null;
		ele = getSchemaElementForMessage(message, schemaMap);

		// Preparing Request Part
		partNs = new HashMap<>();
		// Attaching namespaces for Part
		partNs.put("tns", localSchema.getTargetNamespace());
		if (null == partObj)
			partObj = new com.predic8.wsdl.Part();
		partObj.setName("parameters");
		partObj.setProperty("namespaces", partNs);

		// Create or Modify element from local Schema
		Element localElement = createOrModifySchemaElement(defs, localSchema, elementName, ele);
		partObj.setElement(localElement);
		return partObj;
	}

	/**
	 * Method to either create a local schema element or modify existing element
	 * with the reference element provided to the method.
	 * 
	 * A wrapper element must be defined as a complex type that is a sequence of
	 * elements. Each child element in that sequence will be generated as a
	 * parameter in the service interface. The name of the input wrapper element
	 * must be the same as the operation name. The name of the output wrapper
	 * element should be (but doesn’t have to be) the operation name appended with
	 * "Response" (e.g., if the operation name is "add", the output wrapper element
	 * should be called "addResponse").
	 * 
	 * @param defs
	 * @param localSchema
	 * @param elementName
	 * @param refElement
	 * @return
	 */
	private Element createOrModifySchemaElement(Definitions defs, Schema localSchema, String elementName,
			Element refElement) {
		Element ele = null;
		ele = localSchema.getElement(elementName);
		ComplexType complexType = null;
		Sequence sequence = null;
		Element refEle = null;
		Map<String, Object> eleNs = null;

		if (null == ele) {
			// Have to create a new element with reference
			ele = localSchema.newElement(elementName);
			ele.setName(elementName);
			complexType = ele.newComplexType();
			sequence = complexType.newSequence();
			refEle = sequence.newElement(refElement.getName());
		} else {
			// Modifying existing local schema element
			complexType = (ComplexType) ele.getEmbeddedType();
			sequence = complexType.getSequence();
			refEle = sequence.getElements().get(0);
		}
		Map<String, Object> ns = (Map<String, Object>) defs.getProperty("namespaces");
		eleNs = utils.getNs(ns, refElement.getName(), refElement.getNamespaceUri());
		defs.setProperty("namespaces", eleNs);
		refEle.setRef(new QName(refElement.getNamespaceUri(), refElement.getName()));
		refEle.setProperty("namespaces", eleNs);

		return ele;
	}

	/**
	 * This method would respond with the element part from provided schemas in the
	 * application. A message here in the request part is represented in two ways
	 * one with prefix and other a direct. Prefixed message would indicate that the
	 * element should be referred to a particular schema and not all the schemas.
	 * This would be helpful if same element name is present in more than one XSD,
	 * in that case we can convey in message part by mentioning the prefix.
	 * 
	 * @param message
	 * @param schemaMap
	 * @return
	 */
	private Element getSchemaElementForMessage(String message, Map<String, Schema> schemaMap) {
		Element element = null;
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
		element = sc.getElement(elementName);
		return element;
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
