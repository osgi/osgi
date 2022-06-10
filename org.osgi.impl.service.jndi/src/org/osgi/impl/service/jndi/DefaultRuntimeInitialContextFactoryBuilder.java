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

package org.osgi.impl.service.jndi;

import java.util.Hashtable;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

/**
 * An InitialContextFactoryBuilder that is responsible for providing access to
 * the JNDI implementations made available by the JDK/JRE. These implementations
 * include LDAP, DNS, RMI, etc.
 * 
 * This Builder interface must be registered as a service by the JNDI
 * implementation.
 * 
 * @author $Id$
 */
class DefaultRuntimeInitialContextFactoryBuilder implements
		InitialContextFactoryBuilder {

	private static final Logger logger = 
		Logger.getLogger(DefaultRuntimeInitialContextFactoryBuilder.class.getName());

	@Override
	public InitialContextFactory createInitialContextFactory(
			Hashtable< ? , ? > environment) throws NamingException {

		if (environment.get(Context.INITIAL_CONTEXT_FACTORY) != null) {
			final String initialContextFactoryName = 
				(String) environment.get(Context.INITIAL_CONTEXT_FACTORY);

			try {
				// attempt to load this provider from the system classpath
				try {
					Class< ? > clazz = getClass().getClassLoader()
							.loadClass(initialContextFactoryName);
					return (InitialContextFactory) clazz.getConstructor()
							.newInstance();
				} catch (IllegalAccessException e) {
					// Under JPMS, the class constructor is not accessible
					for (InitialContextFactory factory : ServiceLoader
							.loadInstalled(InitialContextFactory.class)) {
						if (factory.getClass()
								.getName()
								.equals(initialContextFactoryName)) {
							return factory;
						}
					}
				}
			} catch (Exception e) {
				logger.log(Level.FINEST,
							 "Error while trying to load system-level JNDI provider",
							 e);
			}
		}

		return null;
	}

}
