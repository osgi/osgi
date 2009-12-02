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

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
class JNDIContextAdminServiceFactoryImpl implements ServiceFactory {

	private final OSGiInitialContextFactoryBuilder m_builder;
	
	public JNDIContextAdminServiceFactoryImpl(OSGiInitialContextFactoryBuilder builder) {
		m_builder = builder;
	}
	
	/**
	 * @param bundle
	 * @param registration
	 * @return
	 * @see org.osgi.framework.ServiceFactory#getService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration)
	 */
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		return new JNDIContextAdminImpl(bundle, m_builder);
	}

	/**
	 * @param bundle
	 * @param registration
	 * @param service
	 * @see org.osgi.framework.ServiceFactory#ungetService(org.osgi.framework.Bundle, org.osgi.framework.ServiceRegistration, java.lang.Object)
	 */
	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object service) {
		// TODO Auto-generated method stub
		// TODO, add cleanup code here
	}

}
