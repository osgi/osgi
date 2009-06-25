/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.cases.framework.junit.service;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.support.OSGiTestCase;

public class ServiceRegistryTests extends OSGiTestCase {

	public void testServiceListener01() {
		// simple ServiceListener test
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		final boolean[] results = new boolean[] {false, false, false, false};
		ServiceListener testListener = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				switch (event.getType()) {
					case ServiceEvent.REGISTERED :
						results[0] = true;
						break;
					case ServiceEvent.MODIFIED :
						results[1] = true;
						break;
					case ServiceEvent.MODIFIED_ENDMATCH :
						results[2] = true;
						break;
					case ServiceEvent.UNREGISTERING :
						results[3] = true;
						break;
				}
			}
		};
		try {
			getContext()
					.addServiceListener(
							testListener,
							"(&(objectClass=java.lang.Runnable)(" + getName()
							+ "=true))");  
		} catch (InvalidSyntaxException e) {
			fail("filter error", e); 
		}
		ServiceRegistration reg = null;
		try {
			// register service which matches
			Hashtable props = new Hashtable();
			props.put(getName(), Boolean.TRUE);
			reg = getContext().registerService(Runnable.class.getName(), runIt,
					props);
			assertTrue("Did not get ServiceEvent.REGISTERED", results[0]);
			assertFalse("Did get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// change props to still match
			props.put("testChangeProp", Boolean.FALSE); 
			reg.setProperties(props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertTrue("Did not get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// change props to no longer match
			props.put(getName(), Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertFalse("Did get ServiceEvent.MODIFIED", results[1]);
			assertTrue("Did not get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// change props to no longer match
			props.put("testChangeProp", Boolean.TRUE); 
			reg.setProperties(props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertFalse("Did get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// change props back to match
			props.put(getName(), Boolean.TRUE);
			reg.setProperties(props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertTrue("Did not get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// unregister
			reg.unregister();
			reg = null;
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertFalse("Did get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertTrue("Did not get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);
		} finally {
			getContext().removeServiceListener(testListener);
			if (reg != null)
				reg.unregister();
		}
	}

	public void testServiceListener02() {
		// simple ServiceListener test
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		final boolean[] results = new boolean[] {false, false, false, false};
		ServiceListener testListener = new ServiceListener() {
			public void serviceChanged(ServiceEvent event) {
				switch (event.getType()) {
					case ServiceEvent.REGISTERED :
						results[0] = true;
						break;
					case ServiceEvent.MODIFIED :
						results[1] = true;
						break;
					case ServiceEvent.MODIFIED_ENDMATCH :
						results[2] = true;
						break;
					case ServiceEvent.UNREGISTERING :
						results[3] = true;
						break;
				}
			}
		};
		try {
			getContext()
					.addServiceListener(
							testListener,
							"(&(objectClass=java.lang.Runnable)(" + getName()
							+ "=true))");  
		} catch (InvalidSyntaxException e) {
			fail("filter error", e); 
		}
		ServiceRegistration reg = null;
		try {
			// register service which does not match
			Hashtable props = new Hashtable();
			props.put(getName(), Boolean.FALSE);
			reg = getContext().registerService(Runnable.class.getName(), runIt,
					props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertFalse("Did get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// change props to still not match
			props.put("testChangeProp", Boolean.FALSE); 
			reg.setProperties(props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertFalse("Did get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// change props to match
			props.put(getName(), Boolean.TRUE);
			reg.setProperties(props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertTrue("Did not get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// change props to still match
			props.put("testChangeProp", Boolean.TRUE); 
			reg.setProperties(props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertTrue("Did not get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// change props to no longer match
			props.put(getName(), Boolean.FALSE);
			reg.setProperties(props);
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertFalse("Did get ServiceEvent.MODIFIED", results[1]);
			assertTrue("Did not get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);

			// unregister
			reg.unregister();
			reg = null;
			assertFalse("Did get ServiceEvent.REGISTERED", results[0]);
			assertFalse("Did get ServiceEvent.MODIFIED", results[1]);
			assertFalse("Did get ServiceEvent.MODIFIED_ENDMATCH", results[2]);
			assertFalse("Did get ServiceEvent.UNREGISTERING", results[3]); 
			clearResults(results);
		} finally {
			getContext().removeServiceListener(testListener);
			if (reg != null)
				reg.unregister();
		}
	}

	public void testServiceOrdering01() {
		// test that getServiceReference returns the proper service
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		Hashtable props = new Hashtable();
		props.put("name", getName());
		props.put(Constants.SERVICE_DESCRIPTION, "min value"); 
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MIN_VALUE));
		ServiceRegistration reg1 = getContext().registerService(
				Runnable.class.getName(), runIt, props);

		props.put(Constants.SERVICE_DESCRIPTION, "max value first"); 
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration reg2 = getContext().registerService(
				Runnable.class.getName(), runIt, props);

		props.put(Constants.SERVICE_DESCRIPTION, "max value second"); 
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration reg3 = getContext().registerService(
				Runnable.class.getName(), runIt, props);

		try {
			ServiceReference ref = null;
			ref = getContext().getServiceReference(Runnable.class.getName());
			assertNotNull("service ref is null", ref);
			assertEquals("Wrong references", reg2.getReference(), ref); 
		} finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	public void testDuplicateObjectClass() {
		ServiceRegistration reg = null;
		try {
			reg = getContext().registerService(
					new String[] {Runnable.class.getName(),
							Object.class.getName(), Object.class.getName()},
					new Runnable() {
				public void run() {
					// nothing
				}
			}, null);
		} catch (Throwable t) {
			fail("Failed to register service with duplicate objectClass names",
					t); 
		} finally {
			if (reg != null)
				reg.unregister();
		}
	}

	private void clearResults(boolean[] results) {
		for (int i = 0; i < results.length; i++)
			results[i] = false;
	}

	public void testEventSpan() throws Exception {
	    final boolean[] visited = new boolean[1];
	    ServiceListener sl = new ServiceListener() {
	        public void serviceChanged( ServiceEvent e ) {
	        	synchronized (visited) {
					visited[0] = true;
				}
	        }
	    };
	    
	    getContext().addServiceListener(sl);
        synchronized (visited) {
			visited[0] = false;
		}
        
        String[] classes = new String[] {Marker1.class.getName(),
				Marker2.class.getName(),};
        Hashtable properties = new Hashtable();
		properties.put("test", "yes");
        
	    ServiceRegistration registration = getContext().registerService(
				classes, new ConcreteMarker(30), properties);

	    boolean result;
		synchronized (visited) {
			result = visited[0];
		}
		assertTrue("Visited should now be true after registration ", result);
	    
        synchronized (visited) {
			visited[0] = false;
		}
	    registration.setProperties(properties);
        synchronized (visited) {
			result = visited[0];
		}
        assertTrue("Visited should now be true after setProperties ", result);
	    
        synchronized (visited) {
			visited[0] = false;
		}
		registration.unregister();
		synchronized (visited) {
			result = visited[0];
		}
		assertTrue("Visited should now be true after unregistration ", result);
	    
	    getContext().removeServiceListener(sl);
	}
	
	public void testBasicRegistration() throws Exception {
		String[] classes = new String[] {Marker1.class.getName(),
				Marker2.class.getName(),};
		Hashtable properties = new Hashtable();
		properties.put("test", "yes");
		ServiceRegistration registration = getContext().registerService(
				classes, new ConcreteMarker(1), properties);
		assertNotNull(registration);
		registration.setProperties(properties);
		registration.unregister();
	}

	public void testBasicFactory() {
		String[] classes = new String[] {Marker1.class.getName(),
				Marker2.class.getName(),};
		Hashtable properties = new Hashtable();
		properties.put("test", "yes");
		ServiceRegistration registration = getContext().registerService(
				classes, new ServiceFactory() {
					int	counter	= 2;
					Hashtable	services	= new Hashtable();

					public Object getService(Bundle bundle,
							ServiceRegistration reg) {
						Object service = new ConcreteMarker(counter++);
						services.put(bundle, service);
						return service;
					}

					public void ungetService(Bundle bundle,
							ServiceRegistration reg, Object service) {
						Object expected = services.remove(bundle);
						assertEquals("wrong service sent to factory", expected,
								service);
					}
				}, properties);

		ServiceReference reference = getContext().getServiceReference(
				Marker1.class.getName());
		Marker1 service1 = (Marker1) getContext().getService(reference);
		assertEquals("wrong service returned", 2, service1.getValue());
		
		Object service12 = getContext().getService(reference);
		assertSame("wrong service returned", service1, service12);
		Object service13 = getContext().getService(reference);
		assertSame("wrong service returned", service1, service13);
		Object service14 = getContext().getService(reference);
		assertSame("wrong service returned", service1, service14);
		Object service15 = getContext().getService(reference);
		assertSame("wrong service returned", service1, service15);

		assertTrue(getContext().ungetService(reference));
		assertTrue(getContext().ungetService(reference));
		assertTrue(getContext().ungetService(reference));
		assertTrue(getContext().ungetService(reference));
		assertTrue(getContext().ungetService(reference));
		assertFalse(getContext().ungetService(reference));
		assertFalse(getContext().ungetService(reference));
		assertFalse(getContext().ungetService(reference));
		

		reference = getContext().getServiceReference(Marker2.class.getName());
		Marker2 service2 = (Marker2) getContext().getService(reference);
		assertEquals("wrong service returned", 3, service2.getValue());
		assertTrue(getContext().ungetService(reference));

		registration.unregister();
		try {
			registration.unregister();
			fail("! Invalid, should have thrown IllegalStateException");
		}
		catch (IllegalStateException e) {
			// ignore
		}
	}

	public void testFactoryGetException() {
		String[] classes = new String[] {Marker1.class.getName(),
				Marker2.class.getName(),};
		Hashtable properties = new Hashtable();
		properties.put("test", "yes");

		ServiceRegistration registration = getContext().registerService(
				classes, new ServiceFactory() {
					public Object getService(Bundle bundle,
							ServiceRegistration reg) {
						throw new RuntimeException(
								"testFactoryException:getService");
					}

					public void ungetService(Bundle bundle,
							ServiceRegistration reg, Object service) {
						// empty
					}
				}, properties);

		ServiceReference reference = getContext().getServiceReference(
				Marker1.class.getName());
		assertNotNull(reference);
		
		Object service = getContext().getService(reference);
		assertNull(service);

		registration.unregister();
	}

	public void testFactoryUngetException() {
		String[] classes = new String[] {Marker1.class.getName(),
				Marker2.class.getName(),};
		Hashtable properties = new Hashtable();
		properties.put("test", "yes");

		ServiceRegistration registration = getContext().registerService(
				classes, new ServiceFactory() {
					public Object getService(Bundle bundle,
							ServiceRegistration reg) {
						return new ConcreteMarker(10);
					}

					public void ungetService(Bundle bundle,
							ServiceRegistration reg, Object service) {
						throw new RuntimeException(
								"testFactoryException:ungetService");
					}
				}, properties);

		ServiceReference reference = getContext().getServiceReference(
				Marker1.class.getName());
		assertNotNull(reference);

		Marker1 service = (Marker1) getContext().getService(reference);
		assertNotNull(service);
		assertEquals(10, service.getValue());

		getContext().ungetService(reference);

		registration.unregister();
	}
	
	public void testWrongClass() {
		String[] classes = new String[] {Marker1.class.getName(),
				Marker2.class.getName(),};
		Hashtable properties = new Hashtable();
		properties.put("test", "yes");
		try {
			ServiceRegistration registration = getContext().registerService(
					classes, "wrong class", properties);
			registration.unregister();
			fail("! Invalid, registration due to wrong classes");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testNullService() {
		String[] classes = new String[] {Marker1.class.getName(),
				Marker2.class.getName(),};
		Hashtable properties = new Hashtable();
		properties.put("test", "yes");
		try {
			ServiceRegistration registration = getContext().registerService(
					classes, null, properties);
			registration.unregister();
			fail("! Invalid, registration due to null service");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testManyRegistrations() throws Exception {
		String[] classes = new String[] {Marker1.class.getName(),
				Marker2.class.getName(),};
		Hashtable properties = new Hashtable();
		properties.put("test", "yes");
		ServiceRegistration[] registrations = new ServiceRegistration[1000];
		for (int i = 0; i < 1000; i++) {
			registrations[i] = getContext().registerService(classes,
					new ConcreteMarker(i + 1000), properties);
		}

		ServiceReference reference[] = getContext().getServiceReferences(
				Marker1.class.getName(), null);
		
		assertEquals("Should have 1000 references ", 1000, reference.length);

		for (int i = 0; i < 500; i++) {
			registrations[i].unregister();
		}

		reference = getContext().getServiceReferences(null,
				"(objectClass=" + Marker1.class.getName() + ")");
		assertEquals("Should have 500 references (via filter)", 500,
				reference.length);
		reference = getContext().getServiceReferences(Marker1.class.getName(),
				null);
		assertEquals("Should have 500 references (via class)", 500,
				reference.length);

		for (int i = 500; i < 1000; i++)
			registrations[i].unregister();

		reference = getContext().getServiceReferences(Marker1.class.getName(),
				null);
		assertNull("Should have 0 references (=null)", reference);
	}
	public void testServiceReferenceCompare01() {
		// test that getServiceReference returns the proper service
		Runnable runIt = new Runnable() {
			public void run() {
				// nothing
			}
		};
		Hashtable props = new Hashtable();
		props.put("name", getName());
		props.put(Constants.SERVICE_DESCRIPTION, "min value");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MIN_VALUE));
		ServiceRegistration reg1 = getContext().registerService(
				Runnable.class.getName(), runIt, props);
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration reg2 = getContext().registerService(
				Runnable.class.getName(), runIt, props);

		props.put(Constants.SERVICE_DESCRIPTION, "max value second");
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration reg3 = getContext().registerService(
				Runnable.class.getName(), runIt, props);

		try {
			ServiceReference ref = getContext().getServiceReference(
					Runnable.class.getName());
			ServiceReference ref1 = reg1.getReference();
			ServiceReference ref2 = reg2.getReference();
			ServiceReference ref3 = reg3.getReference();

			assertNotNull("service ref is null", ref);
			assertEquals("Wrong reference", ref2, ref);

			assertEquals("Wrong references", 0, ref2.compareTo(ref));
			assertEquals("Wrong references", 0, ref.compareTo(ref2));

			assertTrue("Wrong compareTo value: " + ref1.compareTo(ref1), ref1
					.compareTo(ref1) == 0);
			assertTrue("Wrong compareTo value: " + ref1.compareTo(ref2), ref1
					.compareTo(ref2) < 0);
			assertTrue("Wrong compareTo value: " + ref1.compareTo(ref3), ref1
					.compareTo(ref3) < 0);

			assertTrue("Wrong compareTo value: " + ref2.compareTo(ref1), ref2
					.compareTo(ref1) > 0);
			assertTrue("Wrong compareTo value: " + ref2.compareTo(ref2), ref2
					.compareTo(ref2) == 0);
			assertTrue("Wrong compareTo value: " + ref2.compareTo(ref3), ref2
					.compareTo(ref3) > 0);

			assertTrue("Wrong compareTo value: " + ref3.compareTo(ref1), ref3
					.compareTo(ref1) > 0);
			assertTrue("Wrong compareTo value: " + ref3.compareTo(ref2), ref3
					.compareTo(ref2) < 0);
			assertTrue("Wrong compareTo value: " + ref3.compareTo(ref3), ref3
					.compareTo(ref3) == 0);
		}
		finally {
			if (reg1 != null)
				reg1.unregister();
			if (reg2 != null)
				reg2.unregister();
			if (reg3 != null)
				reg3.unregister();
		}
	}

	static interface Marker1 {
		public int getValue();
	}

	static interface Marker2 {
		public int getValue();
	}

	static class ConcreteMarker implements Marker1, Marker2 {
		int	value;

		ConcreteMarker(int n) {
			value = n;
		}

		public int getValue() {
			return value;
		}
	}
}
