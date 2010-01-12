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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.spi.InitialContextFactory;

/**
 * This class implements a default InitialContextFactory.  
 * 
 * The Context implementation created by this factory is a no-op
 * implementation of javax.naming.Context. 
 *
 * 
 * @version $Revision$
 */
class DefaultInitialContextFactory implements InitialContextFactory {

	public Context getInitialContext(Hashtable var0) throws NamingException {
		return (Context) Proxy.newProxyInstance(this.getClass().getClassLoader(),
				                                new Class[] {Context.class}, 
				                                new DefaultContextInvocationHandler());
	}
	
	
	/**
	 * InvocationHandler for the default Context.  Except for close() and getEnvironment(), 
	 * all Context method invocations should throw a NoInitialContextFactory exception.  
	 *
	 * 
	 * @version $Revision$
	 */
	private static class DefaultContextInvocationHandler implements InvocationHandler {

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// special case for close() invocation
			if(method.getName().equals("close")) {
				return null;
			}
			
			// special case for getEnvironment(), return empty Hashtable
			if(method.getName().equals("getEnvironment")) {
				return new Hashtable();
			}
			
			throw new NoInitialContextException("No InitialContext service available to handle this request");
		}
	}
}