/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
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

package org.osgi.impl.service.rest.client;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.rest.client.RestClientFactory;

public class Activator implements BundleActivator {

	/**
	 * Bundle activator for the REST client RI
	 * 
	 * @author Jan S. Rellermeyer, IBM Research
	 */
	public void start(final BundleContext context) throws Exception {
		context.registerService(RestClientFactory.class, new RestClientFactoryImpl(), null);
	}

	public void stop(final BundleContext context) throws Exception {
		// nop
	}

}
