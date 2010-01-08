/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.test.cases.scaconfigtype.junit.ct12;

import java.util.Hashtable;

import java.util.List;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.test.cases.scaconfigtype.common.A;
import org.osgi.test.cases.scaconfigtype.common.B;
import org.osgi.test.cases.scaconfigtype.common.RemoteServiceConstants;
import org.osgi.test.cases.scaconfigtype.common.SCAConfigConstants;
import org.osgi.test.cases.scaconfigtype.common.Utils;

/**
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class Activator implements BundleActivator, A, B {
	BundleContext       context;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS, SCAConfigConstants.ORG_OSGI_SCA_CONFIG);

		context.registerService(new String[]{A.class.getName()}, this, dictionary);
		
		dictionary = new Hashtable<String, String>();
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, B.class.getName());
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS, fabricateConfigType());

		context.registerService(new String[]{A.class.getName()}, this, dictionary);
	}

	private String fabricateConfigType() throws Exception {
		List types = Utils.getSupportedConfigTypes(context);
		
		Assert.assertFalse(types.isEmpty());
		String type = (String) types.get(0);
		do {
			type += ".foo";
		}
		while ( types.contains( type ) );
		
		return type;
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

	/**
	 * @see org.osgi.test.cases.scaconfigtype.common.A#getA()
	 */
	public String getA() {
		return A;
	}

	public String getB() {
		return B;
	}	
}
