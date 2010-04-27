/*
 * Copyright (c) OSGi Alliance (2008, 2009, 2010). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.remoteserviceadmin.junit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.remoteserviceadmin.EndpointDescription;
import org.osgi.service.remoteserviceadmin.EndpointListener;
import org.osgi.service.remoteserviceadmin.ExportRegistration;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdmin;
import org.osgi.test.cases.remoteserviceadmin.common.A;
import org.osgi.test.cases.remoteserviceadmin.common.B;
import org.osgi.test.cases.remoteserviceadmin.common.RemoteServiceConstants;
import org.osgi.test.cases.remoteserviceadmin.impl.TestServiceImpl;
import org.osgi.test.support.compatibility.Semaphore;

/**
 * Test the discovery portion of the spec by registering a EndpointDescription
 * in one framework and expecting it to show up in the other framework. 
 * 
 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
 * @version 1.0.0
 */
public class DiscoveryTest extends MultiFrameworkTestCase {
	private static final String	SYSTEM_PACKAGES_EXTRA	= "org.osgi.test.cases.remoteserviceadmin.system.packages.extra";

	private final static List<Class> SUPPORTED_TYPES = Arrays.asList(new Class[] {String.class,
		Long.TYPE, Long.class,
		Integer.TYPE, Integer.class,
		Byte.TYPE, Byte.class,
		Character.TYPE, Character.class,
		Double.TYPE, Double.class,
		Float.TYPE, Float.class,
		Boolean.TYPE, Boolean.class,
		Short.TYPE, Short.class});

