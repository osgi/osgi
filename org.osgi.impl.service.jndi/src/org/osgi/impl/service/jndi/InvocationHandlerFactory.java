/*
 * Copyright 2010 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.osgi.impl.service.jndi;

import java.lang.reflect.InvocationHandler;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Factory interface for creating InvocationHandler instances
 * for the purpose of proxying OSGi services.  
 *
 * 
 * @version $Revision$
 */
interface InvocationHandlerFactory {
	/**
	 * Create an InvocationHandler for the specified OSGi service.  
	 * 
	 * @param bundleContext the BundleContext used to obtain the OSGi service
	 * @param serviceReference the ServiceReference that represents the service
	 * @param urlParser the OSGiURLParser associated with this service request
	 * @param osgiService the initial OSGi service to be proxied. 
	 * 
	 * @return an InvocationHandler that can be associated with a dynamic proxy
	 *         for the specified service.  
	 */
	InvocationHandler create(BundleContext bundleContext, ServiceReference serviceReference, OSGiURLParser urlParser, Object osgiService);
}
