/*
 * Copyright (c) OSGi Alliance (2006, 2009). All Rights Reserved.
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

// This document is an experimental draft to enable interoperability
// between bundle repositories. There is currently no commitment to 
// turn this draft into an official specification.  

package org.osgi.service.obr;

import java.util.Map;

/**
 * 
 * Service that provides access to the {@link Resolver} capability.
 * 
 * @version $Id$
 * @deprecated This is proposed API. As a result, this API may never be
 *             published or the final API may change substantially by the time
 *             of final publication. You are cautioned against relying upon this
 *             API.
 */
public interface ResolverFactory {
	public static final String PATH = "resolutionPath";
	
	/**
	 * Service attribute reverse domain name indicating unique provider of resolver
	 */
	public static final String PROVIDER = "provider";
	
	/**
	 * Create a resolver that uses the BundleContext of the 
	 * client to create a Part[].
	 *  
	 * @param properties
	 * @return
	 * @throws Exception if the properties are not understood by this factory
	 */
	Resolver newResolver(Map properties) throws Exception;
	
	/**
	 * Create a resolver that uses the supplied capability providers
	 * to form a resolution. The providers are consulted in order with
	 * providers at the start of the array preferred over later providers.
	 * 
	 * @param properties
	 * @param parts
	 * @return
	 */
	Resolver newResolver(Map properties, CapabilityProvider... providers) throws Exception;
}
