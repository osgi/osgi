/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

package org.osgi.test.cases.component.tb17;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.test.cases.component.service.BaseService;

public class ReferencePolicyOption implements BaseService {
	private static final String	ROOT		= "org.osgi.test.cases.component.tb17";
	private static final String	KEY			= ROOT + ".serviceproperty";
	private static final String	IDENTITY	= ROOT + ".identity";
	private final Dictionary	properties;
	private final SortedSet		boundServices;

	public ReferencePolicyOption() {
		properties = new Hashtable();
		properties.put(IDENTITY,
				Integer.toHexString(System.identityHashCode(this)));
		boundServices = new TreeSet(Collections.reverseOrder());
	}

	public Dictionary getProperties() {
		return properties;
	}

	private void activate(ComponentContext ctxt) {
		Dictionary props = ctxt.getProperties();
		Enumeration en = props.keys();
		while (en.hasMoreElements()) {
			Object key = en.nextElement();
			properties.put(key, props.get(key));
		}
	}

	private void deactivate(ComponentContext ctxt) {
		// empty
	}

	private void bind(ServiceReference ref, String name) {
		boundServices.add(ref);
		properties.put(KEY, "bind" + name + toString(boundServices));
	}

	private void unbind(ServiceReference ref, String name) {
		boundServices.remove(ref);
		properties.put(KEY, "unbind" + name + toString(boundServices));
	}

	private static String toString(SortedSet boundServices) {
		StringBuffer sb = new StringBuffer();
		for (Iterator iter = boundServices.iterator(); iter.hasNext();) {
			ServiceReference ref = (ServiceReference) iter.next();
			sb.append("/").append(ref.getProperty(KEY));
		}
		return sb.toString();
	}

	private void bindSR01(ServiceReference ref) {
		bind(ref, "SR01");
	}

	private void unbindSR01(ServiceReference ref) {
		unbind(ref, "SR01");
	}

	private void bindSR11(ServiceReference ref) {
		bind(ref, "SR11");
	}

	private void unbindSR11(ServiceReference ref) {
		unbind(ref, "SR11");
	}

	private void bindSR0N(ServiceReference ref) {
		bind(ref, "SR0N");
	}

	private void unbindSR0N(ServiceReference ref) {
		unbind(ref, "SR0N");
	}

	private void bindSR1N(ServiceReference ref) {
		bind(ref, "SR1N");
	}

	private void unbindSR1N(ServiceReference ref) {
		unbind(ref, "SR1N");
	}

	private void bindSG01(ServiceReference ref) {
		bind(ref, "SG01");
	}

	private void unbindSG01(ServiceReference ref) {
		unbind(ref, "SG01");
	}

	private void bindSG11(ServiceReference ref) {
		bind(ref, "SG11");
	}

	private void unbindSG11(ServiceReference ref) {
		unbind(ref, "SG11");
	}

	private void bindSG0N(ServiceReference ref) {
		bind(ref, "SG0N");
	}

	private void unbindSG0N(ServiceReference ref) {
		unbind(ref, "SG0N");
	}

	private void bindSG1N(ServiceReference ref) {
		bind(ref, "SG1N");
	}

	private void unbindSG1N(ServiceReference ref) {
		unbind(ref, "SG1N");
	}

	private void bindDR01(ServiceReference ref) {
		bind(ref, "DR01");
	}

	private void unbindDR01(ServiceReference ref) {
		unbind(ref, "DR01");
	}

	private void bindDR11(ServiceReference ref) {
		bind(ref, "DR11");
	}

	private void unbindDR11(ServiceReference ref) {
		unbind(ref, "DR11");
	}

	private void bindDR0N(ServiceReference ref) {
		bind(ref, "DR0N");
	}

	private void unbindDR0N(ServiceReference ref) {
		unbind(ref, "DR0N");
	}

	private void bindDR1N(ServiceReference ref) {
		bind(ref, "DR1N");
	}

	private void unbindDR1N(ServiceReference ref) {
		unbind(ref, "DR1N");
	}

	private void bindDG01(ServiceReference ref) {
		bind(ref, "DG01");
	}

	private void unbindDG01(ServiceReference ref) {
		unbind(ref, "DG01");
	}

	private void bindDG11(ServiceReference ref) {
		bind(ref, "DG11");
	}

	private void unbindDG11(ServiceReference ref) {
		unbind(ref, "DG11");
	}

	private void bindDG0N(ServiceReference ref) {
		bind(ref, "DG0N");
	}

	private void unbindDG0N(ServiceReference ref) {
		unbind(ref, "DG0N");
	}

	private void bindDG1N(ServiceReference ref) {
		bind(ref, "DG1N");
	}

	private void unbindDG1N(ServiceReference ref) {
		unbind(ref, "DG1N");
	}

}
