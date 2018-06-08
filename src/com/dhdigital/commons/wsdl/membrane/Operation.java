package com.dhdigital.commons.wsdl.membrane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dhdigital.commons.wsdl.api.commons.Operations;
import com.dhdigital.commons.wsdl.config.WSDLConfiguration;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Part;
import com.predic8.wsdl.PortType;

/**
 * 
 * All methods pertaining to WSDL Operation are to be performed here.
 * 
 * @author joshi
 *
 */
public class Operation {

	private static Logger logger = LoggerFactory.getLogger(Operation.class);

	/**
	 * 
	 * This method would basically take care of either creating or modifying
	 * existing operations. Modifying existing operation currently is not part of
	 * this API's scope but would be available in subsequent releases. What this
	 * method would be doing is if the operation is already available in existing
	 * portType, it simply skips that operation and proceed with next operation
	 * present in the wsdl configuration.
	 * 
	 * @param portType
	 * @param config
	 * @param schemaMap
	 * @return
	 */
	public PortType getOperations(PortType portType, WSDLConfiguration config, Map<String, Schema> schemaMap) {
		com.predic8.wsdl.Operation operation = null;
		logger.debug("Creating Operation information for the WSDL");
		List<Part> parts = null;
		String ipMsg = null;
		String opMsg = null;
		com.dhdigital.commons.wsdl.membrane.Part partObj = new com.dhdigital.commons.wsdl.membrane.Part();

		for (Operations op : config.getOperations()) {
			logger.debug("Creating operation for {}", op.getOperationName());
			com.predic8.wsdl.Operation pObj = portType.getOperation(op.getOperationName());
			if (pObj == null) {
				operation = portType.newOperation(op.getOperationName());
				{
					logger.debug("Preparing Input message for Operation");
					ipMsg = op.getOperationName() + "Request";
					parts = new ArrayList<Part>();
					parts.add(partObj.createPart(op.getRequestElement(), schemaMap));
					operation.newInput(ipMsg).newMessage(ipMsg).setParts(parts);
					logger.debug("Added part information to Input Message");
				}

				{
					logger.debug("Preparing Output message of Operation");
					opMsg = op.getOperationName() + "Response";
					parts = new ArrayList<Part>();
					parts.add(partObj.createPart(op.getResponseElement(), schemaMap));
					operation.newOutput(opMsg).newMessage(opMsg).setParts(parts);
				}
			} else {
				logger.debug("Modifying operation is not currently available {}", op.getOperationName());
			}
		}
		return portType;
	}
}
