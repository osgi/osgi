/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
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
package org.osgi.impl.service.policy.unittests.util;

import info.dmtree.DmtAdmin;
import info.dmtree.notification.NotificationService;
import info.dmtree.notification.spi.RemoteAlertSender;
import info.dmtree.spi.DataPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.DmtAdminActivator;
import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.impl.service.policy.unittests.DummyComponentContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 *
 * Generic utility for testing Dmt and Dmt Plugin interaction.
 * 
 * @version $Revision$
 */
public abstract class DmtPluginTestCase extends TestCase {
	public DmtAdminActivator dmtAdminActivator;
	public BundleContext dmtBundleContext;
	public ServiceListener	dmtRemoteAlertSenderServiceListener;
	public ServiceListener newServiceTracker;
	public ServiceListener	eventAdminServiceListener;
	public ServiceListener	configurationAdminServiceListener;
	public DmtAdmin dmtAdmin;
	public ServiceFactory dmtAdminFactory;
	public DmtPrincipalPermissionAdmin dmtPrincipalPermissionAdmin;
	public DummyConfigurationAdmin	configurationAdmin;
    static final String DMT_PERMISSION_ADMIN_SERVICE_PID = 
        "org.osgi.impl.service.dmt.perms";
	public DummyComponentContext context;
	public List deferredPluginReferences;
	
	public class DummyContext implements BundleContext {
		public ServiceReference getServiceReference(String clazz) {
			if (clazz.equals(EventAdmin.class.getName())) {
				return new DummyServiceReference(new DummyEventAdmin());
			}
			if (clazz.equals(ConfigurationAdmin.class.getName())){
				return new DummyServiceReference(configurationAdmin);
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
			if (filter.equals("(|(objectClass=info.dmtree.spi.DataPlugin)(objectClass=info.dmtree.spi.ExecPlugin))")){
				return new DummyFilter("plugin");
			}
			if (filter.equals("(objectClass=info.dmtree.notification.spi.RemoteAlertSender)")) {
				return new DummyFilter("remotealertsender");
			}
			if (filter.equals("(objectClass=org.osgi.service.event.EventAdmin)")) {
				return new DummyFilter("eventadmin");
			}
			if (filter.equals("(objectClass=org.osgi.service.cm.ConfigurationAdmin)")) {
				return new DummyFilter("configurationadmin");
			}
			throw new IllegalStateException("unknown filter: "+filter);
		}

		public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
			if (filter==null) {
				newServiceTracker = listener;
				return;
			}
			if (filter.equals("(objectClass=info.dmtree.notification.spi.RemoteAlertSender)")) {
				dmtRemoteAlertSenderServiceListener = listener;
				return;
			}
			if (filter.equals("(objectClass=org.osgi.service.event.EventAdmin)")) {
				eventAdminServiceListener = listener;
				return;
			}
			if (filter.equals("(objectClass=org.osgi.service.cm.ConfigurationAdmin)")) {
				configurationAdminServiceListener = listener;
				return;
			}
			throw new IllegalArgumentException("unknown filter: "+filter);
		}

		public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
			if (RemoteAlertSender.class.getName().equals(clazz)) {
				return new ServiceReference[0];
			}
			if (EventAdmin.class.getName().equals(clazz)) {
				return new ServiceReference[] { new DummyServiceReference(new DummyEventAdmin()) };
			}
			if (ConfigurationAdmin.class.getName().equals(clazz)) {
				return new ServiceReference[] { new DummyServiceReference(configurationAdmin) };
			}
			if ("plugin".equals(filter)) {
				return (ServiceReference[]) deferredPluginReferences.toArray(new ServiceReference[deferredPluginReferences.size()]);
			}
			throw new IllegalStateException(filter);
		}

