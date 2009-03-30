package org.osgi.impl.service.residentialmanagement.plugins;

import info.dmtree.spi.DataPlugin;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class FiltersPluginActivator implements BundleActivator {
	static final String INSTANCE_ID = "1";
	static final String[] PLUGIN_ROOT_PATH = 
        new String[] { ".", "OSGi", INSTANCE_ID, "Filters" };
    static final String PLUGIN_ROOT_URI = "./OSGi/" + INSTANCE_ID + "/Filters";
    
    private ServiceRegistration servReg;
    private FiltersPlugin     filtersPlugin;
    
	public void start(BundleContext bc) throws Exception {
 		filtersPlugin = new FiltersPlugin(bc);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] { PLUGIN_ROOT_URI });
		String[] ifs = new String[] {DataPlugin.class.getName()};
		servReg = bc.registerService(ifs, filtersPlugin, props);
		
		System.out.println("Filters plugin activated successfully.");

	}

	public void stop(BundleContext bc) throws Exception {
		servReg.unregister();
		
		System.out.println("Filters plugin stopped successfully.");

	}

}
