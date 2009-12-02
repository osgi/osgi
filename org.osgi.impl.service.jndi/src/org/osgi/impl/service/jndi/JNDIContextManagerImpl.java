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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.DirContext;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.framework.Bundle;
import org.osgi.service.jndi.JNDIContextManager;

class JNDIContextManagerImpl implements JNDIContextManager {

	private final InitialContextFactoryBuilder	m_builder;
	
	private final Bundle m_callingBundle;

	JNDIContextManagerImpl(Bundle callingBundle) {
		// create a new builder for each client bundle
		// since the JNDI services (factories) should be accessed
		// by the JNDIContextManager service on behalf of the calling bundle
		m_builder = new OSGiInitialContextFactoryBuilder(callingBundle.getBundleContext());
		m_callingBundle = callingBundle;
	}


	public Context newInitialContext() throws NamingException {
		InitialContextFactory factory = 
			m_builder.createInitialContextFactory(new Hashtable());
		return factory.getInitialContext(new Hashtable());
	}

	public Context newInitialContext(Hashtable environment)
			throws NamingException {
		return createNewInitialContext(environment);
	}

	public DirContext newInitialDirContext() throws NamingException {
		throw new OperationNotSupportedException("not supported yet");
	}

	public DirContext newInitialDirContext(Hashtable environment)
			throws NamingException {
		Context context = createNewInitialContext(environment);
		if(context instanceof DirContext) {
			return (DirContext)context;
		}
		
		throw new NamingException("DirContext could not be created.  The matching InitialContextFactory did not create a matching type.");
	}

	private Context createNewInitialContext(final Hashtable environment)
			throws NamingException {
		InitialContextFactory factory = 
			m_builder.createInitialContextFactory(environment);
		return factory.getInitialContext(environment);
	}

}