		public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
			if (DmtAdmin.class.getName().equals(clazz)) {
				dmtAdminFactory = (ServiceFactory)service;
				return null;
			}
			if (DmtPrincipalPermissionAdmin.class.getName().equals(clazz)) {
				dmtPrincipalPermissionAdmin = (DmtPrincipalPermissionAdmin) service;
				return null;
 			}
			if (ManagedService.class.getName().equals(clazz)) {
				return null;
			}
			if (NotificationService.class.getName().equals(clazz)) {
				return null;
			}
			throw new IllegalStateException(clazz+" "+service+" "+properties);
		}

		public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) {
			for(int i=0;i<clazzes.length;i++) registerService(clazzes[i],service,properties);
			return null;
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
		public boolean ungetService(ServiceReference reference) {throw new IllegalStateException();}
		public File getDataFile(String filename) {throw new IllegalStateException();}
		public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException {throw new IllegalStateException();}
	};

	public class DummyConfiguration implements Configuration {
		public final String pid;
		public Hashtable data = new Hashtable();
		public DummyConfiguration(String pid) {
			this.pid = pid;
		}
		public void update(Dictionary properties) throws IOException {
			data = new Hashtable();
			data.putAll((Hashtable)properties);
		}

		public String getPid() { throw new IllegalStateException(); }
		public Dictionary getProperties() { throw new IllegalStateException(); }
		public void delete() throws IOException { throw new IllegalStateException(); }
		public String getFactoryPid() { throw new IllegalStateException(); }
		public void update() throws IOException { throw new IllegalStateException(); }
		public void setBundleLocation(String bundleLocation) { throw new IllegalStateException(); }
		public String getBundleLocation() { throw new IllegalStateException(); }
	}

	public class DummyConfigurationAdmin implements ConfigurationAdmin {
		public DummyConfiguration dmtPrincipalData = new DummyConfiguration(DMT_PERMISSION_ADMIN_SERVICE_PID);
		public Configuration getConfiguration(String pid) throws IOException { 
			if (pid.equals(DMT_PERMISSION_ADMIN_SERVICE_PID)) { return dmtPrincipalData; }	
			throw new IllegalStateException();
		}

		public Configuration createFactoryConfiguration(String factoryPid) throws IOException { throw new IllegalStateException(); }
		public Configuration createFactoryConfiguration(String factoryPid, String location) throws IOException { throw new IllegalStateException();		}
		public Configuration getConfiguration(String pid, String location) throws IOException { throw new IllegalStateException();}
		public Configuration[] listConfigurations(String filter) throws IOException, InvalidSyntaxException { throw new IllegalStateException();}
	}
	
	public class DummyEventAdmin implements EventAdmin {
		public void postEvent(Event event) {}
		public void sendEvent(Event event) {}
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
			if (Constants.SERVICE_DESCRIPTION.equals(key)) {
				return this.serviceObject.getClass().toString();
			}
			throw new IllegalStateException();
		}

		public Bundle getBundle() {
			return new DummyBundle();
		}
		
		public String[] getPropertyKeys() {	throw new IllegalStateException();}
		public Bundle[] getUsingBundles() {	throw new IllegalStateException();}
		public boolean isAssignableTo(Bundle bundle, String className) {	throw new IllegalStateException();}
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
				if (dummyRef.serviceObject instanceof DataPlugin) {
					return true;
				}
			}
			throw new IllegalStateException();
		}
		public boolean match(Dictionary dictionary) { throw new IllegalStateException();}
		public boolean matchCase(Dictionary dictionary) { throw new IllegalStateException();}
	}
	
	public class DummyBundle implements Bundle {
		public int getState() { throw new IllegalStateException();}		
		public void start() throws BundleException { throw new IllegalStateException();}
		public void stop() throws BundleException { throw new IllegalStateException();}
		public void update() throws BundleException { throw new IllegalStateException();}
		public void update(InputStream in) throws BundleException { throw new IllegalStateException();}
		public void uninstall() throws BundleException { throw new IllegalStateException();}
		public Dictionary getHeaders() { throw new IllegalStateException();}
		public long getBundleId() { throw new IllegalStateException();}
		public String getLocation() { throw new IllegalStateException();}
		public ServiceReference[] getRegisteredServices() { throw new IllegalStateException();}
		public ServiceReference[] getServicesInUse() { throw new IllegalStateException();}
		public boolean hasPermission(Object permission) { throw new IllegalStateException();}
		public URL getResource(String name) { throw new IllegalStateException();}
		public Dictionary getHeaders(String locale) { throw new IllegalStateException();}
		public String getSymbolicName() { throw new IllegalStateException();}
		public Class loadClass(String name) throws ClassNotFoundException { throw new IllegalStateException();}
		public Enumeration getResources(String name) throws IOException { throw new IllegalStateException();}
		public Enumeration getEntryPaths(String path) { throw new IllegalStateException();}
		public URL getEntry(String name) { throw new IllegalStateException();}
		public long getLastModified() { throw new IllegalStateException();}
		public Enumeration findEntries(String path, String filePattern, boolean recurse) { throw new IllegalStateException();}
	}
	
	
	public void setUp() throws Exception {
		dmtAdminActivator = new DmtAdminActivator();
		dmtBundleContext = new DummyContext(); 
		configurationAdmin = new DummyConfigurationAdmin();
		dmtAdminActivator.start(dmtBundleContext);
		context = new DummyComponentContext();
		dmtAdmin = (DmtAdmin) dmtAdminFactory.getService(null,null);
		deferredPluginReferences = new ArrayList();
	}
	
	public void tearDown() throws Exception {
		dmtAdmin = null;
		dmtAdminFactory = null;
		configurationAdmin = null;
		dmtRemoteAlertSenderServiceListener = null;
		eventAdminServiceListener = null;
		configurationAdminServiceListener = null;
		dmtAdminActivator = null;
		dmtBundleContext = null;
		deferredPluginReferences = null;
	}
	
	public void addDataPlugin(String URI,DataPlugin plugin) {
		DummyServiceReference ref = new DummyServiceReference(plugin);
		ref.dataRootURIs = new String[] {URI};
		if (newServiceTracker!=null) {
			ServiceEvent e = new ServiceEvent(ServiceEvent.REGISTERED,ref);
			newServiceTracker.serviceChanged(e);
		} else {
			deferredPluginReferences.add(ref);
		}
	}
}
