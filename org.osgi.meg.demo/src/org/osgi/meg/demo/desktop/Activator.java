/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.meg.demo.desktop;

import java.net.MalformedURLException;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.application.*;
import org.osgi.service.dmt.DmtException;

/**
 * Controller part of the MVC pattern.
 */
public class Activator implements BundleActivator {
	private Model	model;
	private Desktop	desktop;

	public Activator() {
		super();
	}

	public void start(BundleContext context) throws Exception {
		try {
			desktop = new Desktop(this);
			model = new Model(context, desktop);
			model.open();
			desktop.setModel(model);
			new Thread(model).start();
			desktop.setVisible(true);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void stop(BundleContext context) throws Exception {
		model.destroy();
		desktop.hide();
	}

	public void installApp(String url) throws MalformedURLException,
			DmtException {
		model.installApp(url);
	}

	public void launchApp(ApplicationDescriptor descr) throws Exception {
		model.launchApp(descr);
	}

	public void uninstallApp(ApplicationDescriptor descr) throws Exception {
		model.uninstallApp(descr);
	}

	public void stopApp(ApplicationHandle handle) throws Exception {
		model.stopApp(handle);
	}

	public void suspendApp(ApplicationHandle handle) throws Exception {
		model.suspendApp(handle);
	}

	public void resumeApp(ApplicationHandle handle) throws Exception {
		model.resumeApp(handle);
	}

	public void scheduleOnEvent(String topic, String props,
			ApplicationDescriptor descr) {
		// TODO
		Hashtable ht = new Hashtable();
		ht.put("bundle.id", new Long(props));
		/*
		 * String[] pairs = Splitter.split(props, ',', 0); for (int i = 0; i <
		 * pairs.length; ++i) { String[] pair = Splitter.split(pairs[i], '=',
		 * 0); ht.put(pair[0], pair[1]); }
		 */
		model.scheduleOnEvent(topic, ht, descr);
	}

	public void scheduleOnDate(Date date, ApplicationDescriptor descr) {
		model.scheduleOnDate(date, descr);
	}
}
