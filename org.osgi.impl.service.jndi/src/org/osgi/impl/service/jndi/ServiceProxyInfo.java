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

/**
 * Data class to hold the information on a dynamic proxy 
 * for a given OSGi service, including whether the proxy 
 * was actually created.  
 *
 * 
 * @version $Revision$
 */
class ServiceProxyInfo {
	final Object m_service;
	final InvocationHandler m_handler;
	final boolean m_isProxied;
	
	ServiceProxyInfo(Object service, InvocationHandler handler, boolean isProxied) {
		m_service = service;
		m_handler = handler;
		m_isProxied = isProxied;
	}
	
	Object getService() {
		return m_service;
	}
	
	InvocationHandler getHandler() {
		return m_handler;
	}
	
	boolean isProxied() {
		return m_isProxied;
	}
}