	private long timeout;
	
	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		timeout = Long.getLong("rsa.ct.timeout", 300000L);
	}
	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		System.out.println("Sleep for 5s before ending to allow for cleanup of ports before the next run.");
		Thread.sleep(5000);
	}
	
	/**
	 * @see org.osgi.test.cases.remoteserviceadmin.junit.MultiFrameworkTestCase#getConfiguration()
	 */
	public Map<String, String> getConfiguration() {
		Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(Constants.FRAMEWORK_STORAGE_CLEAN, "true");
		
		//make sure that the server framework System Bundle exports the interfaces
		String systemPackagesXtra = System.getProperty(SYSTEM_PACKAGES_EXTRA);
        configuration.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, systemPackagesXtra);
        configuration.put("osgi.console", "" + (Integer.getInteger("osgi.console", 1111).intValue() + 1));
		return configuration;
	}
	
	/**
	 * 122.6 Discovery
	 */
	public void testDiscovery122_6() throws Exception {
		// verify that the server framework is exporting the test packages
		verifyFramework();
		
		//
		// install test bundle in child framework
		//
		BundleContext childContext = getFramework().getBundleContext();
		
		Bundle tb1Bundle = installBundle(childContext, "/tb1.jar");
		assertNotNull(tb1Bundle);
		
		//
		// register EndpointListener in parent framework
		//
		final EndpointListenerImpl endpointListenerImpl = new EndpointListenerImpl();
		
		final String endpointListenerFilter = "(!(org.osgi.framework.uuid=" + getContext().getProperty("org.osgi.framework.uuid") + "))";
		String secondFilter = "(mykey=has been overridden)";
		Hashtable<String, Object> endpointListenerProperties = new Hashtable<String, Object>();
		endpointListenerProperties.put(EndpointListener.ENDPOINT_LISTENER_SCOPE, new String[]{endpointListenerFilter,secondFilter});

		ServiceRegistration endpointListenerRegistration = getContext().registerService(
				EndpointListener.class.getName(), endpointListenerImpl, endpointListenerProperties);
		assertNotNull(endpointListenerRegistration);

		//
		// 122.6.1 Scope and Filters
		// register an EndpointListener w/o a scope. If called, then fail
		//
		EndpointListenerImpl emptyEndpointListener = scope_and_filter_122_6_1("");
		
		// start test bundle in child framework
		// this will run the test in the child framework and fail
		tb1Bundle.start();
		
		System.out.println("************* wait for Signal 1 ********************");
		// verify callback in parent framework
		endpointListenerImpl.getSemAdded().waitForSignal(timeout);
		
		// 122.6.2 callback has to return first matched filter
		assertEquals("filter doesn't match the first filter", endpointListenerFilter, endpointListenerImpl.getMatchedFilter());
		
		EndpointDescription ep = endpointListenerImpl.getAddedEndpoint(); 
		assertNotNull(ep);
		assertEquals("remote service id is incorrect", 12345, ep.getServiceId());
		assertEquals("remote.id does not match", "someURI", ep.getId());
		assertEquals("remote framework id is incorrect", getFramework()
				.getBundleContext().getProperty("org.osgi.framework.uuid"), ep
				.getFrameworkUUID());
		assertFalse(
				"remote framework id has to be UUID of remote not local framework",
				ep.getFrameworkUUID().equals(
						getContext().getProperty("org.osgi.framework.uuid")));
		assertTrue("discovered interfaces don't contain " + A.class.getName(), ep.getInterfaces().contains(A.class.getName()));
		assertFalse("discovered interfaces must not contain " + B.class.getName(), ep.getInterfaces().contains(B.class.getName()));
		assertEquals("the property of the service should have been overridden by the EndpointDescription", "has been overridden", ep.getProperties().get("mykey")); 
		assertEquals("the property myprop is missing", "myvalue", ep.getProperties().get("myprop"));
		
		// verify 122.6.1
		assertNull(emptyEndpointListener.getAddedEndpoint());
		
		System.out.println("************* Sleeping for 5s so that the discovery can settle ********************");
		Thread.sleep(5000);
		//
		// remove the endpoint
		//
		tb1Bundle.stop();

		System.out.println("************* wait for Signal 2 ********************");
		// verify callback in parent framework
		endpointListenerImpl.getSemRemoved().waitForSignal(timeout);
		
		// 122.6.2 callback has to return first matched filter
		assertEquals("filter doesn't match the first filter", endpointListenerFilter, endpointListenerImpl.getMatchedFilter());

		ep = endpointListenerImpl.getRemovedEndpoint();
		assertNotNull(ep);
		assertEquals("remote service id is incorrect", 12345, ep.getServiceId());
		assertEquals("remote.id does not match", "someURI", ep.getId());
		assertEquals("remote framework id is incorrect", getFramework()
				.getBundleContext().getProperty("org.osgi.framework.uuid"), ep
				.getFrameworkUUID());
		assertFalse(
				"remote framework id has to be UUID of remote not local framework",
				ep.getFrameworkUUID().equals(
						getContext().getProperty("org.osgi.framework.uuid")));
		assertTrue("discovered interfaces don't contain " + A.class.getName(), ep.getInterfaces().contains(A.class.getName()));
		assertFalse("discovered interfaces must not contain " + B.class.getName(), ep.getInterfaces().contains(B.class.getName()));
		assertEquals("the property of the service should have been overridden by the EndpointDescription", "has been overridden", ep.getProperties().get("mykey")); 
		assertEquals("the property myprop is missing", "myvalue", ep.getProperties().get("myprop"));
	}
	
	/**
	 * 122.6 Discovery
	 * 
	 * EndpointDescription via XML file
	 */
	public void testDiscovery122_6_8() throws Exception {
		Set<String> set = new HashSet<String>();
		set.add("one");
		set.add("two");
		List<String> list = new LinkedList<String>();
		list.add("first");
		list.add("second");
		
		// register a service and get the exported EndpointDescription
		Hashtable<String, Object> dictionary = new Hashtable<String, Object>();
		dictionary.put("mykey", "will be overridden");
		dictionary.put("myprop", "myvalue");
		dictionary.put("good test", true);
		dictionary.put("myset", set);
		dictionary.put(RemoteServiceConstants.SERVICE_EXPORTED_INTERFACES, "*");

		TestServiceImpl service = new TestServiceImpl();
		
		ServiceRegistration registration = getContext().registerService(new String[]{A.class.getName(), B.class.getName()}, service, dictionary);

		ServiceReference rsaRef = getContext().getServiceReference(RemoteServiceAdmin.class.getName());
		assertNotNull(rsaRef);
		RemoteServiceAdmin rsa = (RemoteServiceAdmin) getContext().getService(rsaRef);
		assertNotNull(rsa);
		
		Map<String, Object> properties = loadCTProperties();
		properties.put("mykey", "has been overridden");
		properties.put("mylist", list);
		properties.put("myfloat", (float)3.1415f);
		properties.put("mydouble", (double)-3.1415d);
		properties.put("mychar", (char)'t');
		properties.put("myxml", "<myxml>test</myxml>");
		
		// export the service
		Collection<ExportRegistration> exportRegistrations = rsa.exportService(registration.getReference(), properties);
		assertNotNull(exportRegistrations);
		assertFalse(exportRegistrations.isEmpty());
		
		EndpointDescription description = exportRegistrations.iterator().next().getExportReference().getExportedEndpoint();
		assertNotNull(description);
		
		// add a new property that only the XML description has
		Map<String, Object> props = new HashMap<String, Object>();
		props.putAll(description.getProperties());
		props.put("newkey", "newvalue");
		props.remove(RemoteConstants.ENDPOINT_FRAMEWORK_UUID); // this points to the parent framework and needs to be removed
		props.remove("service.id"); // this is specific to the parent framework
		description = new EndpointDescription(props);
		
		// create an XML file version of the description
		String xmlStr = toXml(description);
		System.out.println(xmlStr);
		
		// verify that the server framework is exporting the test packages
		verifyFramework();
		BundleContext childContext = getFramework().getBundleContext();
		
		// create a bundle and start the bundle in the child framework
		String testbundleloc = createBundle(xmlStr);
		
		Bundle testbundle = childContext.installBundle(testbundleloc);
		assertNotNull(testbundle);
		
		testbundle.start();
		
		//
		// install test bundle in child framework
		//
		
		Bundle tb3Bundle = installBundle(childContext,"/tb3.jar");
		assertNotNull(tb3Bundle);
		
		tb3Bundle.start(); // throws Exception if test was not successful
		
		Thread.sleep(2000);
		
		// remove the proxy
		testbundle.stop();

		tb3Bundle.stop(); // throws Exception if test was not successful
	}

	/**
	 * @param xmlStr
	 * @return
	 * @throws IOException 
	 */
	private String createBundle(String xmlStr) throws IOException {
		File bundle = File.createTempFile("rsatck", ".jar");

		Manifest manifest = new Manifest();
		Attributes attrs = manifest.getMainAttributes();
		attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		attrs.put(new Attributes.Name(
				org.osgi.framework.Constants.BUNDLE_NAME),
				"RemoteServiceAdmin TCK");
		attrs.put(new Attributes.Name(org.osgi.framework.Constants.BUNDLE_SYMBOLICNAME), "org.osgi.test.cases.remoteserviceadmin.testbundle");
		attrs.put(new Attributes.Name(org.osgi.framework.Constants.BUNDLE_MANIFESTVERSION), "2");
		attrs.put(new Attributes.Name(org.osgi.framework.Constants.BUNDLE_VERSION),	"1.0.0");
		attrs.put(new Attributes.Name(org.osgi.framework.Constants.BUNDLE_VENDOR), "OSGi Alliance");
		attrs.put(new Attributes.Name("Remote-Service"), "endpoint.xml");

		JarOutputStream jos = new JarOutputStream(new FileOutputStream(bundle), manifest);

		JarEntry je = new JarEntry("endpoint.xml");
		jos.putNextEntry(je);

		jos.write(xmlStr.getBytes());

		jos.flush();
		jos.closeEntry();
		jos.close();
		
		return bundle.toURI().toString();
	}

	/**
	 * @param description
	 * @return
	 */
	private String toXml(EndpointDescription description) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
		sb.append("<endpoint-descriptions xmlns=\"http://www.osgi.org/xmlns/rsa/v1.0.0\">").append("\n");
		sb.append("<endpoint-description>").append("\n");
		
		String str;
		for (Iterator<Map.Entry<String, Object>> it = description.getProperties().entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Object> entry = it.next();
			Object value = entry.getValue();
			Class<?> clazz = value.getClass();
			str = null;

			if (SUPPORTED_TYPES.contains(clazz)) {
				String type = clazz.getName();
				type = type.substring(type.lastIndexOf(".") != -1 ? type.lastIndexOf(".") + 1 : 0);
				
				if ("String".equals(type) && ((String)value).startsWith("<")) {
					str = ">\n <xml>\n" + value.toString() + "\n </xml>";
					
				} else {
					str = " value-type=\"" + type + "\" value=\"" + value.toString() + "\">";
				}
			} else if (value instanceof List<?>) {
				str = asList((List<?>)value);
			} else if (value instanceof Set<?>) {
				str = asSet((Set<?>)value);
			} else if ((str = isArray(value)) != null) {
				
			} else {
				System.err.println("unsupported property type " + clazz.getName());
				continue;
			}
			
			sb.append(" <property name=\"").append(entry.getKey()).append("\"");
			sb.append(str).append("\n");
			sb.append(" </property>").append("\n");
		}
		
		sb.append("</endpoint-description>").append("\n");
		sb.append("</endpoint-descriptions>");
		
		return sb.toString();
	}
	
	/**
	 * @param value
	 * @return
	 */
	private String isArray(Object value) {
		if (!value.getClass().isArray()) {
			return null;
		}
		
		Class type = value.getClass().getComponentType();
		if (!SUPPORTED_TYPES.contains(type)) {
			System.err.println("unsupported array type " + type);
		}
		
		String str = type.getName();
		str = str.substring(str.lastIndexOf(".") != -1 ? str.lastIndexOf(".") + 1 : 0);

		
		StringBuffer sb = new StringBuffer();
		sb.append(" value-type=\"").append(str).append("\">\n");
		sb.append("  <array>").append("\n");
		
		Object[] arr = (Object[]) value;
		for (int i = 0; i < arr.length; i++) {
			sb.append("   <value>").append(arr[i].toString()).append("</value>").append("\n");
		}
		
		sb.append("  </array>");
		return sb.toString();
	}

	/**
	 * @param value
	 * @return
	 */
	private String asList(List<?> value) {
		StringBuffer sb = new StringBuffer();
		
		if (value.size() > 0) {
			Class<?> type = value.iterator().next().getClass();
			if (!SUPPORTED_TYPES.contains(type)) {
				System.err.println("unsupported list type " + type);
			}

			String str = type.getName();
			str = str.substring(str.lastIndexOf(".") != -1 ? str.lastIndexOf(".") + 1 : 0);

			sb.append(" value-type=\"").append(str).append("\">\n");
		} else {
			sb.append("\">\n");
		}
		
		
		sb.append("  <list>").append("\n");
		for (Iterator<?> it = value.iterator(); it.hasNext(); ) {
			sb.append("   <value>").append(it.next().toString()).append("</value>").append("\n");
		}
		sb.append("  </list>");

		return sb.toString();
	}

	/**
	 * @param value
	 * @return
	 */
	private String asSet(Set<?> value) {
		StringBuffer sb = new StringBuffer();
		
		if (value.size() > 0) {
			Class<?> type = value.iterator().next().getClass();
			if (!SUPPORTED_TYPES.contains(type)) {
				System.err.println("unsupported list type " + type);
			}

			String str = type.getName();
			str = str.substring(str.lastIndexOf(".") != -1 ? str.lastIndexOf(".") + 1 : 0);

			sb.append(" value-type=\"").append(str).append("\">\n");
		} else {
			sb.append("\">\n");
		}
		
		sb.append("  <set>").append("\n");
		for (Iterator<?> it = value.iterator(); it.hasNext(); ) {
			sb.append("   <value>").append(it.next().toString()).append("</value>").append("\n");
		}
		sb.append("  </set>");

		return sb.toString();
	}

	/**
	 * Test empty filter scope
	 */
	private EndpointListenerImpl scope_and_filter_122_6_1(String scope) throws Exception {
		Hashtable<String, String> elp = new Hashtable<String, String>();
		elp.put(EndpointListener.ENDPOINT_LISTENER_SCOPE, scope);
		
		EndpointListenerImpl el = new EndpointListenerImpl();
		ServiceRegistration elr = getContext().registerService(EndpointListener.class.getName(), el, elp);
		assertNotNull(elr);
		
		return el;
	}
	
	

	class EndpointListenerImpl implements EndpointListener {
		private Semaphore semAdded = new Semaphore(0);
		private Semaphore semRemoved = new Semaphore(0);
		private String matchedFilter;
		private EndpointDescription addedEndpoint;
		private EndpointDescription removedEndpoint;

		/**
		 * @see org.osgi.service.remoteserviceadmin.EndpointListener#endpointAdded(org.osgi.service.remoteserviceadmin.EndpointDescription, java.lang.String)
		 */
		public void endpointAdded(EndpointDescription endpoint,
				String matchedFilter) {
			
			this.addedEndpoint = endpoint;
			this.matchedFilter = matchedFilter;
			semAdded.signal();
		}

		/**
		 * @see org.osgi.service.remoteserviceadmin.EndpointListener#endpointRemoved(org.osgi.service.remoteserviceadmin.EndpointDescription, java.lang.String)
		 */
		public void endpointRemoved(EndpointDescription endpoint,
				String matchedFilter) {
			
			this.removedEndpoint = endpoint;
			this.matchedFilter = matchedFilter;
			semRemoved.signal();
		}
		
		/**
		 * @return the sem
		 */
		public Semaphore getSemAdded() {
			return semAdded;
		}
		
		/**
		 * @return the sem
		 */
		public Semaphore getSemRemoved() {
			return semRemoved;
		}
		
		/**
		 * @return the matchedFilter
		 */
		public String getMatchedFilter() {
			return matchedFilter;
		}
		
		/**
		 * @return the addedEndpoint
		 */
		public EndpointDescription getAddedEndpoint() {
			return addedEndpoint;
		}
		
		/**
		 * @return the removedEndpoint
		 */
		public EndpointDescription getRemovedEndpoint() {
			return removedEndpoint;
		}
	}
}
