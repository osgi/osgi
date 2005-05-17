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

import java.io.*;
import java.net.URL;
import java.util.*;
import org.eclipse.osgi.component.resolver.ComponentProperties;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentException;

/**
 *
 * Combines the Compoent Description object and properties 
 * 
 * @version $Revision$
 */
public class ComponentDescriptionProp {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	protected ComponentDescription componentDescription;
	protected ComponentProperties componentProperties;
	protected Hashtable properties;
	protected ComponentContext componentContext;

	protected List referenceCDPs;
	protected List servicesProvided;
	protected List references;
	protected List instances;

	/**
	 * @param bundle The bundle to set.
	 */
	public ComponentDescriptionProp(ComponentDescription cd, Dictionary configProperties) throws IOException {

		this.componentDescription = cd;
		properties = new Hashtable();
		referenceCDPs = new ArrayList();
		servicesProvided = new ArrayList();
		instances = new ArrayList();
		initProperties(configProperties);
	}

	/** 
	 * Initialize Properties for this Component 
	 * 
	 * The property elements provide default or supplemental property values 
	 * if not overridden by the properties retrieved from Configuration Admin.
	 * 
	 * The property and properties elements are processed in top to bottom order.
	 * This allows later elements to override property values defined by earlier 
	 * elements.  There can be many property and properties elements and they may be 
	 * interleaved.
	 * 
	 * @return Dictionary properties
	 */
	public void initProperties(Dictionary configProperties) throws IOException {

		// ComponentDescription Default Properties
		PropertyDescription[] propertyDescriptions = componentDescription.getProperties();
		for (int i = 0; i < propertyDescriptions.length; i++) {
			if (propertyDescriptions[i] instanceof PropertyValueDescription) {
				PropertyValueDescription propvalue = (PropertyValueDescription) propertyDescriptions[i];
				properties.put(propvalue.getName(), propvalue.getValue());
			} else {
				addEntryProperties(((PropertyResourceDescription) propertyDescriptions[i]).getEntry());
			}
		}

	}

	/**
	 * Get the Service Component properties from a properties entry file 
	 * 
	 * @param propertyEntryName - the name of the properties file
	 */
	private void addEntryProperties(String propertyEntryName) throws IOException {

		URL url = null;
		Properties props = new Properties();

		Bundle bundle = componentDescription.getBundle();
		url = bundle.getEntry(propertyEntryName);
		if (url == null) {
			throw new ComponentException("Properties entry file " + propertyEntryName + " cannot be found");
		}

		InputStream in = null;
		File file = new File(propertyEntryName);

		if (file.exists()) {
			in = new FileInputStream(file);
		}
		if (in == null) {
			in = getClass().getResourceAsStream(propertyEntryName);
		}
		if (in == null) {
			in = url.openStream();
		}
		if (in != null) {
			props.load(new BufferedInputStream(in));
			in.close();
		} else {
			if (DEBUG)
				System.out.println("Unable to find properties file " + propertyEntryName);
		}

		//Get the properties value from the file and store them in the properties object
		if (props != null) {
			Enumeration keys = props.propertyNames();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				properties.put(key, props.get(key));
			}
		}

		return;

	}

	/** 
	 * getProperties
	 * 
	 * @return Dictionary properties
	 */
	public Dictionary getProperties() {
		return properties;
	}

	/** 
	 * getComponentDescription
	 * 
	 * @return ComponentDescription 
	 */
	public ComponentDescription getComponentDescription() {
		return componentDescription;
	}

	public void setComponentContext(ComponentContext context) {
		this.componentContext = context;
	}

	public ComponentContext getComponentContext() {
		return componentContext;
	}

	public void setReferenceCDP(ComponentDescriptionProp cdp) {
		if (!referenceCDPs.contains(cdp))
			referenceCDPs.add(cdp);
	}

	public List getReferenceCDPs() {
		return referenceCDPs == null ? Collections.EMPTY_LIST : referenceCDPs;
	}

	public void clearReferenceCDPs() {
		referenceCDPs.clear();
	}

	public void setServiceProvided(List services) {
		this.servicesProvided = services;
	}

	public List getServicesPrivided() {
		return servicesProvided == null ? Collections.EMPTY_LIST : servicesProvided;
	}

	public void setReferences(List references) {
		this.references = references;
	}

	public List getReferences() {
		return references == null ? Collections.EMPTY_LIST : references;
	}

	public void addInstance(Object object) {
		instances.add(object);
	}

	public List getInstances() {
		return instances;
	}

	public void removeInstance(Object object) {
		instances.remove(object);
	}

	public void removeAllInstances() {
		instances.clear();
	}
}
