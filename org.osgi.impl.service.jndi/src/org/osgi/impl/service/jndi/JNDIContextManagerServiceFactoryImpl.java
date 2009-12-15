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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;

class JNDIContextManagerServiceFactoryImpl implements ServiceFactory {

	// map of bundles to context managers
	private Map m_mapOfManagers = 
		Collections.synchronizedMap(new HashMap());
	
	JNDIContextManagerServiceFactoryImpl() {
	}
	
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		final JNDIContextManagerImpl jndiContextManagerImpl = new JNDIContextManagerImpl(bundle);
		m_mapOfManagers.put(bundle, jndiContextManagerImpl);
		bundle.getBundleContext().addBundleListener(new ContextManagerBundleListener());
		return jndiContextManagerImpl;
	}

	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		closeContextManager(bundle);
	}

	private void closeContextManager(Bundle bundle) {
		JNDIContextManagerImpl jndiContextManagerImpl = 
			(JNDIContextManagerImpl)m_mapOfManagers.get(bundle);
		if(jndiContextManagerImpl != null) {
			jndiContextManagerImpl.close();
			m_mapOfManagers.remove(bundle);
		}
	}
	
	
	private class ContextManagerBundleListener implements SynchronousBundleListener {
		public void bundleChanged(BundleEvent event) {
			if(event.getType() == BundleEvent.STOPPED) {
				if(m_mapOfManagers.containsKey(event.getBundle())) {
					closeContextManager(event.getBundle());
				}
			}
		}
		
	}

}
