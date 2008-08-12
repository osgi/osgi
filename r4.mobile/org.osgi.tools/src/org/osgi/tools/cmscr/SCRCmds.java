package org.osgi.tools.cmscr;

import java.util.*;

import org.osgi.framework.*;
import org.osgi.tools.command.*;
import org.osgi.util.tracker.ServiceTracker;


/**
 * This is NOT a declarative service because it tests the XML file of the
 * declarative services.
 * 
 * @version $Revision$
 */

public class SCRCmds implements BundleActivator, CommandProvider {
	BundleContext		context;
	ServiceRegistration	registration;	// Ourselves
	ServiceTracker		tracker;

	/*
	 * @see BundleActivator#start(BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		context.registerService(CommandProvider.class.getName(), this, null);
	}

	/*
	 * @see BundleActivator#stop(BundleContext)
	 */
	public void stop(BundleContext arg0) throws Exception {
	}

	/**
	 * Return a list with our commands.
	 */
	public String getHelp() {
		return "CM\r\n" + "\r\n";
	}

	/**
	 * List all the configuration objects.
	 */
	public Object _listscr(CommandInterpreter intp) throws Exception {
		return getScrBundles(null);
	}

	
	
	private Object getScrBundles(String id) {
		List bundles = Arrays.asList(context.getBundles());

		for (Iterator i = bundles.iterator(); i.hasNext();) {
			Bundle b = (Bundle) i.next();
			String components = (String) b.getHeaders(null).get(
					"Service-Component");
			if (components != null) {
				bundles.add( new BundleProxy(b) );
			}
		}
		return bundles;
	}

	public Object toString(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}



