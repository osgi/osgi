/*
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
 

package org.eclipse.osgi.component.model;

import java.util.ArrayList;

import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.parser.Parser;
import org.osgi.framework.Bundle;

/**
 *
 * Cache of component descriptions.
 * 
 * @version $Revision$
 */
public class ComponentDescriptionCache {
	
	/**
	 * Parser object to use if the bundle's information is not in the cache
	 * or is stale.
	 */
	protected Parser parser;
	
	/**
	 * Create the cache object.
	 * 
	 * @param main Main object.
	 */
	public ComponentDescriptionCache(Main main) {
		parser = new Parser(main);
	}

	/**
	 * Return the component descriptions for the specified bundle.
	 * 
	 * @param bundle Bundle for which component description are to be returns
	 * @return An array list of the component descriptions for the specified bundle.
	 */
	public ArrayList getComponentDescriptions(Bundle bundle) {
		// The cache is not yet implement, so we always parse the XML.
		return parser.getComponentDescriptions(bundle);
	}
	
	public void dispose() {
		parser.dispose();
	}
}
