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

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
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
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.xml.sax.SAXException;

public class BundleTest extends TestCase {
	public DummyBundleContext bundleContext;
	public Autoconf activator;
	public ResourceProcessor resourceProcessor;
	
	public final class DummyBundleContext implements BundleContext {
		public ServiceReference[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
			return new ServiceReference[] {
					new DummyServiceReference(clazz)
			};
		}
		public Object getService(ServiceReference reference) {
			// only one service is required here
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

		public ServiceRegistration registerService(String clazz, Object service, Dictionary properties) {
			if (clazz.equals(ResourceProcessor.class.getName())) {
				resourceProcessor = (ResourceProcessor) service;
				return null;
			}
			throw new IllegalStateException(); 
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
		public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary properties) { throw new IllegalStateException(); }
		public ServiceReference[] getAllServiceReferences(String clazz, String filter) throws InvalidSyntaxException { throw new IllegalStateException(); }
		public ServiceReference getServiceReference(String clazz) { throw new IllegalStateException(); }
		public boolean ungetService(ServiceReference reference) { throw new IllegalStateException(); }
		public File getDataFile(String filename) { throw new IllegalStateException();	}
		public Filter createFilter(String filter) throws InvalidSyntaxException { throw new IllegalStateException(); }
	};

	public final class DummyServiceReference implements ServiceReference {
		public final String serviceClass;
		public DummyServiceReference(String serviceClass) { this.serviceClass = serviceClass; }
		public Object getProperty(String key) { throw new IllegalStateException(); }
		public String[] getPropertyKeys() { throw new IllegalStateException(); }
		public Bundle getBundle() { throw new IllegalStateException(); }
		public Bundle[] getUsingBundles() { throw new IllegalStateException(); }
		public boolean isAssignableTo(Bundle bundle, String className) { throw new IllegalStateException(); }
	};

	protected void setUp() throws Exception {
		super.setUp();
		resourceProcessor = null;
		bundleContext = new DummyBundleContext();
		activator = new Autoconf();
		activator.start(bundleContext);
		
	}

	protected void tearDown() throws Exception {
		resourceProcessor = null;
		activator.stop(bundleContext);
		activator = null;
		bundleContext = null;
		super.tearDown();
	}

	public void test1() throws Exception {
		
	}
}
