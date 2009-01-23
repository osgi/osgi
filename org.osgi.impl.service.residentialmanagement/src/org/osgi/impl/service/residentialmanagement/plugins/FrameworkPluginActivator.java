
package org.osgi.impl.service.residentialmanagement.plugins;

import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import info.dmtree.spi.DataPlugin;

public class FrameworkPluginActivator implements BundleActivator {
	static final String INSTANCE_ID = "1";
	static final String[] PLUGIN_ROOT_PATH = 
        new String[] { ".", "OSGi", INSTANCE_ID, "Framework" };
    static final String PLUGIN_ROOT_URI = "./OSGi/" + INSTANCE_ID + "/Framework";
    
    private ServiceRegistration servReg;
    private FrameworkPlugin     frameworkPlugin;

	public void start(BundleContext bc) throws BundleException {
 		frameworkPlugin = new FrameworkPlugin(bc);
		Hashtable props = new Hashtable();
		props.put("dataRootURIs", new String[] { PLUGIN_ROOT_URI });
		String[] ifs = new String[] {DataPlugin.class.getName()};
		servReg = bc.registerService(ifs, frameworkPlugin, props);
		
		System.out.println("Framework plugin activated successfully.");
	}

	public void stop(BundleContext bc) throws BundleException {
		servReg.unregister();
		
		System.out.println("Framework plugin stopped successfully.");
	}
}
