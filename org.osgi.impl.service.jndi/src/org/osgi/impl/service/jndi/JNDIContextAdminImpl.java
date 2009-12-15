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
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.spi.ObjectFactory;

import org.osgi.framework.Bundle;
import org.osgi.service.jndi.JNDIContextAdmin;

class JNDIContextAdminImpl implements JNDIContextAdmin {

	private final OSGiInitialContextFactoryBuilder m_objectFactoryBuilder;
	
	public JNDIContextAdminImpl(Bundle callingBundle, OSGiInitialContextFactoryBuilder builder) {
		m_objectFactoryBuilder = builder;
	}
	
	
	public Object getObjectInstance(Object refInfo, Name name, Context context,
			Hashtable environment) throws NamingException {
		ObjectFactory objectFactory = 
			m_objectFactoryBuilder.createObjectFactory(refInfo, environment);
		try {
			return objectFactory.getObjectInstance(refInfo, name, context, environment);
		}
		catch (Exception e) {
			NamingException namingException = new NamingException("Error while attempting to resolve reference");
			namingException.initCause(e);
			throw namingException;
		}
	}

	public Object getObjectInstance(Object refInfo, Name name, Context context,
			Hashtable environment, Attributes attributes)
			throws NamingException {
		throw new OperationNotSupportedException("Not Implemented yet");
	}

}
