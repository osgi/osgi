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
package org.osgi.test.cases.scaconfigtype.junit.tck12;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.cases.scaconfigtype.common.A;
import org.osgi.test.cases.scaconfigtype.common.B;
import org.osgi.test.cases.scaconfigtype.common.RemoteServiceConstants;
import org.osgi.test.cases.scaconfigtype.common.SCAConfigConstants;

/**
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class Activator implements BundleActivator, A, B {
	ServiceRegistration registration;
	BundleContext       context;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, A.class.getName());
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS, SCAConfigConstants.ORG_OSGI_SCA);

		registration = context.registerService(new String[]{A.class.getName()}, this, dictionary);
		
		dictionary = new Hashtable<String, String>();
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, B.class.getName());
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS, fabricateConfigType());

		registration = context.registerService(new String[]{A.class.getName()}, this, dictionary);
	}

	private String fabricateConfigType() {
		return "foo.bar.baz";
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
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
