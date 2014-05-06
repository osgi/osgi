/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2011
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2011 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.pojos;

import org.osgi.framework.Bundle;

public final class BundlePojo {

	private long id;
	private String location;
	private long lastModified;
	private int state;
	private String symbolicName;
	private String version;

	public BundlePojo(final Bundle bundle) {
		setId(bundle.getBundleId());
		setLocation(bundle.getLocation());
		setLastModified(bundle.getLastModified());
		setState(bundle.getState());
		setSymbolicName(bundle.getSymbolicName());
		setVersion(bundle.getVersion().toString());
	}

	public void setId(final long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLastModified(final long lastModified) {
		this.lastModified = lastModified;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setState(final int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void setSymbolicName(final String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public String getSymbolicName() {
		return symbolicName;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(final String version) {
		this.version = version;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

}
