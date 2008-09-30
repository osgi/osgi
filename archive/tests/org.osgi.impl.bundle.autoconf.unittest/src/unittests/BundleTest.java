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
package unittests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.impl.bundle.autoconf.Autoconf;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;

import unittests.metadata.AD;
import unittests.metadata.Attribute;
import unittests.metadata.Designate;
import unittests.metadata.MetaData;
import unittests.metadata.OCD;
import unittests.metadata.Object;
import unittests.metadata.ObjectFactory;

public class BundleTest extends TestCase {
	public DummyBundleContext bundleContext;
	public ResourceProcessor resourceProcessor;
	public DummyConfigurationAdmin configurationAdmin;
	public DummyMetaTypeService metaTypeService;
	public DummyDeploymentAdmin deploymentAdmin;
	private DummyComponentContext componentContext;
	JAXBContext jaxbContext;
	ObjectFactory of;
	
	public final class DummyBundleContext implements BundleContext {
		public ArrayList bundles = new ArrayList();
		public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
			return new ServiceReference[] {
					new DummyServiceReference(clazz)
			};
		}
		public java.lang.Object getService(ServiceReference reference) {
			String clazz = ((DummyServiceReference)reference).serviceClass;
			if (clazz.equals(SAXParserFactory.class.getName())) {
				SAXParserFactory sp = SAXParserFactory.newInstance();
				sp.setNamespaceAware(true);
				sp.setValidating(true);
				return sp;
			}
			if (clazz.equals(ConfigurationAdmin.class.getName())) {
				return configurationAdmin;
			}
			if (clazz.equals(MetaTypeService.class.getName())) {
				return metaTypeService;
			}
			if (clazz.equals(DeploymentAdmin.class.getName())) {
				return deploymentAdmin;
			}
			throw new IllegalStateException();
		}

		public ServiceRegistration registerService(String clazz, java.lang.Object service, Dictionary properties) {
			if (clazz.equals(ResourceProcessor.class.getName())) {
				resourceProcessor = (ResourceProcessor) service;
				return null;
			}
			throw new IllegalStateException(); 
		}

		public ServiceReference getServiceReference(String clazz) {
			return new DummyServiceReference(clazz);
		}

		public File getDataFile(String filename) { 
			assertEquals("storedConfigurations",filename);
			try {
				File f = File.createTempFile("OSGi-autoconf-RI-storedConfigurations","dat");
				f.delete();
				f.deleteOnExit();
				return f;
			} catch (IOException e) {
				IllegalStateException e2 = new IllegalStateException();
				e2.initCause(e);
				throw e2;
			}
		}

		public Bundle[] getBundles() {
			Bundle[] b = new Bundle[bundles.size()];
			return (Bundle[]) bundles.toArray(b);
		}

