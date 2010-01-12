/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.test.cases.scaconfigtype.common;

//import static org.osgi.test.cases.scaconfigtype.common.DistributionProviderConstants.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author <a href="mailto:david.savage@paremus.com">David Savage</a>
 *
 */
public class Utils {
	/**
	 * Creates the basic SCA attributes to export a service with the given sca binding name
	 * @param bindingName
	 * @return
	 */
	public static Hashtable getBasicSCAAttributes(String bindingName) {
		Hashtable<String, String> dictionary = new Hashtable<String, String>();
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, "*");
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_CONFIGS, SCAConfigConstants.ORG_OSGI_SCA_CONFIG);
		dictionary.put(SCAConfigConstants.ORG_OSGI_SCA_BINDING, bindingName);
		return dictionary;
	}

	/**
	 * Creates a new value not previously contained in this list
	 * @param values
	 * @return
	 */
	public static String fabricateValue(List values) {
		Assert.assertFalse(values.isEmpty());
		
		String type = (String) values.get(0);
		do {
			type += "foo";
		}
		while ( values.contains( type ) );
		
		return type;

	}
	
	/**
	 * Converts the property value to a list of values from the following classes:
	 *   <ul>
	 *   <li>String space delimited</li>
	 *   <li>Collection</ul>
	 *   <li>String[]</ul>
	 * @param configProperty
	 * @return
	 */
	public static List propertyToList(Object configProperty) {
		List list = new ArrayList(); // collect all supported config types
		if (configProperty instanceof String) {
			// TODO verify if space delimiter is valid for all sca properties
			StringTokenizer st = new StringTokenizer((String)configProperty, " ");
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
		} else if (configProperty instanceof Collection) {
			Collection col = (Collection) configProperty;
			list.addAll(col);
		} else { // assume String[]
			String[] arr = (String[])configProperty;
			for (int i=0; i<arr.length; i++) {
				list.add(arr[i]);
			}
		}
		
		return list;
	}

	/**
	 * Looks for a service that exports the specified key and returns the list of values
	 * associated with that key
	 * 
	 * @param ctx
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static List getServiceAdvert(BundleContext ctx, String key) throws Exception {
		Filter filter = ctx.createFilter("(" +
				key + "=*)");
		ServiceTracker dpTracker = new ServiceTracker(ctx, filter, null);
		dpTracker.open();
		
		List vals = Collections.EMPTY_LIST;
		
		Object dp = dpTracker.waitForService(TestConstants.SERVICE_TIMEOUT);

		if ( dp != null ) {
			ServiceReference dpReference = dpTracker.getServiceReference();

			if ( dpReference != null ) { 
				// collect all supported config types
				vals = propertyToList(dpReference.getProperty(key)); 				
				dpTracker.close();
				return vals;
			}			
		}
		
		dpTracker.close();		
		return vals;
	}

}
