/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.rest.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.rest.PojoReflector.RootNode;

/**
 * Pojo for services.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
@RootNode(name = "service")
public final class ServicePojo {

	private long				id;
	private Map<String, Object>	properties;
	private String				bundle;
	private String[]			usingBundles;

	public ServicePojo(final ServiceReference<?> sref) {
		id = ((Long) sref.getProperty(Constants.SERVICE_ID)).longValue();
		final Map<String, Object> props = new HashMap<String, Object>();
		for (final String key : sref.getPropertyKeys()) {
			props.put(key, sref.getProperty(key));
		}
		setProperties(props);

		setBundle(getBundleUri(sref.getBundle()));
		final List<String> usingBundlesList = new ArrayList<String>();

		if (sref.getUsingBundles() != null) {
			for (final Bundle using : sref.getUsingBundles()) {
				usingBundlesList.add(getBundleUri(using));
			}
		}
		setUsingBundles(usingBundlesList.toArray(new String[usingBundlesList.size()]));
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
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

	private String getBundleUri(final org.osgi.framework.Bundle b) {
		return "framework/bundle/" + b.getBundleId();
	}

}
