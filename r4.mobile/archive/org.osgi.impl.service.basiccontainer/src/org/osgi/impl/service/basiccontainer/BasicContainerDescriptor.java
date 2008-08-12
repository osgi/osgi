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
package org.osgi.impl.service.basiccontainer;

import java.io.InputStream;
import java.util.*;
import org.osgi.service.application.ApplicationDescriptor;

public class BasicContainerDescriptor implements ApplicationDescriptor {
	private String	appName;
	private String	appCategory;
	private String	appVersion;
	private String	contID;
	private String	id;
	private String	mainClass;

	public BasicContainerDescriptor(String name, String category,
			String version, String cId, String appId, String mainCls) {
		appName = name;
		appCategory = category;
		appVersion = version;
		contID = cId;
		id = appId;
		mainClass = mainCls;
	}

	public String getCategory() {
		return appCategory;
	}

	public void setCategory(String cat) {
		appCategory = cat;
	}

	public String getContainerID() {
		return contID;
	}

	public Map getContainerProperties(String locale) {
		/* TODO */
		return null;
	}

	public String getName() {
		return appName;
	}

	public void setName(String name) {
		appName = name;
	}

	public Map getProperties(String locale) {
		/* TODO  */
		return null;
	}

	public String getUniqueID() {
		return contID + "-" + appCategory + "-" + appName + "-" + appVersion;
	}

	public String getVersion() {
		return appVersion;
	}

	public void setVersion(String ver) {
		appVersion = ver;
	}

	public InputStream getIcon(Locale aLocale, int pixNum) {
		/* TODO  */
		return null;
	}

	public String getId() {
		return id;
	}

	public String getMainClass() {
		return mainClass;
	}
}
