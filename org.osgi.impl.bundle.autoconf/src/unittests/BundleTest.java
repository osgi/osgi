/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package unittests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
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
import org.osgi.impl.bundle.autoconf.Autoconf;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.xml.sax.SAXException;
import unittests.metadata.AD;
import unittests.metadata.Attribute;
import unittests.metadata.Designate;
import unittests.metadata.MetaData;
import unittests.metadata.OCD;
import unittests.metadata.Object;
import unittests.metadata.ObjectFactory;

public class BundleTest extends TestCase {
	public DummyBundleContext bundleContext;
	public Autoconf activator;
	public ResourceProcessor resourceProcessor;
	public DummyConfigurationAdmin configurationAdmin;
	public DummyMetaTypeService metaTypeService;
	public DummyDeploymentAdmin deploymentAdmin;
	JAXBContext jaxbContext;
	ObjectFactory of;
	
	public final class DummyBundleContext implements BundleContext {
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
				try {
					return sp.newSAXParser();
				}
				catch (ParserConfigurationException e) {
					throw new IllegalStateException();
				}
				catch (SAXException e) {
					throw new IllegalStateException();
				}
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
		
		public String getProperty(String key) {	throw new IllegalStateException(); }
		public Bundle getBundle() { throw new IllegalStateException(); }
		public Bundle installBundle(String location) throws BundleException { throw new IllegalStateException(); }
		public Bundle installBundle(String location, InputStream input) throws BundleException { throw new IllegalStateException(); }
		public Bundle getBundle(long id) { throw new IllegalStateException();	}
		public Bundle[] getBundles() { throw new IllegalStateException();	}
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
		public File getDataFile(String filename) { throw new IllegalStateException();	}
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

		public String getPid() { throw new IllegalStateException(); }
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

		public Configuration createFactoryConfiguration(String factoryPid) throws IOException { throw new IllegalStateException(); }
		public Configuration createFactoryConfiguration(String factoryPid, String location) throws IOException { throw new IllegalStateException(); }
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
	}
	
	public final class DummyDeploymentPackage implements DeploymentPackage {
		public ArrayList bundles = new ArrayList();
		public Bundle[] listBundles() {
			Bundle[] b = new Bundle[bundles.size()];
			return (Bundle[]) bundles.toArray(b);
		}
		public long getId() { throw new IllegalStateException(); }
		public String getName() { throw new IllegalStateException(); }
		public String getVersion() { throw new IllegalStateException(); }
		public void uninstall() { throw new IllegalStateException(); }
		public boolean isNew(Bundle b) { throw new IllegalStateException(); }
		public boolean isUpdated(Bundle b) { throw new IllegalStateException(); }
		public boolean isPendingRemoval(Bundle b) { throw new IllegalStateException(); }
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
		public String getSymbolicName() { throw new IllegalStateException(); }
		public Class loadClass(String name) throws ClassNotFoundException { throw new IllegalStateException(); }
		public Enumeration getResources(String name) { throw new IllegalStateException(); }
		public Enumeration getEntryPaths(String path) { throw new IllegalStateException(); }
		public URL getEntry(String name) { throw new IllegalStateException(); }
		public long getLastModified() { throw new IllegalStateException(); }
		public Enumeration findEntries(String path, String filePattern, boolean recurse){ throw new IllegalStateException(); }  
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
		bundleContext = new DummyBundleContext();
		metaTypeService = new DummyMetaTypeService();
		configurationAdmin = new DummyConfigurationAdmin();
		deploymentAdmin = new DummyDeploymentAdmin();
		activator = new Autoconf();
		activator.start(bundleContext);
		
	}

	protected void tearDown() throws Exception {
		resourceProcessor = null;
		activator.stop(bundleContext);
		activator = null;
		bundleContext = null;
		configurationAdmin = null;
		deploymentAdmin = null;
		metaTypeService = null;
		super.tearDown();
	}

	public void testNothing() throws Exception {}

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
		dp.bundles.add(new DummyBundle("foo","1.1"));
		resourceProcessor.begin(dp,ResourceProcessor.INSTALL);

		MetaData md = of.createMetaData();
		md.getOCD().add(createOCD1());

		Designate d = of.createDesignate();
		md.getDesignate().add(d);
		d.setFactory(false);
		d.setBundle("foo-1.1");
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

		resourceProcessor.complete(true);
		
		DummyConfiguration conf = (DummyConfiguration) configurationAdmin.configurations.get("pid1");
		assertEquals("data",conf.properties.get("ad_string1"));
		assertEquals(new Integer(2),conf.properties.get("ad_int1"));
		assertArrayEquals(new int[] { 1,2 }, conf.properties.get("ad_intArray"));
		assertArrayEquals(new String[] { "foo", "bar" }, conf.properties.get("ad_stringArray"));
	}
}
