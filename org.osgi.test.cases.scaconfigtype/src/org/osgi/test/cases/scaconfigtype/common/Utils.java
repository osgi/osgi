package org.osgi.test.cases.scaconfigtype.common;

import static org.osgi.test.cases.scaconfigtype.common.DistributionProviderConstants.*;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Utils {
	public static List getSupportedConfigTypes(BundleContext ctx) throws Exception {
		return getServiceAdvert(ctx, REMOTE_CONFIGS_SUPPORTED);
	}
	
	public static List getSupportedIntentTypes(BundleContext ctx) throws Exception {
		return getServiceAdvert(ctx, REMOTE_INTENTS_SUPPORTED);
	}
	
	public static String fabricateValue(List values) {
		Assert.assertFalse(values.isEmpty());
		
		String type = (String) values.get(0);
		do {
			type += ".foo";
		}
		while ( values.contains( type ) );
		
		return type;

	}
	
	private static List getServiceAdvert(BundleContext ctx, String key) throws Exception {
		Filter filter = ctx.createFilter("(" +
				key + "=*)");
		ServiceTracker dpTracker = new ServiceTracker(ctx, filter, null);
		dpTracker.open();
		
		List vals = Collections.EMPTY_LIST;
		
		Object dp = dpTracker.waitForService(10000L);

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
			for (Iterator it=col.iterator(); it.hasNext(); ) {
				list.add(it.next());
			}
		} else { // assume String[]
			String[] arr = (String[])configProperty;
			for (int i=0; i<arr.length; i++) {
				list.add(arr[i]);
			}
		}
		
		return list;
	}


}
