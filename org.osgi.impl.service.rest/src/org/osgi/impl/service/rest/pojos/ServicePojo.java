/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2012
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2013 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.Bundle;

public final class ServicePojo {

	private Map<String, Object> properties;

	private String bundle;

	private String[] usingBundles;

	public ServicePojo(final ServiceReference<?> sref) {
		final Map<String, Object> props = new HashMap<String, Object>();
		for (final String key : sref.getPropertyKeys()) {
			props.put(key, sref.getProperty(key));
		}
		setProperties(props);
		setBundle(getBundleUri(sref.getBundle()));
		final List<String> usingBundles = new ArrayList<String>();
		if (sref.getUsingBundles() != null) {
			for (final Bundle using : sref.getUsingBundles()) {
				usingBundles.add(getBundleUri(using));
			}
		}
		setUsingBundles(usingBundles.toArray(new String[usingBundles.size()]));
	}

	public void setProperties(final Map<String, Object> properties) {
		this.properties = properties;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setBundle(final String bundle) {
		this.bundle = bundle;
	}

	public String getBundle() {
		return bundle;
	}

	public void setUsingBundles(final String[] usingBundles) {
		this.usingBundles = usingBundles;
	}

	public String[] getUsingBundles() {
		return usingBundles;
	}

	private String getBundleUri(final org.osgi.framework.Bundle bundle) {
		return "framework/bundle/" + bundle.getBundleId();
	}

}
