/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.rest.pojos;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.rest.PojoReflector.RootNode;

/**
 * Pojo for bundles.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
@RootNode(name = "bundle")
public final class BundlePojo {

	private long	id;
	private String	location;
	private long	lastModified;
	private int		state;
	private String	symbolicName;
	private String	version;

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
	 * @param version the version to set
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
