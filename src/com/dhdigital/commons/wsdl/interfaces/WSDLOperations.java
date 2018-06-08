package com.dhdigital.commons.wsdl.interfaces;

import com.dhdigital.commons.wsdl.config.WSDLConfiguration;
import com.dhdigital.commons.wsdl.config.WSDLResponseObject;

/**
 * Interface to provide all possible operations for WSDL. Operations include
 * 'GenerateWSDL', 'ReadWSDL','GetOperations', 'GetRequestForOperation',
 * 'ValidateWSDL' etc., This has to be implemented by necessary low level
 * service providers like SOAPUI, MembraneSOA etc.,
 * 
 * @author joshi
 *
 */
public interface WSDLOperations {

	WSDLResponseObject generateWSDL(WSDLConfiguration config);

	<T extends WSDLResponseObject> T validateWSDL(T resObj);

	void validateConfiguration(WSDLConfiguration config);
}
