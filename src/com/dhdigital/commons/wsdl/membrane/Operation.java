package com.dhdigital.commons.wsdl.membrane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dhdigital.commons.wsdl.api.commons.Operations;
import com.dhdigital.commons.wsdl.config.WSDLConfiguration;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
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
	 * This method would basically take care of both creating and modifying
	 * existing operations.
	 * 
	 * @param defs
	 * @param localSchema
	 * @param portType
	 * @param config
	 * @param schemaMap
	 * @return
	 */
	public PortType getOperations(Definitions defs, Schema localSchema, PortType portType, WSDLConfiguration config,
			Map<String, Schema> schemaMap) {
		com.predic8.wsdl.Operation operation = null;
		logger.debug("Creating Operation information for the WSDL");
		List<Part> parts = null;
		String ipMsg = null;
		String opMsg = null;
		com.dhdigital.commons.wsdl.membrane.Part partObj = null;
		for (Operations op : config.getOperations()) {
			logger.debug("Creating operation for {}", op.getOperationName());
			com.predic8.wsdl.Operation opObj = portType.getOperation(op.getOperationName());
			ipMsg = op.getOperationName();
			opMsg = op.getOperationName() + "Response";
			partObj = new com.dhdigital.commons.wsdl.membrane.Part();
			if (opObj == null) {
				operation = portType.newOperation(op.getOperationName());
				{
					logger.debug("Preparing Input message for Operation");
					parts = new ArrayList<Part>();
					parts.add(partObj.createOrModifyPart(defs, localSchema, ipMsg, op.getRequestElement(), schemaMap,
							null));
					operation.newInput(ipMsg).newMessage(ipMsg).setParts(parts);
					logger.debug("Added part information to Input Message");
				}
				{
					logger.debug("Preparing Output message of Operation");
					parts = new ArrayList<Part>();
					parts.add(partObj.createOrModifyPart(defs, localSchema, opMsg, op.getResponseElement(), schemaMap,
							null));
					operation.newOutput(opMsg).newMessage(opMsg).setParts(parts);
				}
			} else {
				logger.debug("Trying to modify the operation {}", op.getOperationName());
				logger.debug("Operation Modificiation flag is set to {}", op.isOverride());
				if (op.isOverride()) {
					logger.debug("Operation Modification is in process");
					Part opPartObj = null;
					// Modifying the request element
					try {
						parts = opObj.getInput().getMessage().getParts();
						opPartObj = parts.get(0);
						partObj.createOrModifyPart(defs, localSchema, ipMsg, op.getRequestElement(), schemaMap,
								opPartObj);
						logger.debug("Modified the request element");
					} catch (Exception e) {
						logger.error("Exception while retrieving part object from the operation.");
						throw new RuntimeException("Exception while retrieving part object from the operation.");
					}
					// Modifying the response element
					try {
						parts = opObj.getOutput().getMessage().getParts();
						opPartObj = parts.get(0);
						partObj.createOrModifyPart(defs, localSchema, opMsg, op.getResponseElement(), schemaMap,
								opPartObj);
						logger.debug("Modified the response element");
					} catch (Exception e) {
						logger.error("Exception while retrieving part object from the operation.");
						throw new RuntimeException("Exception while retrieving part object from the operation.");
					}

				} else {
					logger.debug("Modifying operation it not applicable for the current operation");
				}
			}
		}
		return portType;
	}
}
