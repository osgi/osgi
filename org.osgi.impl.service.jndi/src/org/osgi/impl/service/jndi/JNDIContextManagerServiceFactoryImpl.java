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
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;

class JNDIContextManagerServiceFactoryImpl implements ServiceFactory {

	// map of bundles to context managers (CloseableJNDIContextManager)
	private Map m_mapOfManagers = 
		Collections.synchronizedMap(new HashMap());
	
	/* BundleContext for the JNDI Implementation Bundle */
	private final BundleContext m_implBundleContext;
	
	JNDIContextManagerServiceFactoryImpl(BundleContext implBundleContext) {
		m_implBundleContext = implBundleContext;
	}
	
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		CloseableJNDIContextManager jndiContextManager = 
			createJNDIContextManager(bundle, m_implBundleContext);
		m_mapOfManagers.put(bundle, jndiContextManager);
		bundle.getBundleContext().addBundleListener(new ContextManagerBundleListener());
		return jndiContextManager;
	}

	

	public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
		closeContextManager(bundle);
	}
	
	protected void closeAll() {
		synchronized(m_mapOfManagers) {
			Iterator iterator = m_mapOfManagers.keySet().iterator();
			while(iterator.hasNext()) {
				Bundle bundleKey = (Bundle)iterator.next();
				closeContextManager(bundleKey);
			}
		}
	}

	private void closeContextManager(Bundle bundle) {
		CloseableJNDIContextManager jndiContextManager = 
			(CloseableJNDIContextManager)m_mapOfManagers.get(bundle);
		if(jndiContextManager != null) {
			jndiContextManager.close();
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
	
	
	/**
	 * Convenience factory method for creating a CloseableJNDIContextManager
	 * instance.  
	 * @param bundle the Bundle associated with this context manager
	 * @return a CloseableJNDIContextManager that will handle requests for 
	 *         the given Bundle.  
	 */
	private static CloseableJNDIContextManager createJNDIContextManager(Bundle bundle, BundleContext implBundleContext) {
		return new SecurityAwareJNDIContextManagerImpl(new JNDIContextManagerImpl(bundle, implBundleContext));
	}
}
