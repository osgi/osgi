/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.megcontainer;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;

/**
 * The MEG container's ApplicationDescriptor realization
 */
public class MEGApplicationDescriptor implements ApplicationDescriptor {
	private Properties		props;
	private Hashtable		names;
	private Hashtable		icons;
	private BundleContext	bc;
	private String			startClass;
	private Bundle			bundle;

	MEGApplicationDescriptor(BundleContext bc, Properties props, Map names,
			Map icons, String startClass, Bundle bundle ) throws Exception {
		this.bc = bc;
		this.props = new Properties();
		this.props.putAll(props);
		this.names = new Hashtable(names);
		this.icons = new Hashtable(icons);
		this.startClass = startClass;
		this.bundle = bundle;
		if (names.size() == 0 || icons.size() == 0
				|| !props.containsKey("bundle_id")
				|| !props.containsKey("version"))
			throw new Exception("Invalid MEG container input!");
	}

	public String getStartClass() {
		return startClass;
	}

	public String getName() {
		String language = (Locale.getDefault()).getLanguage();
		String defaultName = (String) names.get(language);
		if (defaultName != null)
			return defaultName;
		Enumeration enum = names.keys();
		String firstKey = (String) enum.nextElement();
		return (String) names.get(firstKey);
	}

	public String getUniqueID() {
		String uniqueID = getContainerID() + "-" + getCategory() + "-"
				+ getName() + "-" + getVersion() + "-"
				+ props.getProperty("bundle_id");
		uniqueID = uniqueID.replace(',', '_');
		return uniqueID;
	}

	public String getVersion() {
		return props.getProperty("version");
	}

	public String getContainerID() {
		return "MEG";
	}

	public String getCategory() {
		String category = props.getProperty("category");
		if (category != null)
			return category;
		else
			return "unknown";
	}

	public Map getProperties(String locale) {
		Hashtable properties = new Hashtable();
		String localizedName = (String) names.get(locale);
		if (localizedName == null) {
			Enumeration enum = names.keys();
			String firstKey = (String) enum.nextElement();
			localizedName = (String) names.get(firstKey);
		}
		properties.put("localized_name", localizedName);
		boolean iconFound = false;
		String firstIcon = null;
		Enumeration icn = icons.keys();
		while (icn.hasMoreElements()) {
			String key = (String) icn.nextElement();
			if (firstIcon == null)
				firstIcon = key;
			if (key.startsWith(locale)) {
				properties.put("localized_icon_" + key.substring(3) + "_path",
						icons.get(key));
				iconFound = true;
			}
		}
		if (!iconFound)
			properties.put(
					"localized_icon_" + firstIcon.substring(3) + "_path", icons
							.get(firstIcon));
		properties.put("bundle_id", props.getProperty("bundle_id"));
		if (props.containsKey("category"))
			properties.put("category", props.getProperty("category"));
		String singleton = props.getProperty("singleton");
		if (singleton != null && singleton.equalsIgnoreCase("true"))
			properties.put("singleton", "true");
		else
			properties.put("singleton", "false");
		String autostart = props.getProperty("autostart");
		if (autostart != null && autostart.equalsIgnoreCase("true"))
			properties.put("autostart", "true");
		else
			properties.put("autostart", "false");
		String visible = props.getProperty("visible");
		if (autostart != null && autostart.equalsIgnoreCase("false"))
			properties.put("visible", "false");
		else
			properties.put("visible", "true");
		boolean locked = false;
		try {
			ServiceReference appManReference = bc
					.getServiceReference("org.osgi.service.application.ApplicationManager");
			ApplicationManager appMan = (ApplicationManager) bc
					.getService(appManReference);
			locked = appMan.isLocked(this);
			bc.ungetService(appManReference);
		}
		catch (Exception e) {
		}
		properties.put("locked", (new Boolean(locked)).toString());
		properties.put("application_type", "MEG");
		return properties;
	}

	public Map getContainerProperties(String locale) {
		try {
			ServiceReference[] MEGContainerList = bc.getServiceReferences(
					"org.osgi.service.application.ApplicationContainer",
					"(application_type=MEG)");
			if (MEGContainerList == null || MEGContainerList.length == 0)
				return null;
			Hashtable containerProps = new Hashtable();
			String[] propKeys = MEGContainerList[0].getPropertyKeys();
			for (int i = 0; i != propKeys.length; i++)
				containerProps.put(propKeys[i], MEGContainerList[0]
						.getProperty(propKeys[i]));
			return containerProps;
		}
		catch (InvalidSyntaxException e) {
			return null;
		}
	}
}
