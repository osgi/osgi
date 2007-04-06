/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.io.tbc;

import org.osgi.test.cases.util.*;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Constants;
import org.osgi.service.io.ConnectionFactory;
import org.osgi.service.io.ConnectorService;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import java.util.Hashtable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;

/**
 * Tests the functionality of a ConnectorService implementation
 */
public class IOControl extends DefaultTestBundleControl {
	BundleContext	bc;
	/**
	 * List of test methods used in this test case.
	 */
	static String[]	methods	= new String[] {"testOpen", "testRanking1",
			"testRanking2", "testMultipleSchemes",};

	/**
	 * Returns the list of test methods contained in this test case.
	 * 
	 * @return list of test methods
	 */
	public String[] getMethods() {
		return methods;
	}

	/**
	 * tests the open methods in ConnectorService
	 * 
	 * @throws Exception
	 */
	public void testOpen() throws Exception {
		bc = getContext();
		Hashtable props = new Hashtable();
		props.put(ConnectionFactory.IO_SCHEME, new String[] {"test"});
		TestConnectionFactory cf = new TestConnectionFactory();
		ServiceRegistration reg = bc.registerService(ConnectionFactory.class
				.getName(), cf, props);
		ServiceTracker st = new ServiceTracker(bc, ConnectorService.class
				.getName(), null);
		st.open();
		st.waitForService(5000);
		ConnectorService connector = (ConnectorService) st.getService();
		assertNotNull("connector service", connector);
		Connection c = connector.open("test://testurl");
		assertNotNull("returned connection", c);
		assertEquals("uri check", "test://testurl", cf.uri);
		cf.clean();
		boolean exc = false;
		try {
			connector.open("notavalidURI");
		}
		catch (IllegalArgumentException e) {
			exc = true;
		}
		assertTrue("illegal argument exception not thrown", exc);
		c = connector.open("test://readurl", ConnectorService.READ);
		assertNotNull("returned connection", c);
		assertEquals("mode", ConnectorService.READ, cf.mode);
		cf.clean();
		c = connector.open("test://writeurl", ConnectorService.WRITE);
		assertNotNull("returned connection", c);
		assertEquals("mode", ConnectorService.WRITE, cf.mode);
		cf.clean();
		c = connector.open("test://readwriteurl", ConnectorService.READ_WRITE);
		assertNotNull("returned connection", c);
		assertEquals("mode", ConnectorService.READ_WRITE, cf.mode);
		cf.clean();
		c = connector.open("test://testurl", ConnectorService.READ, true);
		assertNotNull("returned connection", c);
		assertTrue("timeouts", cf.timeouts);
		cf.clean();
		exc = false;
		try {
			Connection con = connector.open("notavalidscheme://notfoundurl");
			fail("Connection should not be created " + c);
		}
		catch (ConnectionNotFoundException cnf) {
			exc = true;
		}
		assertTrue("connection not found exception", exc);
		cf.clean();
		DataInputStream dis = connector.openDataInputStream("test://testurl");
		assertNotNull("openDataInputStream", dis);
		cf.clean();
		DataOutputStream dos = connector.openDataOutputStream("test://testurl");
		assertNotNull("openDataOutputStream", dos);
		cf.clean();
		InputStream is = connector.openInputStream("test://testurl");
		assertNotNull("openInputStream", is);
		cf.clean();
		InputStream os = connector.openDataInputStream("test://testurl");
		assertNotNull("openOutputStream", os);
		reg.unregister();
	}

	/**
	 * checks if the ConnectionFactory with the lowest service id is choosen
	 * 
	 * @throws Exception
	 */
	public void testRanking1() throws Exception {
		bc = getContext();
		Hashtable props = new Hashtable();
		props.put(ConnectionFactory.IO_SCHEME, new String[] {"test"});
		TestConnectionFactory cf1 = new TestConnectionFactory();
		ServiceRegistration reg1 = bc.registerService(ConnectionFactory.class
				.getName(), cf1, props);
		TestConnectionFactory cf2 = new TestConnectionFactory();
		ServiceRegistration reg2 = bc.registerService(ConnectionFactory.class
				.getName(), cf2, props);
		ServiceTracker st = new ServiceTracker(bc, ConnectorService.class
				.getName(), null);
		st.open();
		st.waitForService(5000);
		ConnectorService connector = (ConnectorService) st.getService();
		assertNotNull("connector service", connector);
		Connection c = connector.open("test://testurl");
		assertNotNull("returned connection", c);
		assertEquals("uri check lowest service.id", "test://testurl", cf1.uri);
		assertEquals("uri check highest service.id", null, cf2.uri);
		reg1.unregister();
		reg2.unregister();
	}

	/**
	 * checks if the ConnectionFactory with the highest service ranking is
	 * choosen
	 * 
	 * @throws Exception
	 */
	public void testRanking2() throws Exception {
		bc = getContext();
		Hashtable props = new Hashtable();
		props.put(ConnectionFactory.IO_SCHEME, new String[] {"test"});
		TestConnectionFactory cf1 = new TestConnectionFactory();
		ServiceRegistration reg1 = bc.registerService(ConnectionFactory.class
				.getName(), cf1, props);
		TestConnectionFactory cf2 = new TestConnectionFactory();
		props.put(Constants.SERVICE_RANKING, new Integer(Integer.MAX_VALUE));
		ServiceRegistration reg2 = bc.registerService(ConnectionFactory.class
				.getName(), cf2, props);
		ServiceTracker st = new ServiceTracker(bc, ConnectorService.class
				.getName(), null);
		st.open();
		st.waitForService(5000);
		ConnectorService connector = (ConnectorService) st.getService();
		assertNotNull("connector service", connector);
		Connection c = connector.open("test://testurl");
		assertNotNull("returned connection", c);
		assertEquals("uri check highest ranking", "test://testurl", cf2.uri);
		assertEquals("uri check lowest ranking", null, cf1.uri);
		reg1.unregister();
		reg2.unregister();
	}

	/**
	 * tests connection factories registered with multiple schemes
	 * 
	 * @throws Exception
	 */
	public void testMultipleSchemes() throws Exception {
		bc = getContext();
		Hashtable props = new Hashtable();
		props.put(ConnectionFactory.IO_SCHEME, new String[] {"test", "test2"});
		TestConnectionFactory cf = new TestConnectionFactory();
		ServiceRegistration reg = bc.registerService(ConnectionFactory.class
				.getName(), cf, props);
		ServiceTracker st = new ServiceTracker(bc, ConnectorService.class
				.getName(), null);
		st.open();
		st.waitForService(5000);
		ConnectorService connector = (ConnectorService) st.getService();
		assertNotNull("connector service", connector);
		Connection c = connector.open("test://testurl");
		assertNotNull("returned connection", c);
		assertEquals("uri check", "test://testurl", cf.uri);
		c = connector.open("test2://testurl");
		assertNotNull("returned connection", c);
		assertEquals("uri check", "test2://testurl", cf.uri);
		reg.unregister();
	}
}
