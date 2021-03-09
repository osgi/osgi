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

package org.osgi.test.cases.provisioning.simple;
import java.security.AllPermission;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.provisioning.ProvisioningService;

public class Simple implements BundleActivator {
	
	public void start(BundleContext context) {
		Dictionary<String,Object> properties = new Hashtable<>();
		properties.put("test.case", "org.osgi.test.cases.provisioning" );
		if ( context.getBundle().hasPermission( new AllPermission() ) )
			properties.put( "allpermission", "true" );
		else
			properties.put( "allpermission", "false" );

		ServiceReference<ProvisioningService> ref = context
				.getServiceReference(ProvisioningService.class);
		ProvisioningService		ps 			= context.getService( ref );
		byte [] data = (byte[]) ps.getInformation().get( ProvisioningService.PROVISIONING_AGENT_CONFIG );
		if ( data != null )
			properties.put( "config.data", data );
		
		context.registerService(Bundle.class, context.getBundle(), properties);
	}
	public void stop( BundleContext context ) {
		// empty
	}
}
