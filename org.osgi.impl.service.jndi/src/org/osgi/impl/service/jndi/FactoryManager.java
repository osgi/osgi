/*
 * Copyright 2009 Oracle Corporation
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

import javax.naming.Context;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.ObjectFactory;


/**
 * Interface that defines the basic usage of the Factory Manager in RFC 142.
 * 
 * The FactoryManager interface can be used to abstract the details of OSGi
 * service access from certain portions of RFC 142.
 * 
 */
public interface FactoryManager extends InitialContextFactoryBuilder {

	/**
	 * Returns a javax.naming.spi.ObjectFactory that is published in the OSGi
	 * service registry. The ObjectFactory returned must support the specified
	 * urlScheme.
	 * 
	 * @param urlScheme the requested URL scheme
	 * @return a javax.naming.spi.ObjectFactory that supports the given URL
	 *         scheme
	 */
	public ObjectFactory getURLContextFactory(String urlScheme);
	
	/**
	 * Associates a given OSGi JNDI Factory service to a Context that 
	 * was created with the given service.  
	 * 
	 * @param factory the JNDI factory service used to create the Context
	 * @param createdContext the Context created with this factory service  
	 */
	public void associateFactoryService(Object factory, Context createdContext);
	
	
	/**
	 * Checks to see if a given OSGi JNDI Factory Service is still active in 
	 * the service registry.  
	 * @param factory the JNDI Factory service
	 * @return true if the service is still available
	 *         false if the service is no longer available
	 */
	public boolean isFactoryServiceActive(Object factory);
}
