package org.osgi.test.cases.dmt.tc4.tb1;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.dmt.spi.DataPlugin;

public class Activator implements BundleActivator {
	// This is what it should be, but the DMT RI has a hard coded set of valid nodes
//	private static final String[] ROOT_URI = {"./OSGi/1/Framework"};
//	private static final String[] MOUNT_POINT = {"./OSGi/1/Framework/InstallBundle/#"};
	private static final String[] ROOT_URI = {"./OSGi/_Framework"};
	private static final String[] MOUNT_POINT = {"Bundles/#"};
	
	private ServiceRegistration<DataPlugin>	reg;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Dictionary<String,Object> props = new Hashtable<>(2);
		DataPlugin dp = new FrameworkDP(context);
		String bundleName = context.getBundle().getSymbolicName();
		System.out.println( "\nStarting Bundle [" + bundleName + "]" );
		
		props.put("dataRootURIs", ROOT_URI);
		props.put("mountPoints", MOUNT_POINT);
		reg = context.registerService(DataPlugin.class, dp, props);
		
		System.out.println("[" + bundleName + "] registered a Data Plugin with:\n\t [" +
				arrToStr(ROOT_URI) + "] as the 'dataRootURIs' property\n\t [" + 
				arrToStr(MOUNT_POINT) + "] as the 'mountPoints' property.");
	}

	public void unregister() {
		if ( reg != null )
			reg.unregister();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		String bundleName = context.getBundle().getSymbolicName();
		System.out.println( "Stopping Bundle [" + bundleName + "]" );
	}

	
	private String arrToStr( String[] input ) {
		StringBuffer sb = new StringBuffer();
		
		for ( int inx = 0 ; inx < input.length ; inx++ ) {
			sb.append(input[inx]);
			if ( inx < input.length - 1 ) {
				sb.append(", ");
			}
		}
		
		return sb.toString();
	}
}