		public String getProperty(String key) {	throw new IllegalStateException(); }
		public Bundle getBundle() { throw new IllegalStateException(); }
		public Bundle installBundle(String location) throws BundleException { throw new IllegalStateException(); }
		public Bundle installBundle(String location, InputStream input) throws BundleException { throw new IllegalStateException(); }
		public Bundle getBundle(long id) { throw new IllegalStateException();	}
		public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException { throw new IllegalStateException(); }
		public void addServiceListener(ServiceListener listener) { throw new IllegalStateException(); }
		public void removeServiceListener(ServiceListener listener) {throw new IllegalStateException();}
		public void addBundleListener(BundleListener listener) { throw new IllegalStateException(); }
		public void removeBundleListener(BundleListener listener) { throw new IllegalStateException(); }
		public void addFrameworkListener(FrameworkListener listener) { throw new IllegalStateException(); }
		public void removeFrameworkListener(FrameworkListener listener) { throw new IllegalStateException(); }
		public ServiceRegistration registerService(String[] clazzes, java.lang.Object service, Dictionary properties) { throw new IllegalStateException(); }
		public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException { throw new IllegalStateException(); }
		public boolean ungetService(ServiceReference reference) { throw new IllegalStateException(); }
		public Filter createFilter(String filter) throws InvalidSyntaxException { throw new IllegalStateException(); }
	};

	public final class DummyServiceReference implements ServiceReference {
		public final String serviceClass;
		public DummyServiceReference(String serviceClass) { this.serviceClass = serviceClass; }
		public java.lang.Object getProperty(String key) { throw new IllegalStateException(); }
		public String[] getPropertyKeys() { throw new IllegalStateException(); }
		public Bundle getBundle() { throw new IllegalStateException(); }
		public Bundle[] getUsingBundles() { throw new IllegalStateException(); }
		public boolean isAssignableTo(Bundle bundle, String className) { throw new IllegalStateException(); }
	};

	public final class DummyConfiguration implements Configuration {
		public Hashtable properties = new Hashtable();
		public final String pid;
		public String location=null;

		public DummyConfiguration(String pid) {
			this.pid = pid;
		}

		public void update(Dictionary properties) throws IOException {
			this.properties = new Hashtable();
			this.properties.putAll((Hashtable)properties);
		}

		public String getPid() {
			return pid;
		}
		
		public Dictionary getProperties() { throw new IllegalStateException(); }
		public void delete() throws IOException { throw new IllegalStateException(); }
		public String getFactoryPid() { throw new IllegalStateException(); }
		public void update() throws IOException { throw new IllegalStateException(); }
		public void setBundleLocation(String bundleLocation) { throw new IllegalStateException(); }
		public String getBundleLocation() { throw new IllegalStateException(); }
	}

	public final class DummyConfigurationAdmin implements ConfigurationAdmin {
		public HashMap configurations = new HashMap();
		public Configuration getConfiguration(String pid, String location) throws IOException {
			DummyConfiguration conf = (DummyConfiguration) configurations.get(pid);
			if (conf==null) {
				conf = new DummyConfiguration(pid);
				conf.location = location;
				configurations.put(pid,conf);
			}
			return conf;
		}

		public Configuration createFactoryConfiguration(String factoryPid, String location) throws IOException {
			String pid = "factory+"+factoryPid+location;
			DummyConfiguration factoryConf = (DummyConfiguration) configurations.get(pid);
			if (factoryConf==null) {
				factoryConf = new DummyConfiguration(pid);
				factoryConf.location = location;
				configurations.put(pid,factoryConf);
			}
			return factoryConf;
		}

		public Configuration createFactoryConfiguration(String factoryPid) throws IOException { throw new IllegalStateException(); }
		public Configuration getConfiguration(String pid) throws IOException { throw new IllegalStateException(); }
		public Configuration[] listConfigurations(String filter) throws IOException, InvalidSyntaxException { throw new IllegalStateException(); }
	}

	public final class DummyMetaTypeService implements MetaTypeService {
		public MetaTypeInformation getMetaTypeInformation(Bundle bundle) { throw new IllegalStateException(); }
	}

	public final class DummyDeploymentAdmin implements DeploymentAdmin {
		public DeploymentPackage installDeploymentPackage(InputStream in) { throw new IllegalStateException(); }
		public DeploymentPackage[] listDeploymentPackages() { throw new IllegalStateException(); }
		public String location(String symbName, String version) { return location(symbName,version); }
		public boolean cancel() { throw new IllegalStateException(); }
		public DeploymentPackage getDeploymentPackage(String symbName) { throw new IllegalStateException(); }
		public DeploymentPackage getDeploymentPackage(Bundle bundle) { throw new IllegalStateException(); }
	}
	
	public final class DummyDeploymentPackage implements DeploymentPackage {
		public ArrayList bundles = new ArrayList();
		public Bundle[] listBundles() {
			Bundle[] b = new Bundle[bundles.size()];
			return (Bundle[]) bundles.toArray(b);
		}
		public Bundle getBundle(String bundleSymName) { 
			for (Iterator i = bundles.iterator(); i.hasNext();) {
				Bundle b = (Bundle) i.next();
				if (b.getSymbolicName().equals(bundleSymName)) return b;
			}
			return null;
		}
		
		public String getName() { return "dummyDep1"; }

		public long getId() { throw new IllegalStateException(); }
		public Version getVersion() { throw new IllegalStateException(); }
		public void uninstall() { throw new IllegalStateException(); }
		public boolean isNew(Bundle b) { throw new IllegalStateException(); }
		public boolean isUpdated(Bundle b) { throw new IllegalStateException(); }
		public boolean isPendingRemoval(Bundle b) { throw new IllegalStateException(); }
		public File getDataFile(Bundle bundle) { throw new IllegalStateException(); }
		public String[][] getBundleSymNameVersionPairs() { throw new IllegalStateException(); }
		public String[] getResources() { throw new IllegalStateException(); }
		public ServiceReference getResourceProcessor(String resource) { throw new IllegalStateException(); }
		public String getHeader(String name) { throw new IllegalStateException(); }
		public String getResourceHeader(String path, String header) { throw new IllegalStateException(); }
		public boolean uninstallForced() { throw new IllegalStateException(); }
		public boolean isStale() { throw new IllegalStateException(); }
		public BundleInfo[] getBundleInfos() { throw new IllegalStateException(); }
	}

	public final class DummyDeploymentSession implements DeploymentSession {
		public DummyDeploymentPackage sourceDeploymentPackage;
		public DeploymentPackage getSourceDeploymentPackage() { return sourceDeploymentPackage; }

		public int getDeploymentAction() { throw new IllegalStateException(); }
		public DeploymentPackage getTargetDeploymentPackage() { throw new IllegalStateException(); }
		public File getDataFile(Bundle bundle) { throw new IllegalStateException(); }
	}

	public class DummyBundle implements Bundle {
		Hashtable headers = new Hashtable();
		final String location;
		
		public DummyBundle(String symbolicName,String version) {
			headers.put("Bundle-SymbolicName",symbolicName);
			headers.put("Bundle-Version",version);
			location = "bundle://"+symbolicName+"-"+version;
		}

		public String getLocation() { return location; }

		public Dictionary getHeaders(String localeString) { return headers; }
		public Dictionary getHeaders() { return headers; }
		public String getSymbolicName() { return (String) headers.get("Bundle-SymbolicName"); }

		public int getState() { throw new IllegalStateException(); }
		public void start() throws BundleException { throw new IllegalStateException(); }
		public void stop() throws BundleException { throw new IllegalStateException(); }
		public void update() throws BundleException { throw new IllegalStateException(); }
		public void update(InputStream in) throws BundleException { throw new IllegalStateException(); }
		public void uninstall() throws BundleException { throw new IllegalStateException(); }
		public long getBundleId() { throw new IllegalStateException(); }
		public ServiceReference[] getRegisteredServices() { throw new IllegalStateException(); }
		public ServiceReference[] getServicesInUse() { throw new IllegalStateException(); }
		public boolean hasPermission(java.lang.Object permission) { throw new IllegalStateException(); }
		public URL getResource(String name) { throw new IllegalStateException(); }
		public Class loadClass(String name) throws ClassNotFoundException { throw new IllegalStateException(); }
		public Enumeration getResources(String name) { throw new IllegalStateException(); }
		public Enumeration getEntryPaths(String path) { throw new IllegalStateException(); }
		public URL getEntry(String name) { throw new IllegalStateException(); }
		public long getLastModified() { throw new IllegalStateException(); }
		public Enumeration findEntries(String path, String filePattern, boolean recurse){ throw new IllegalStateException(); }  
	}
	
	public final class DummyComponentContext implements ComponentContext {
		public HashMap services = new HashMap();
		public BundleContext bundleContext;
		public java.lang.Object locateService(String name) {
			return services.get(name);
		}	
		public BundleContext getBundleContext() { 
			return bundleContext;
		}

		public Dictionary getProperties() { throw new IllegalStateException(); }
		public java.lang.Object locateService(String name, ServiceReference reference) { throw new IllegalStateException(); }
		public java.lang.Object[] locateServices(String name) { throw new IllegalStateException(); }
		public Bundle getUsingBundle() { throw new IllegalStateException(); }
		public ComponentInstance getComponentInstance() { throw new IllegalStateException(); }
		public void enableComponent(String name) { throw new IllegalStateException(); }
		public void disableComponent(String name) { throw new IllegalStateException(); }
		public ServiceReference getServiceReference() { throw new IllegalStateException(); }
	}

	public static String location(String symbname,String version) {
		return "bundle://"+symbname+"-"+version;
	}
	
	public InputStream getStream(MetaData md) throws JAXBException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		jaxbContext.createMarshaller().marshal(md,bos);
		byte[] data = bos.toByteArray();
		return new ByteArrayInputStream(data);
	}

	protected void setUp() throws Exception {
		super.setUp();
		of = new ObjectFactory();
		jaxbContext = JAXBContext.newInstance("unittests.metadata");
		resourceProcessor = null;
		componentContext = new DummyComponentContext();
		bundleContext = new DummyBundleContext();
		componentContext.bundleContext = bundleContext;
		metaTypeService = new DummyMetaTypeService();
		componentContext.services.put("metaTypeService",metaTypeService);
		configurationAdmin = new DummyConfigurationAdmin();
		componentContext.services.put("configurationAdmin",configurationAdmin);
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		saxParserFactory.setValidating(false);
		componentContext.services.put("saxParserFactory",saxParserFactory);
		resourceProcessor = new Autoconf();
		Method activate = resourceProcessor.getClass().getDeclaredMethod("activate",new Class[] {ComponentContext.class});
		activate.setAccessible(true);
		activate.invoke(resourceProcessor,new java.lang.Object[] {componentContext});
//		resourceProcessor = new Autoconf(bundleContext,configurationAdmin,metaTypeService,saxParserFactory);
		
	}

	protected void tearDown() throws Exception {
		resourceProcessor = null;
		bundleContext = null;
		configurationAdmin = null;
		deploymentAdmin = null;
		metaTypeService = null;
		super.tearDown();
	}

	private OCD createOCD1() throws Exception {
		OCD ocd = of.createOCD();
		ocd.setId("ocd1");
		ocd.setName("ocdName1");
		
		AD ad;
		
		ad = of.createAD();
		ad.setId("ad_string1");
		ad.setCardinality(0);
		ad.setType("String");
		ocd.getAD().add(ad);

		ad = of.createAD();
		ad.setId("ad_string1");
		ad.setCardinality(0);
		ad.setType("String");
		ocd.getAD().add(ad);

		ad = of.createAD();
		ad.setId("ad_stringArray");
		ad.setCardinality(4);
		ad.setType("String");
		ocd.getAD().add(ad);
		
		ad = of.createAD();
		ad.setId("ad_stringVector");
		ad.setCardinality(-4);
		ad.setType("String");
		ocd.getAD().add(ad);
		
		ad = of.createAD();
		ad.setId("ad_intArray");
		ad.setCardinality(4);
		ad.setType("Integer");
		ocd.getAD().add(ad);

		ad = of.createAD();
		ad.setId("ad_intVector");
		ad.setCardinality(-4);
		ad.setType("Integer");
		ocd.getAD().add(ad);

		ad = of.createAD();
		ad.setId("ad_intVector_unlimited");
		ad.setCardinality(Integer.MIN_VALUE);
		ad.setType("Integer");
		ocd.getAD().add(ad);

		ad = of.createAD();
		ad.setId("ad_int1");
		ad.setCardinality(0);
		ad.setType("Integer");
		ocd.getAD().add(ad);

		ad = of.createAD();
		ad.setId("ad_int2");
		ad.setCardinality(0);
		ad.setType("Integer");
		ocd.getAD().add(ad);

		return ocd;
	}

	public void assertArrayEquals(java.lang.Object o1,java.lang.Object o2) {
		Class c1 = o1.getClass();
		Class c2 = o2.getClass();
		assertEquals(c2,c1);
		assertTrue(c1.isArray());
		if (int[].class.equals(c1)) {
			assertTrue(Arrays.equals((int[])o1,(int[])o2));
		} else {
			assertTrue(Arrays.equals((java.lang.Object[])o1,(java.lang.Object[])o2));
		}
		
	}
	
	public void testBasic() throws Exception {
		DummyDeploymentPackage dp = new DummyDeploymentPackage();
		DummyBundle dummyBundle = new DummyBundle("foo","1.1");
		dp.bundles.add(dummyBundle);
		bundleContext.bundles.add(dummyBundle);
		DummyDeploymentSession ds = new DummyDeploymentSession();
		ds.sourceDeploymentPackage = dp;
		resourceProcessor.begin(ds);

		MetaData md = of.createMetaData();
		md.getOCD().add(createOCD1());

		Designate d = of.createDesignate();
		md.getDesignate().add(d);
		d.setFactoryPid("factorypid1");
		d.setBundle("bundle://foo-1.1");
		d.setPid("pid1");
		Object o = of.createObject();
		d.setObject(o);
		o.setOcdref("ocd1");

		Attribute attr = of.createAttribute();
		attr.setAdref("ad_string1");
		attr.setContent("data");
		o.getAttribute().add(attr);
		
		attr = of.createAttribute();
		attr.setAdref("ad_int1");
		attr.setContent("2");
		o.getAttribute().add(attr);
		
		attr = of.createAttribute();
		attr.setAdref("ad_intArray");
		attr.setContent("1");
		o.getAttribute().add(attr);
		
		attr = of.createAttribute();
		attr.setAdref("ad_intArray");
		attr.setContent("2");
		o.getAttribute().add(attr);
		
		attr = of.createAttribute();
		attr.setAdref("ad_intVector");
		attr.setContent("1");
		o.getAttribute().add(attr);

		attr = of.createAttribute();
		attr.setAdref("ad_intVector");
		attr.setContent("2");
		o.getAttribute().add(attr);
		
		attr = of.createAttribute();
		attr.setAdref("ad_stringArray");
		attr.setContent("foo");
		o.getAttribute().add(attr);

		attr = of.createAttribute();
		attr.setAdref("ad_stringArray");
		attr.setContent("bar");
		o.getAttribute().add(attr);

		//jaxbContext.createMarshaller().marshal(md,System.out);
		resourceProcessor.process("foo/autoconf.xml",getStream(md));

		resourceProcessor.commit();
		
		DummyConfiguration conf = (DummyConfiguration) configurationAdmin.configurations.get("factory+factorypid1bundle://foo-1.1");
		assertEquals("data",conf.properties.get("ad_string1"));
		assertEquals(new Integer(2),conf.properties.get("ad_int1"));
		assertArrayEquals(new int[] { 1,2 }, conf.properties.get("ad_intArray"));
		assertArrayEquals(new String[] { "foo", "bar" }, conf.properties.get("ad_stringArray"));
	}
}
