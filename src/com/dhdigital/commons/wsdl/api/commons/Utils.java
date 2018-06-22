package com.dhdigital.commons.wsdl.api.commons;

import java.util.Map;

/**
 * Common utility functions are to be mentioned here.
 * 
 * @author joshi
 *
 */
public class Utils {

	/**
	 * Get's the prefix for any given namespace
	 * 
	 * @param ns
	 * @param namespace
	 * @return
	 */
	public String getPrefix(Map<String, Object> ns, String namespace) {
		String prefix = null;
		for (String p : ns.keySet()) {
			if (namespace.equals(ns.get(p))) {
				prefix = p;
				break;
			}
		}
		return prefix;
	}

	/**
	 * Gets the namespace prefix for elementNamespace from the map of namespaces if
	 * its already present if not then it creates one and returns the updated list
	 * of namespaces.
	 * 
	 * @param defs
	 * @param ns
	 * @param elementName
	 * @param elementNs
	 * @return
	 */
	public Map<String, Object> getNs(Map<String, Object> ns, String elementName, String elementNs) {
		String nsPrefix = elementName.toLowerCase();
		String pf = getPrefix(ns, elementNs);
		if (null == pf)
			ns.put(nsPrefix, elementNs);
		else
			ns.put(pf, elementNs);
		return ns;
	}
}
