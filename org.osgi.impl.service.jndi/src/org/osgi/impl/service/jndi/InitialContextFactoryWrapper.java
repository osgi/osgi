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
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * A Wrapper implementation of InitialContextFactory, used to support URL
 * context factories in RFC 142.
 * 
 */
class InitialContextFactoryWrapper implements InitialContextFactory {
	private final InitialContextFactory	m_initialContextFactory;
	private final FactoryManager		m_factoryManager;

	public InitialContextFactoryWrapper(InitialContextFactory initialContextFactory, FactoryManager factoryManager) {
		m_initialContextFactory = initialContextFactory;
		m_factoryManager = factoryManager;
	}

	public Context getInitialContext(Hashtable environment) throws NamingException {
		final Context contextToReturn = 
			m_initialContextFactory.getInitialContext(environment);

		if(contextToReturn instanceof DirContext) {
			final DirContextWrapperImpl dirContextWrapper = new DirContextWrapperImpl((DirContext)contextToReturn, m_factoryManager);
			setupFactoryAssociation(dirContextWrapper);
			return ServiceAwareContextFactory.createServiceAwareDirContextWrapper(m_initialContextFactory, dirContextWrapper, m_factoryManager);
		} else {
			final ContextWrapperImpl contextWrapper = new ContextWrapperImpl(contextToReturn, m_factoryManager);
			setupFactoryAssociation(contextWrapper);
			return ServiceAwareContextFactory.createServiceAwareContextWrapper(m_initialContextFactory, contextWrapper, m_factoryManager);
		}
		
		
	}

	private void setupFactoryAssociation(final Context contextWrapper) {
		if(m_initialContextFactory instanceof BuilderSupportedInitialContextFactory) {
			BuilderSupportedInitialContextFactory builderFactory = 
				(BuilderSupportedInitialContextFactory)m_initialContextFactory;
			// this Context is backed by an InitialContextFactoryBuilder service
			m_factoryManager.associateFactoryService(builderFactory.getBuilder(), contextWrapper);
		} else {
			// this Context is backed by an InitialContextFactory service
			m_factoryManager.associateFactoryService(m_initialContextFactory, contextWrapper);
		}
	}
}
