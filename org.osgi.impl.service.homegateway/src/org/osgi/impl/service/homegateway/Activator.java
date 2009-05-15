package org.osgi.impl.service.homegateway;

import org.osgi.framework.*;
import info.dmtree.*;
import org.osgi.util.tracker.*;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		ServiceTracker st = new ServiceTracker(context,DmtAdmin.class.getName(), null);
		st.open();
		
		DmtAdmin dmtadmin = (DmtAdmin) st.getService();
		DmtSession session = dmtadmin.getSession(null);
	}

	public void stop(BundleContext context) throws Exception {
	}

}
