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

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.framework.*;
import org.osgi.service.component.*;

import org.eclipse.osgi.component.resolver.*;


/**
 *
 * Combines the Compoent Description object and properties 
 * 
 * @version $Revision$
 */
public class ComponentDescriptionProp {
	
	/* set this to true to compile in debug messages */
	static final boolean				DEBUG	= false;

	protected ComponentDescription componentDescription;
	protected ComponentProperties componentProperties;
	protected Hashtable properties;

	
	/**
	 * @param bundle The bundle to set.
	 */
	public ComponentDescriptionProp(ComponentDescription cd, Dictionary configProperties) 
		throws IOException {
		
		this.componentDescription = cd;
		properties = new Hashtable();
		initProperties(configProperties);

	}
	
	/** 
	 * Initialize Properties for this Component 
	 * 
	 * The property elements provide default or supplemental property values 
	 * if not overridden by the properties retrieved from Configuration Admin
	 * 
	 * @return Dictionary properties
	 */
	public void initProperties(Dictionary configProperties) throws IOException {
		
		// ComponentDescription Default Properties
		PropertyDescription [] propertyDescriptions = componentDescription.getProperties();
		for (int i=0; i<propertyDescriptions.length; i++){
			if(propertyDescriptions[i] instanceof PropertyValueDescription) {
				PropertyValueDescription propvalue = (PropertyValueDescription)propertyDescriptions[i];
				properties.put(propvalue.getName(),propvalue.getValue());
			} else {
				addEntryProperties(((PropertyResourceDescription)propertyDescriptions[i]).getEntry());
			} 
		}
		
		// properties from Configuration Admin
		if (configProperties != null){
			Enumeration keys = configProperties.keys();
			while(keys.hasMoreElements()){
				properties.put(keys.nextElement(),configProperties.get(keys.nextElement()));
			}
		} 
		
	}

	 /**
	 * Get the Service Component properties from a properties entry file 
	 * 
	 * @param propertyEntryName - the name of the properties file
	 */
	private void addEntryProperties(String propertyEntryName) throws 
		IOException {
		
		URL url = null;
		Properties props = new Properties();
		
		Bundle bundle = componentDescription.getBundle();
		url = bundle.getEntry(propertyEntryName);
		if (url == null) {
			throw new ComponentException("Properties entry file "+propertyEntryName+" cannot be found");
		}
				
			InputStream in = null;
			File file = new File(propertyEntryName);
			
			if (file.exists()){
				in = new FileInputStream(file);
			}
			if (in == null){
				in = getClass().getResourceAsStream(propertyEntryName);
			}
			if (in == null) {
				in = url.openStream();
			}
			if (in != null){
				
				props.load(new BufferedInputStream(in));
				in.close();
			}
			else {
				if(DEBUG)
					System.out.println("Unable to find properties file "+propertyEntryName); 
			}

		//Get the properties value from the file and store them in the properties object
		if ( props !=null ){
			Enumeration keys = props.propertyNames();
			while (keys.hasMoreElements()){
				String key = (String)keys.nextElement();
				String[] result = new String[1];
				result[0] = (String)props.get(key);
				properties.put( key , result );
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
	
}
