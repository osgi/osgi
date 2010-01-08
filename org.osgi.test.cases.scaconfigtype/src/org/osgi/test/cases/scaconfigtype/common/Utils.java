package org.osgi.test.cases.scaconfigtype.common;

import static org.osgi.test.cases.scaconfigtype.common.DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED;

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
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Utils {
	public static List getSupportedConfigTypes(BundleContext ctx) throws Exception {
		// make sure there is a Distribution Provider installed in the framework
//		Filter filter = getFramework().getBundleContext().createFilter("(&(objectClass=*)(" +
//				DistributionProviderConstants.REMOTE_CONFIGS_SUPPORTED + "=*))");
		Filter filter = ctx.createFilter("(" +
				REMOTE_CONFIGS_SUPPORTED + "=*)");
		ServiceTracker dpTracker = new ServiceTracker(ctx, filter, null);
		dpTracker.open();
		
		Object dp = dpTracker.waitForService(10000L);
		
		if ( dp != null ) {
			ServiceReference dpReference = dpTracker.getServiceReference();

			if ( dpReference != null ) { 
				// collect all supported config types
				List supportedConfigTypes = propertyToList(dpReference.getProperty(REMOTE_CONFIGS_SUPPORTED)); 				
				dpTracker.close();
				return supportedConfigTypes;
			}			
		}
		
		dpTracker.close();		
		return Collections.EMPTY_LIST;
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
