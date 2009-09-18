/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.impl.service.jdbc;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;

/**
 * Creates a {@link DerbyEmbeddedDataSourceFactory} for the Derby JDBC driver.
 * 
 * @version $Revision$
 */
public class Activator implements BundleActivator {
    private ServiceRegistration dataSourceServiceRegistration;
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		Hashtable props = new Hashtable();
		props.put( DataSourceFactory.JDBC_DRIVER_CLASS, DerbyEmbeddedDataSourceFactory.JDBC_DRIVER_CLASS_PROPERTY_VALUE );
		props.put( DataSourceFactory.JDBC_DRIVER_NAME, DerbyEmbeddedDataSourceFactory.JDBC_DRIVER_NAME_PROPERTY_VALUE );
		props.put( DataSourceFactory.JDBC_DRIVER_VERSION, DerbyEmbeddedDataSourceFactory.JDBC_DRIVER_VERSION_PROPERTY_VALUE );

		dataSourceServiceRegistration = context.registerService( DataSourceFactory.class.getName(),
				new DerbyEmbeddedDataSourceFactory(), 
				props );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if ( dataSourceServiceRegistration != null ) {
			dataSourceServiceRegistration.unregister();
		}
	}

}
