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
package unittests.util;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import junit.framework.TestCase;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.DmtAdminActivator;
import org.osgi.impl.service.dmt.api.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.service.dmt.DmtAlertSender;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.event.ChannelEvent;
import org.osgi.service.event.EventChannel;

/**
 *
 * Generic utility for testing Dmt and Dmt Plugin interaction.
 * 
 * @version $Revision$
 */
public class DmtPluginTestCase extends TestCase {
	public DmtAdminActivator dmtAdminActivator;
	public BundleContext dmtBundleContext;
	public ServiceListener	dmtRemoteAlertSenderServiceListener;
	public ServiceListener newServiceTracker;
	public DmtAdmin dmtFactory;
	public DmtAlertSender dmtAlertSender;
	public DmtPrincipalPermissionAdmin dmtPrincipalPermissionAdmin;
	
	public class DummyContext implements BundleContext {
		public ServiceReference getServiceReference(String clazz) {
			if (clazz.equals(EventChannel.class.getName())) {
				return new DummyServiceReference(new DummyEventChannel());
			}
			throw new IllegalStateException();
		}

		public Object getService(ServiceReference reference) {
			if (reference instanceof DummyServiceReference) {
				return ((DummyServiceReference)reference).serviceObject;
			}
			throw new IllegalStateException();
		}

		public Filter createFilter(String filter) throws InvalidSyntaxException {
			if (filter.equals("(|(objectClass=org.osgi.service.dmt.DmtDataPlugin)(objectClass=org.osgi.service.dmt.DmtExecPlugin))")){
				return new DummyFilter("plugin");
			}
			if (filter.equals("(objectClass=org.osgi.impl.service.dmt.api.RemoteAlertSender)")) {
				return new DummyFilter("remotealertsender");
			}
			throw new IllegalStateException();
		}

		public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
			if (filter==null) {
				newServiceTracker = listener;
				return;
			}
			if (filter.equals("(objectClass=org.osgi.impl.service.dmt.api.RemoteAlertSender)")) {
				dmtRemoteAlertSenderServiceListener = listener;
				return;
			}
			throw new IllegalArgumentException();
		}

		public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
			if (RemoteAlertSender.class.getName().equals(clazz)) {
				return new ServiceReference[0];
			}
			if ("plugin".equals(filter)) {
				return new ServiceReference[0];
			}
			throw new IllegalStateException();
		}

		public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
			if (DmtAdmin.class.getName().equals(clazz)) {
				dmtFactory = (DmtAdmin) service;
				return null;
			}
			if (DmtAlertSender.class.getName().equals(clazz)) {
				dmtAlertSender = (DmtAlertSender) service;
				return null;
			}
			if (DmtPrincipalPermissionAdmin.class.getName().equals(clazz)) {
				dmtPrincipalPermissionAdmin = (DmtPrincipalPermissionAdmin) service;
				return null;
 			}
			throw new IllegalStateException();
		}
		
		// not used methods
		public String getProperty(String key) { throw new IllegalStateException();}
		public Bundle getBundle() { throw new IllegalStateException(); }
		public Bundle installBundle(String location) throws BundleException { throw new IllegalStateException();}
		public Bundle installBundle(String location, InputStream input) throws BundleException {throw new IllegalStateException();		}
		public Bundle getBundle(long id) {throw new IllegalStateException();}
		public Bundle[] getBundles() {throw new IllegalStateException();}
		public void addServiceListener(ServiceListener listener) { throw new IllegalStateException();}
		public void removeServiceListener(ServiceListener listener) {throw new IllegalStateException();	}
		public void addBundleListener(BundleListener listener) {throw new IllegalStateException();}
		public void removeBundleListener(BundleListener listener) {throw new IllegalStateException();}
		public void addFrameworkListener(FrameworkListener listener) {throw new IllegalStateException();}
		public void removeFrameworkListener(FrameworkListener listener) {throw new IllegalStateException();}
		public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {throw new IllegalStateException();}
		public boolean ungetService(ServiceReference reference) {throw new IllegalStateException();}
		public File getDataFile(String filename) {throw new IllegalStateException();}
	};
	
	public class DummyEventChannel implements EventChannel {
		public void postEvent(ChannelEvent event) {}
		public void sendEvent(ChannelEvent event) {}
	}
	
	public class DummyServiceReference implements ServiceReference {
		public Object serviceObject;
		public String[] dataRootURIs;
		public DummyServiceReference(Object serviceObject) {
			this.serviceObject = serviceObject;
		}
		public Object getProperty(String key) {
			if ("dataRootURIs".equals(key)) {
				return dataRootURIs;
			}
			if ("execRootURIs".equals(key)) {
				return null;
			}
			throw new IllegalStateException();
		}
		public String[] getPropertyKeys() {	throw new IllegalStateException();}
		public Bundle getBundle() {	throw new IllegalStateException();}
		public Bundle[] getUsingBundles() {	throw new IllegalStateException();}
	}

	public class DummyFilter implements Filter {
		public String filter;
		public DummyFilter(String filter) { this.filter = filter; }
		public String toString() {
			return filter;
		}
		public boolean match(ServiceReference reference) {
			if (reference instanceof DummyServiceReference) {
				DummyServiceReference dummyRef = (DummyServiceReference)reference;
				if (dummyRef.serviceObject instanceof DmtDataPlugin) {
					return true;
				}
			}
			throw new IllegalStateException();
		}
		public boolean match(Dictionary dictionary) { throw new IllegalStateException();}
		public boolean matchCase(Dictionary dictionary) { throw new IllegalStateException();}
	}
	
	public void setUp() throws Exception {
		dmtAdminActivator = new DmtAdminActivator();
		dmtBundleContext = new DummyContext(); 
		dmtAdminActivator.start(dmtBundleContext);
	}
	
	public void tearDown() throws Exception {
		dmtAlertSender = null;
		dmtFactory = null;
		dmtRemoteAlertSenderServiceListener = null;
		dmtAdminActivator = null;
		dmtBundleContext = null;
	}
	
	public void addDataPlugin(String URI,DmtDataPlugin plugin) {
		DummyServiceReference ref = new DummyServiceReference(plugin);
		ref.dataRootURIs = new String[] {URI};
		ServiceEvent e = new ServiceEvent(ServiceEvent.REGISTERED,ref);
		newServiceTracker.serviceChanged(e);
	}
}
