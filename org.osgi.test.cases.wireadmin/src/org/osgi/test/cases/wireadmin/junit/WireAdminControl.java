package org.osgi.test.cases.wireadmin.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.wireadmin.BasicEnvelope;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Producer;
import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireAdmin;
import org.osgi.service.wireadmin.WireAdminEvent;
import org.osgi.service.wireadmin.WireAdminListener;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.service.wireadmin.WirePermission;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * Contains the test methods of the wireadmin test case
 * 
 * $Log$ Revision 1.9 2006/05/22 10:17:14 sboshev Fixed bug - filter test did
 * fail sometimes because the Thread didn't wait for 500ms as requested.
 * 
 * Revision 1.8 2005/11/30 08:44:34 sboshev Fixed a bug which caused sometimes
 * the test case to fail
 * 
 * Revision 1.7 2005/08/03 16:32:17 pdobrev fix last issues
 * 
 * Revision 1.6 2005/07/27 15:13:31 pdobrev fixes issue#387
 * 
 * Revision 1.4 2004/12/03 09:12:32 pkriens Added service project Revision 1.3
 * 2004/11/03 11:47:09 pkriens Format and clean up of warnings Revision 1.2
 * 2004/11/03 10:55:32 pkriens Format and clean up of warnings Revision 1.1
 * 2004/07/07 13:15:26 pkriens *** empty log message ***
 * 
 * Revision 1.7 2003/12/16 15:06:17 vpanushev 1. test_wireadmin_DeleteWire is
 * changed to reproduce the case described in issue #245 from the issue tracker
 * 2. test_wireadmin_RestartWireadmin rewriten
 * 
 * Revision 1.6 2003/11/14 07:18:29 vpanushev resolved issues 243 and 246
 * 
 * 
 * @author Neviana Ducheva, Vasil Panushev
 */
public class WireAdminControl extends DefaultTestBundleControl {
	private PermissionAdmin	pa;
	private Helper			helper;
	private WireAdmin		wa;
	private Hashtable		returnedWires;
	public int				synchCounterx;
	private List			permBundles;

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////wire admin test methods start
	// here ///////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Increase the test case timeout
	 */
	protected synchronized void setUp() {
		returnedWires = new Hashtable();
		clearSync();
		permBundles = new ArrayList();
		wa = (WireAdmin) getService(WireAdmin.class);
		pa = (PermissionAdmin) getService(PermissionAdmin.class);
		helper = new Helper(getContext(), this);
		Helper.deleteAllWires(wa);
	}

	protected synchronized void tearDown() {
		helper.unregisterAll();
		Helper.deleteAllWires(wa);
		removePermissions();
		ungetService(pa);
		ungetService(wa);
	}

	/**
	 * @param requiredSyncs
	 * @param maxDelay
	 * @throws InterruptedException
	 */
	static Object	lock	= new Object();

	private synchronized void waitForSync(int requiredSyncs, int maxDelay)
			throws InterruptedException {
		// System.out.println("**** delaying " + synchCounterx + " " +
		// requiredSyncs);
		while (synchCounterx < requiredSyncs && maxDelay-- > 0) {
			// Thread.sleep(100);
			wait(100 * OSGiTestCaseProperties.getScaling());
		}
		if (maxDelay <= 0)
			fail("**** timed out");
		synchCounterx = 0;
	}

	private synchronized void waitForNoSync(int maxDelay)
			throws InterruptedException {
		// System.out.println("**** delaying " + synchCounterx + " " +
		// requiredSyncs);
		while (synchCounterx == 0 && maxDelay-- > 0) {
			// Thread.sleep(100);
			wait(100 * OSGiTestCaseProperties.getScaling());
		}
		if (synchCounterx > 0) {
			synchCounterx = 0;
			fail("**** sync received");
		}
	}

	private synchronized boolean checkForAdditionalNotifications(int maxDelay)
			throws InterruptedException {
		while (synchCounterx == 0 && maxDelay-- > 0) {
			// Thread.sleep(100);
			wait(100 * OSGiTestCaseProperties.getScaling());
		}
		if (synchCounterx > 0) {
			log("Warning:Additional producer/consumer notification detected");
			synchCounterx = 0;
			return true;
		}
		return false;
	}

	synchronized void syncup(String pid) {
		if (System.getProperty("dump.now") != null) {
			log("Syncing up " + pid);
		}
		synchCounterx++;
		notify();
	}

	private synchronized void clearSync() {
		synchCounterx = 0;
	}

	/**
	 * The most basic tests: create and connect check if the methods of the Wire
	 * interface return a correct results check if the proper methods are called
	 * (producersConnected, consumersConnected)
	 */
	public void testCreateWire() throws Exception {
		log("Must call producersConnected(..) for consumer.ConsumerImplA");
		helper.registerConsumer("consumer.ConsumerImplA", new Class[] {
				String.class, Integer.class, Float.class});
		waitForSync(1, 100);
		compareConnected("", "consumer.ConsumerImplA");

		log("Must call consumersConnected(..) for producer.ProducerImplA");
		helper.registerProducer("producer.ProducerImplA", new Class[] {
				String.class, Float.class});
		waitForSync(1, 100);
		compareConnected("producer.ProducerImplA", "");

		Hashtable properties = new Hashtable();
		properties.put("property1", new Integer(1));
		properties.put("property2", new Float(1.0));
		properties.put("property3", new Boolean(false));
		properties.put("org.osgi.test.wireadmin", "yes");

		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConnected(..) for consumer.ConsumerImplA");
		Wire wire = helper.createWire(wa, "producer.ProducerImplA",
				"consumer.ConsumerImplA", properties);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");

		assertTrue("Test wire.isValid after createWire: NOT OK. ", wire
				.isValid());
		assertTrue("Test wire.isConnected after createWire: NOT OK. ", wire
				.isConnected());
		assertTrue("Test wire flavors after createWire: NOT OK. ",
				compareWires_Flavors(wire.getFlavors(), new Class[] {
						String.class, Integer.class, Float.class}));

		wire.update("NEW VALUE");

		assertEquals("Test update wire: NOT OK. ", "NEW VALUE", wire
				.getLastValue());

		Hashtable newProperties = new Hashtable();
		newProperties.put("property1", new Integer(2));
		newProperties.put("property2", new Float(2.0));
		newProperties.put("property3", new Boolean(false));
		newProperties.put("org.osgi.test.wireadmin", "yes");
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConnected(..) for consumer.ConsumerImplA");
		helper.updateWire(wa, wire, newProperties);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");
		assertTrue("Test wire properties after updateWire: NOT OK.",
				compareWireProperties(wire.getProperties(), newProperties));
		try {
			// Wait some seconds to avoid upcomming invalid sync notifications
			Thread.sleep(1000);
		}
		catch (InterruptedException ie) {
			// empty
		}
	}

	/**
	 * Test behaviour of deleted wires
	 * 
	 * isValid (should be false after deletion) isConnected (should be false
	 * after deletion) Wire.getFlavors (should return null) WireAdmin.getWires
	 * (should return null when filter matches the deleted wire)
	 */
	public void testDeleteWire() throws Exception {
		log("creating two wires");
		Hashtable props = new Hashtable();
		props.put("org.osgi.test.wireadmin", "yes");
		Wire wire1 = helper.createWire(wa, "deletedWireProducer",
				"deletedWireConsumer", props);
		Wire wire2 = helper.createWire(wa, "deletedWireProducer",
				"deletedWireConsumer", props);
		log("registering consumer and producer so the wires are connected when we delete one of them later");
		helper.registerProducer("deletedWireProducer",
				new Class[] {java.lang.String.class});
		helper.registerConsumer("deletedWireConsumer",
				new Class[] {java.lang.String.class});
		/*
		 * The number of the notifications is 5 : 1 for "deletedWireProducer"
		 * with parameter null and 2x2 for each wire and each producer/consumer
		 */
		waitForSync(5, 200);
		checkForAdditionalNotifications(5);
		log("Deleting wire. Must call consumersConnected and producersConnected");
		wa.deleteWire(wire1);
		waitForSync(2, 200);
		Wire[] wires = (Wire[]) returnedWires.get("deletedWireProducer");
		assertNotNull(
				"{deletedWireProducer} Invokes consumersConnected(..) NOT OK, Wrong Wire[]",
				wires);
		assertEquals(
				"{deletedWireProducer} Invokes consumersConnected(..) NOT OK, Wrong Wire[]",
				1, wires.length);

		wires = (Wire[]) returnedWires.get("deletedWireConsumer");
		assertNotNull(
				"{deletedWireConsumer} Invokes producersConnected(..) NOT OK, Wrong Wire[]",
				wires);
		assertEquals(
				"{deletedWireConsumer} Invokes producersConnected(..) NOT OK, Wrong Wire[]",
				1, wires.length);
		log("Deleting the second wire.");
		wa.deleteWire(wire2);
		waitForSync(2, 200);
		compareConnected("deletedWireProducer", "deletedWireConsumer");
		assertFalse(
				"Test Wire.isValid after deleteWire falied! Wire.isValid() returns true ",
				wire1.isValid());
		assertFalse(
				"Test Wire.isValid after deleteWire falied! Wire.isValid() returns true ",
				wire2.isValid());
		assertFalse(
				"Test wire.isConnected after deleteWire failed! Wire.isConnected returns true",
				wire1.isConnected());
		assertFalse(
				"Test wire.isConnected after deleteWire failed! Wire.isConnected returns true",
				wire2.isConnected());
		assertNull("Test wire flavors after deleteWire failed.", wire1
				.getFlavors());
		assertNull("Test wire flavors after deleteWire failed.", wire2
				.getFlavors());
		wires = wa
				.getWires("(&(wireadmin.producer.pid=deletedWireProducer)(wireadmin.consumer.pid=deletedWireConsumer))");
		assertNull("Test getWires after deleteWire failed.", wires);
	}

	/**
	 * Simulate some "incorrect" situations
	 * 
	 * Create a wire with non-registered pids (wire should be valid but not
	 * connected) Create a wire with null pid (behaviour not specified - could
	 * be ignored, however an Exception is expected by the test) Create a wire
	 * with invalid properties (Exception should be thrown) Create a wire with
	 * incompatible flavors (should be created but data should not be tranfered
	 * over it)
	 */
	public void testIncorrectCreateWire() throws Exception {
		Wire wire = helper.createWire(wa, "aa.bb.cc", "cc.dd", null);
		assertTrue("Test createWire with non-existing PIDs: NOT OK. ", wire
				.isValid());
		assertFalse("Test createWire with non-existing PIDs: NOT OK. ", wire
				.isConnected());
		wa.deleteWire(wire);
		try {
			wire = helper.createWire(wa, null, "cc.dd", null);
			fail("Test createWire with null PIDs: NOT OK.");
		}
		catch (NullPointerException e) {
			log("Test createWire with null PIDs: Operation passed: OK. Exception thrown.");
		}
		catch (IllegalArgumentException e) {
			log("Test createWire with null PIDs: Operation passed: OK. Exception thrown.");
		}
		Hashtable properties1 = new Hashtable();
		properties1.put(new Integer(1), "test");
		try {
			wire = helper.createWire(wa, "abc.bac", "bac.abc", properties1);
			fail("Test createWire with incorrect properties' key: NOT OK. ");
		}
		catch (IllegalArgumentException e) {
			log("Test createWire with incorrect properties' key: Operation passed: OK. Exception thrown.");
		}
		Hashtable properties2 = new Hashtable();
		properties2.put("Test", new Integer(1));
		properties2.put("test", new Integer(2));
		try {
			wire = helper.createWire(wa, "abc.bac", "bac.abc", properties2);
			fail("Test createWire with case insensensitive keys: NOT OK.");
		}
		catch (IllegalArgumentException e) {
			log("Test createWire with case insensensitive keys: Operation passed: OK. Exception thrown.");
		}
		log("Must call producersConnected(..) for consumer.ConsumerImplB");
		helper.registerConsumer("consumer.ConsumerImplB",
				new Class[] {Date.class});
		waitForSync(1, 100);
		compareConnected("", "consumer.ConsumerImplB");
		log("Must call consumersConnected(..) for producer.ProducerImplA");
		helper.registerProducer("producer.ProducerImplA", new Class[] {
				String.class, Float.class});
		waitForSync(1, 100);
		compareConnected("producer.ProducerImplA", "");
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConected(..) for consumer.ConsumerImplB");
		wire = helper.createWire(wa, "producer.ProducerImplA",
				"consumer.ConsumerImplB", null);
		waitForSync(2, 200);
		checkForAdditionalNotifications(5); // check for possible
		// unneeded/undesired
		// producer/consumer notifications
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplB");
		assertTrue("Test createWire with incompatible flavors: NOT OK.", wire
				.isValid());
		assertTrue("Test createWire with incompatible flavors: NOT OK.", wire
				.isConnected());
		assertTrue("Test createWire with incompatible flavors: NOT OK.",
				notUpdated(wire));
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConected(..) for consumer.ConsumerImplB");
		wa.deleteWire(wire);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplB");
	}

	/**
	 * Simulate "incorrect" situations when wire is updated
	 * 
	 * Update with invalid properties (Exception should be thrown) Update a
	 * deleted wire (not defined in the specification - the test expecs an
	 * Exception to be thrown)
	 */
	public void testIncorrectUpdateWire() throws Exception {
		log("Must call producersConnected(..) for consumer.ConsumerImplA");
		helper.registerConsumer("consumer.ConsumerImplA", new Class[] {
				String.class, Integer.class, Float.class});
		waitForSync(1, 100);
		compareConnected("", "consumer.ConsumerImplA");

		log("Must call consumersConnected(..) for producer.ProducerImplA");
		helper.registerProducer("producer.ProducerImplA", new Class[] {
				String.class, Float.class});
		waitForSync(1, 100);
		compareConnected("producer.ProducerImplA", "");

		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConected(..) for consumer.ConsumerImplA");
		Wire wire = helper.createWire(wa, "producer.ProducerImplA",
				"consumer.ConsumerImplA", null);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");

		Hashtable properties = new Hashtable();
		properties.put(new Integer(1), "TEST");
		try {
			helper.updateWire(wa, wire, properties);
			fail("Test updateWire with incorrect properties' keys: NOT OK.");
		}
		catch (IllegalArgumentException e) {
			log("Test updateWire with incorrect properties' keys: Operation passed: OK.Exception thrown.");
		}
		properties = new Hashtable();
		properties.put("property1", "TEST1");
		properties.put("PROPERTY1", "TEST2");
		try {
			helper.updateWire(wa, wire, properties);
			fail("Test updateWire with case insensitive properties' keys: NOT OK. Exception was not thrown");
			// try {
			// // Wait some seconds to avoid upcoming invalid sync
			// // notifications
			// Thread.sleep(3000);
			// clearSync();
			// }
			// catch (InterruptedException ie) {
			// }
		}
		catch (IllegalArgumentException e) {
			log("Test updateWire with case insensitive properties' keys: Operation passed: OK.Exception thrown.");
		}

		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConected(..) for consumer.ConsumerImplA");
		wa.deleteWire(wire);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");
		properties = new Hashtable();
		properties.put("property", "correct");
		try {
			helper.updateWire(wa, wire, properties);
			assertFalse("Test updateWire with non- existing wire: NOT OK.",
					compareWireProperties(wire.getProperties(), properties));
			// try {
			// // Wait some seconds to avoid upcomming invalid sync
			// // notifications
			// Thread.sleep(3000);
			// clearSync(); // reset the sync counter synce it may
			// // have been updated
			// }
			// catch (InterruptedException ie) {
			// }
		}
		catch (IllegalArgumentException e) {
			log("Test updateWire with non-existing wire: Operation passed: OK.Exception thrown.");
		}
	}

	/**
	 * Test getting a wire with incoorect filter (Exception should be thrown
	 */
	public void testIncorrectGetWires() throws Exception {
		try {
			wa.getWires("asdasd=dd");
			fail("Test getWires with invalid filter: NOT OK.");
		}
		catch (InvalidSyntaxException e) {
			log("Test getWires with invalid filter: Operation passed: OK. Exception thrown.");
		}
	}

	/**
	 * Test what happens with the wire when consumer/producers are
	 * registered/unregistered Wire should remain persistent after such
	 * operation
	 */
	public void testRegisterUnregisterConsumerProducer() throws Exception {
		log("Must call producersConnected(..) for consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		waitForSync(1, 100);
		compareConnected("", "consumer.ConsumerImplC");
		log("Must call consumersConnected(..) for producer.ProducerImplC");
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		waitForSync(1, 100);
		compareConnected("producer.ProducerImplC", "");
		Hashtable properties = new Hashtable();
		properties.put("property1", new String("TEST"));
		properties.put("property2", new Float(5.0));
		properties.put("property3", new Boolean(false));
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		Wire wire = helper.createWire(wa, "producer.ProducerImplC",
				"consumer.ConsumerImplC", properties);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		Dictionary oldProperties = wire.getProperties();
		log("Must call producersConnected(..) for producer.ProducerImplC");
		helper.unregisterProducer("producer.ProducerImplC");
		waitForSync(1, 100);
		compareConnected("", "consumer.ConsumerImplC");
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		waitForSync(2, 100);
		checkForAdditionalNotifications(5); // check for possible
		// unneeded/undesired
		// producer/consumer notifications
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		wire = wa
				.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
		assertTrue(
				"Test wire properties after Producer's re- registration: NOT OK.",
				compareWireProperties(wire.getProperties(), oldProperties));

		oldProperties = wire.getProperties();
		log("Must call consumersConnected(..) for producer.ProducerImplC");
		helper.unregisterConsumer("consumer.ConsumerImplC");
		waitForSync(1, 100);
		compareConnected("producer.ProducerImplC", "");
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		waitForSync(2, 100);
		checkForAdditionalNotifications(5); // check for possible
		// unneeded/undesired
		// producer/consumer notifications
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		wire = wa
				.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
		assertTrue(
				"Test wire properties after Consumer's re- registration: NOT OK.",
				compareWireProperties(wire.getProperties(), oldProperties));
	}

	/**
	 * Test updating a wire, befor/after its consumer or producer was
	 * unregistered operation should be valid
	 */
	public void testUpdateAfterUnregister() throws Exception {
		log("Must call producersConnected(..) for consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		waitForSync(1, 100);
		compareConnected("", "consumer.ConsumerImplC");
		log("Must call consumersConnected(..) for producer.ProducerImplC");
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		waitForSync(1, 100);
		compareConnected("producer.ProducerImplC", "");
		Hashtable properties = new Hashtable();
		properties.put("property1", new String("TEST"));
		properties.put("property2", new Float(5.0));
		properties.put("property3", new Boolean(false));
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		Wire wire = helper.createWire(wa, "producer.ProducerImplC",
				"consumer.ConsumerImplC", properties);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");

		wire = wa
				.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
		log("Must call producersConnected(..) for consumer.ConsumerImplC");
		helper.unregisterProducer("producer.ProducerImplC");
		waitForSync(1, 100);
		compareConnected("", "consumer.ConsumerImplC");
		properties = new Hashtable();
		properties.put("property1", new String("NEW TEST FOR PRODUCER"));
		properties.put("property2", new Float(2.0));
		helper.updateWire(wa, wire, properties);

		waitForNoSync(30);

		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		wire = wa
				.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
		assertTrue("Test updateWire with unregistered Producer: NOT OK.",
				compareWireProperties(wire.getProperties(), properties));
		log("Must call consumersConnected(..) for producer.ProducerImplC");
		helper.unregisterConsumer("consumer.ConsumerImplC");
		waitForSync(1, 100);
		compareConnected("producer.ProducerImplC", "");
		properties = new Hashtable();
		properties.put("property1", new String("NEW TEST FOR CONSUMER"));
		properties.put("property2", new Float(1.0));
		helper.updateWire(wa, wire, properties);

		waitForNoSync(30);

		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		waitForSync(2, 100);
		checkForAdditionalNotifications(5); // check for possible
		// unneeded/undesired
		// producer/consumer notifications
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		wire = wa
				.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
		assertTrue("Test updateWire with unregistered Consumer: NOT OK.",
				compareWireProperties(wire.getProperties(), properties));
	}

	/**
	 * Test Wire persistency after restarting the WireAdmin bundle
	 */
	public void testRestartWireadmin() throws Exception {
		log("createing first wire");
		Wire wire1 = helper.createWire(wa, "producer1", "consumer1", null);
		log("createing second wire");
		Wire wire2 = helper.createWire(wa, "producer2", "consumer2", null);
		log("registering producer and consumer for first wire");
		helper.registerProducer("producer1",
				new Class[] {java.lang.String.class});
		helper.registerConsumer("consumer1",
				new Class[] {java.lang.String.class});
		log("stopping wire admin bundle");
		Bundle wireadmin = getServiceReference(wa).getBundle();
		ungetService(wa);
		wireadmin.stop();

		// The test must wait several seconds.
		// The total number of consumers and producers could vary therefore
		// it cannot count on the number of notifications
		try {
			Thread.sleep(5000 * OSGiTestCaseProperties.getScaling());
		}
		catch (InterruptedException ie) {
			// ignored
		}

		log("registering producer and consumer for the second wire");
		helper.registerProducer("producer2",
				new Class[] {java.lang.String.class});
		helper.registerConsumer("consumer2",
				new Class[] {java.lang.String.class});
		log("starting wire admin bundle again");
		log("Must call consumersConnected(..) for: producer1, producer2");
		log("and producersConnected(..) for: consumer1, consumer2");

		clearSync(); // reset the synch counter
		wireadmin.start();
		// The test must wait several seconds.
		// The total number of consumers and producers could vary therefore
		// it cannot count on the number of notifications
		try {
			Thread.sleep(5000 * OSGiTestCaseProperties.getScaling());
		}
		catch (InterruptedException ie) {
			// ignored
		}

		wa = (WireAdmin) getService(WireAdmin.class);
		compareConnected("producer1", "consumer1");
		compareConnected("producer2", "consumer2");
		Wire[] wires = wa
				.getWires("(&(wireadmin.producer.pid=producer1)(wireadmin.consumer.pid=consumer1))");
		assertNotNull(
				"Test wire state after WireAdmin restarting: Wire is null.",
				wires);
		wire1 = wires[0];
		assertNotNull(
				"Test wire state after WireAdmin restart: first wire isn't valid or connected.",
				wire1);
		assertTrue(
				"Test wire state after WireAdmin restart: first wire isn't valid or connected.",
				wire1.isValid());
		assertTrue(
				"Test wire state after WireAdmin restart: first wire isn't valid or connected.",
				wire1.isConnected());

		wires = wa
				.getWires("(&(wireadmin.producer.pid=producer2)(wireadmin.consumer.pid=consumer2))");
		assertNotNull(
				"Test wire state after WireAdmin restarting: Wire is null.",
				wires);
		wire2 = wires[0];
		assertNotNull(
				"Test wire state after WireAdmin restart: first wire isn't valid or connected.",
				wire2);
		assertTrue(
				"Test wire state after WireAdmin restart: first wire isn't valid or connected.",
				wire2.isValid());
		assertTrue(
				"Test wire state after WireAdmin restart: first wire isn't valid or connected.",
				wire2.isConnected());
		// The test must wait several seconds in order to finish the cleanup
		try {
			Thread.sleep(3000 * OSGiTestCaseProperties.getScaling());
			// changed
		}
		catch (InterruptedException ie) {
			// ignored
		}
	}

	/**
	 * Test if Wire.getFlavors when flavors aren't Class[] returns null
	 */
	public void testGetFlavors() throws Exception {
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		waitForSync(1, 100);
		compareConnected("producer.ProducerImplC", "");
		helper.registerConsumer("consumer.ConsumerImplF", new String[] {"AA",
				"BB"});
		waitForSync(1, 100);
		compareConnected("", "consumer.ConsumerImplF");
		Wire wire1 = helper.createWire(wa, "producer.ProducerImplC",
				"consumer.ConsumerImplF", null);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplF");
		assertNull(
				"Test wire getFlavors with incorrect consumer's flavors: NOT OK.",
				wire1.getFlavors());
	}

	/**
	 * Test producer/consumers taking part in several wires
	 */
	public void testMultipleRegistration() throws Exception {
		helper.registerConsumer("consumer.ConsumerImplA", new Class[] {
				String.class, Integer.class, Float.class});
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		helper.registerProducer("producer.ProducerImplA", new Class[] {
				String.class, Float.class});
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});

		// wait for producers/consumersConnected(null) calls when being
		// registered without existing wires
		waitForSync(4, 100);
		checkForAdditionalNotifications(5); // check for possible
		// unneeded/undesired
		// producer/consumer notifications

		Hashtable properties = new Hashtable();
		properties.put("property1", new Integer(1));
		properties.put("property2", new Float(1.0));
		properties.put("property3", new Boolean(false));
		Wire wire = helper.createWire(wa, "producer.ProducerImplA",
				"consumer.ConsumerImplA", properties);
		wire = helper.createWire(wa, "producer.ProducerImplC",
				"consumer.ConsumerImplC", properties);
		waitForSync(4, 100);
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConnected(..) for consumer.ConsumerImplC");
		wire = helper.createWire(wa, "producer.ProducerImplA",
				"consumer.ConsumerImplC", null);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplC");
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplA");
		wire = helper.createWire(wa, "producer.ProducerImplC",
				"consumer.ConsumerImplA", null);
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplA");
		Wire[] correct = wa.getWires("(org.osgi.test.wireadmin=yes)");
		log("Must call producersConnected(..) for: consumer.ConsumerImplA and consumer.ConsumerImplC");
		helper.unregisterProducer("producer.ProducerImplA");
		waitForSync(2, 100);
		compareConnected("", "consumer.ConsumerImplA");
		compareConnected("", "consumer.ConsumerImplC");
		assertTrue(
				"Test Wireadmin Wires after Producer's unregistration: NOT OK.",
				compareWires_Flavors(correct, wa
						.getWires("(org.osgi.test.wireadmin=yes)")));
		log("Must call consumersConnected(..) for: producer.ProducerImplA");
		log("and producersConnected(..) for: consumer.ConsumerImplA and consumer.ConsumerImplC");
		helper.registerProducer("producer.ProducerImplA", new Class[] {
				String.class, Float.class});
		waitForSync(4, 100);
		checkForAdditionalNotifications(5); // check for possible
		// unneeded/undesired
		// producer/consumer notifications
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");
		compareConnected("", "consumer.ConsumerImplC");
		assertTrue(
				"Test Wireadmin Wires after Producer's registration: NOT OK.",
				compareWires_Flavors(correct, wa
						.getWires("(org.osgi.test.wireadmin=yes)")));
		log("Must call consumersConnected(..) for: producer.ProducerImplA and producer.ProducerImplC");
		helper.unregisterConsumer("consumer.ConsumerImplC");
		waitForSync(2, 100);
		compareConnected("producer.ProducerImplA", "");
		compareConnected("producer.ProducerImplC", "");
		assertTrue(
				"Test Wireadmin Wires after Consumer's unregistration: NOT OK.",
				compareWires_Flavors(correct, wa
						.getWires("(org.osgi.test.wireadmin=yes)")));
		log("Must call consumersConnected(..) for: producer.ProducerImplA and producer.ProducerImplC");
		log("and producersConnected(..) for: consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		waitForSync(4, 100);
		checkForAdditionalNotifications(5); // check for possible
		// unneeded/undesired
		// producer/consumer notifications
		compareConnected("producer.ProducerImplA", "");
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		assertTrue(
				"Test Wireadmin Wires after Consumer's registration: NOT OK.",
				compareWires_Flavors(correct, wa
						.getWires("(org.osgi.test.wireadmin=yes)")));
	}

	/**
	 * Creates a wire, binds it and checks its poll and update methods
	 */
	public void testPollUpdate() throws Exception {
		log("create wire for the test");
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin.property", "42");
		Wire localWire = helper.createWire(wa, "wireAPITest.producer.pid",
				"wireAPITest.consumer.pid", h);
		// check if the wire is connected
		assertFalse("wire is connected", localWire.isConnected());
		// poll unconnected wire
		log("polling unconnected wire");
		Object value = localWire.poll();
		assertNull("received value (null) ", value);
		// update unconnected wire
		log("updating unconnected wire");
		localWire.update(new Integer(4242));
		// register producer
		log("register test producer");
		WireAPITestProducerImpl producer = new WireAPITestProducerImpl();
		helper.registerProducer(producer, "wireAPITest.producer.pid",
				new Class[] {Integer.class}, null);
		log("register test consumer");
		WireAPITestConsumerImpl consumer = new WireAPITestConsumerImpl();
		helper.registerConsumer(consumer, "wireAPITest.consumer.pid",
				new Class[] {Integer.class}, null);
		int count = 0;
		while (!localWire.isConnected() && (count++ < 100)) {
			Thread.sleep(50);
		}
		// check if the wire is connected
		assertTrue("wire is NOT connected", localWire.isConnected());
		// poll
		log("polling connected wire");
		value = localWire.poll();
		assertEquals("did not receive value Integer(42) ", new Integer(42),
				value);
		// update
		log("updating connected wire");
		localWire.update(new Integer(4242));
		assertEquals("did not receive value Integer(4242) ", new Integer(4242),
				consumer.getValue());
		// poll wire with producer throwing exception in its polled method
		log("polling connected wire with producer throwing exception");
		producer.setThrowsException(true);
		value = localWire.poll();
		assertNull("received value (null) ", value);
		producer.setThrowsException(false);
	}

	/**
	 * Tests wire flow control
	 */
	public void testValueFiltering() throws Exception {
		String delimiter = "------------------------------------------------------------";
		String filter = "(" + WireConstants.WIREVALUE_CURRENT + "=5)";
		// create wire
		log("create wire for the test");
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin.property", "42");
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		Wire localWire = helper.createWire(wa, "filter.test.producer.pid",
				"filter.test.conusmer.pid", h);
		// register producer
		log("register test producer");
		FilteredProducerImpl fpi = new FilteredProducerImpl();
		helper.registerProducer(fpi, "filter.test.producer.pid",
				new Class[] {Integer.class}, null);
		// register consumer
		log("register test consumer");
		FilteredConsumerImpl fci = new FilteredConsumerImpl();
		helper.registerConsumer(fci, "filter.test.conusmer.pid",
				new Class[] {Integer.class}, null);
		// wait until filtered producer's method consumersConnected is called
		// with the test wire
		int i = 0;
		while ((fpi.getWire() == null) && (i++ < 500)) {
			try {
				Thread.sleep(20);
			}
			catch (InterruptedException e) {
				// ignored
			}
		}
		if (fpi.getWire() == null) {
			fail("producer not connected (wire is null)");
		}
		// current value test - filter is set on wire creation
		log("Current value test - notifying producer. Will send integers [0..9]");
		log("Filter is " + filter);
		log("Consumer should receive values [5]");
		fpi.updateWire(1, false);
		int counter = 0;
		while ((counter++ < 100) && (fci.numberValuesReceived() < 1)) {
			Thread.sleep(50);
		}
		List values = fci.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Integer[] {new Integer(5)}), values);
		log(delimiter);
		// previous value test
		// last received value was 5 so the consumer should receive 0 and
		// nothing else
		filter = "(" + WireConstants.WIREVALUE_PREVIOUS + "=5)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		helper.updateWire(wa, localWire, h);
		log("Previous value test - notifying producer. Will send integers [0..9]");
		log("Filter is " + filter);
		log("Consumer should receive values [0]");
		fpi.updateWire(1, false);
		counter = 0;
		while ((counter++ < 100) && (fci.numberValuesReceived() < 1)) {
			Thread.sleep(50);
		}
		values = fci.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Integer[] {new Integer(0)}), values);
		log(delimiter);
		// delta absolute test
		// last received value was 0 so we should receive:
		// 4 - absolute delta 4
		// 8 - absolute delta 8
		filter = "(" + WireConstants.WIREVALUE_DELTA_ABSOLUTE + ">=3)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		helper.updateWire(wa, localWire, h);
		// delta absolute and delta relative tests need the delta increased to 2
		log("Value delta absolute test - notifying producer. Will send integers [0, 2, 4, 6, 8]");
		log("Filter is " + filter);
		log("Consumer should receive values [4, 8]");
		fpi.updateWire(2, false);
		counter = 0;
		while ((counter++ < 100) && (fci.numberValuesReceived() < 2)) {
			Thread.sleep(50);
		}
		values = fci.resetValuesReceived();
		assertEquals("incorrect values received", Arrays.asList(new Integer[] {
				new Integer(4), new Integer(8)}), values);
		log(delimiter);
		// delta relative test
		// last received value was 8 so we should receive
		/* !!! last received value changes dinamicly during that test !!! */
		// 0 - relative delta Infinity
		// 2 - relative delta 1
		// 4 - relative delta 0.5
		// 8 - relative delta 0.5
		filter = "(" + WireConstants.WIREVALUE_DELTA_RELATIVE + ">=0.4)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		helper.updateWire(wa, localWire, h);
		log("Value delta relative test - notifying producer. Will send integers [0, 2, 4, 6, 8]");
		log("Filter is " + filter);
		log("Consumer should receive values [0, 2, 4, 8]");
		fpi.updateWire(2, false);
		counter = 0;
		while ((counter++ < 100) && (fci.numberValuesReceived() < 4)) {
			Thread.sleep(50);
		}
		values = fci.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Integer[] {new Integer(0), new Integer(2),
						new Integer(4), new Integer(8)}), values);
		log(delimiter);
		// time elapsed test
		// 4 should be received since it is the first one that has waited so
		// long
		filter = "(" + WireConstants.WIREVALUE_ELAPSED + ">=500)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		helper.updateWire(wa, localWire, h);
		Thread.sleep(550); // ensure the time is elapsed
		log("Time elapsed test - notifying producer. Will send integers [0, 2, 4, 6, 8]");
		log("Filter is " + filter);
		log("Consumer should receive values [0, 4, 6, 8]");
		// make the producer wait before sending updates
		fpi.updateWire(2, true);
		counter = 0;
		while ((counter++ < 500) && (fci.numberValuesReceived() < 4)) { // timeout
			// increased
			// to
			// 25
			// seconds
			Thread.sleep(50);
		}
		values = fci.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Integer[] {new Integer(0), new Integer(4),
						new Integer(6), new Integer(8)}), values);
		log(delimiter);
		// disable filtering test
		log("update the producer properties - add WIREADMIN_PRODUCER_FILTERS property");
		Map p_h = new HashMap();
		p_h.put(WireConstants.WIREADMIN_PRODUCER_FILTERS, new Object());
		helper.modifyProducer("filter.test.producer.pid", p_h);
		filter = "(" + WireConstants.WIREVALUE_CURRENT + ">=5)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		helper.updateWire(wa, localWire, h);
		log("Filtering disabled test - notifying producer. Will send integers [0, 3, 6, 9]");
		log("Filter is " + filter);
		log("Consumer should receive values [0, 3, 6, 9]");
		fpi.updateWire(3, false);
		counter = 0;
		while ((counter++ < 100) && (fci.numberValuesReceived() < 4)) {
			Thread.sleep(50);
		}
		values = fci.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Integer[] {new Integer(0), new Integer(3),
						new Integer(6), new Integer(9)}), values);
		log(delimiter);
		log("Finished");

		// The test must wait several seconds.
		// The events, genereated from the operations above, must be delivered
		// in order not to interfere with the next test
		// try {
		// Thread.sleep(4000);
		// }
		// catch (InterruptedException ie) {
		// }
	}

	/**
	 * Tests event dispatching in the wire admin
	 */
	public void testEvents() throws Exception {
		// [begin=testEvents]
		// event test: "Create a wire: WIRE_CREATED event is expected"
		// wire listener: "received event WIRE_CREATED"
		// wire listener: "wire is OK"
		// event test: "update a wire: WIRE_UPDATED event is expected"
		// wire listener: "received event WIRE_UPDATED"
		// wire listener: "wire is OK"
		// event test: "connect a wire: WIRE_CONNECTED event is expected"
		// wire listener: "received event WIRE_CONNECTED"
		// wire listener: "wire is OK"
		// event test: "poll the wire: WIRE_TRACE event is expected"
		// wire listener: "received event WIRE_TRACE"
		// wire listener: "wire is OK"
		// event test: "disconnect wire: WIRE_DISCONNECTED event is expected"
		// wire listener: "received event WIRE_DISCONNECTED"
		// wire listener: "wire is OK"
		// event test:
		// "cause exception from producersConnected: CONSUMER_EXCEPTION is expected"
		// wire listener: "received event CONSUMER_EXCEPTION"
		// wire listener: "wire is OK"
		// wire listener: "correct Throwable passed! OK"
		// event test:
		// "cause exception from updated: CONSUMER_EXCEPTION is expected"
		// wire listener: "received event CONSUMER_EXCEPTION"
		// wire listener: "wire is OK"
		// wire listener: "correct Throwable passed! OK"
		// event test:
		// "cause exception from consumersConnected: PRODUCER_EXCEPTION is expected"
		// wire listener: "received event PRODUCER_EXCEPTION"
		// wire listener: "wire is OK"
		// wire listener: "correct Throwable passed! OK"
		// event test:
		// "cause exception from polled: PRODUCER_EXCEPTION is expected"
		// wire listener: "received event PRODUCER_EXCEPTION"
		// wire listener: "wire is OK"
		// wire listener: "correct Throwable passed! OK"
		// event test: "delete wire: WIRE_DELETED event is expected"
		// wire listener: "received event WIRE_DELETED"
		// wire listener: "wire is OK"
		// event test:
		// "cause exception form producersConnected of a 'free' Consumer. CONSUMER_EXCEPTION is expected"
		// wire listener: "received event CONSUMER_EXCEPTION"
		// wire listener:
		// "event.getWire() returned null. No specific wire was responsible for the event"
		// wire listener: "correct Throwable passed! OK"
		// event test:
		// "cause exception form consumersConnected of a 'free' Producer. PRODUCER_EXCEPTION is expected"
		// wire listener: "received event PRODUCER_EXCEPTION"
		// wire listener:
		// "event.getWire() returned null. No specific wire was responsible for the event"
		// wire listener: "correct Throwable passed! OK"
		// [end=testEvents]

		// a full mask
		Integer mask = new Integer(0xFFFFFFFF);
		// register the listeners
		TestWireAdminListener listener = new TestWireAdminListener(false); // should
		// receive
		// all
		// events
		TestWireAdminListener dummy = new TestWireAdminListener(true); // shouldn't
		// receive
		// events
		// real listener
		Hashtable walProps = new Hashtable();
		walProps.put(WireConstants.WIREADMIN_EVENTS, mask);
		ServiceRegistration listenerReg = getContext().registerService(
				WireAdminListener.class.getName(), listener, walProps);
		// dummy listener
		Hashtable dummyProps = new Hashtable();
		dummyProps.put(WireConstants.WIREADMIN_EVENTS, new Integer(0));
		ServiceRegistration dummyReg = getContext().registerService(
				WireAdminListener.class.getName(), dummy, dummyProps);
		// real test
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin.property", "42");
		log("Create a wire: WIRE_CREATED event is expected");
		Wire w = helper.createWire(wa, "producer.event.test.pid",
				"consumer.event.test.pid", h);
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		List result = listener.resetValuesReceived();
		assertEquals("incorrect values received",
				Arrays.asList(new Object[] {new Integer(
						WireAdminEvent.WIRE_CREATED)}), result);

		log("update a wire: WIRE_UPDATED event is expected");
		helper.updateWire(wa, w, h);
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received",
				Arrays.asList(new Object[] {new Integer(
						WireAdminEvent.WIRE_UPDATED)}), result);

		log("connect a wire: WIRE_CONNECTED event is expected");
		helper.registerEventConsumer(false);
		helper.registerEventProducer(false);
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received",
				Arrays.asList(new Object[] {new Integer(
						WireAdminEvent.WIRE_CONNECTED)}), result);

		log("poll the wire: WIRE_TRACE event is expected");
		w.poll();
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Object[] {new Integer(WireAdminEvent.WIRE_TRACE)}),
				result);

		log("disconnect wire: WIRE_DISCONNECTED event is expected");
		helper.unregisterEventConsumer();
		helper.unregisterEventProducer();
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Object[] {new Integer(
						WireAdminEvent.WIRE_DISCONNECTED)}), result);

		// receive only PRODUCERS_EXCEPTION, CONSUMERS_EXCEPTION and
		// WIRE_DELETED events from now
		mask = new Integer(WireAdminEvent.CONSUMER_EXCEPTION
				| WireAdminEvent.PRODUCER_EXCEPTION
				| WireAdminEvent.WIRE_DELETED);
		walProps.put(WireConstants.WIREADMIN_EVENTS, mask);
		listenerReg.setProperties(walProps);

		log("cause exception from producersConnected: CONSUMER_EXCEPTION is expected");
		helper.registerEventProducer(false);
		helper.registerEventConsumer(true);
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received", Arrays.asList(new Object[] {
				new Integer(WireAdminEvent.CONSUMER_EXCEPTION), "testing"}),
				result);

		log("cause exception from updated: CONSUMER_EXCEPTION is expected");
		w.update("42");
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received", Arrays.asList(new Object[] {
				new Integer(WireAdminEvent.CONSUMER_EXCEPTION), "testing"}),
				result);

		helper.unregisterEventConsumer();
		helper.unregisterEventProducer();

		log("cause exception from consumersConnected: PRODUCER_EXCEPTION is expected");
		helper.registerEventConsumer(false);
		helper.registerEventProducer(true);
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received", Arrays.asList(new Object[] {
				new Integer(WireAdminEvent.PRODUCER_EXCEPTION), "testing"}),
				result);

		log("cause exception from polled: PRODUCER_EXCEPTION is expected");
		w.poll();
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received", Arrays.asList(new Object[] {
				new Integer(WireAdminEvent.PRODUCER_EXCEPTION), "testing"}),
				result);

		helper.unregisterEventConsumer();
		helper.unregisterEventProducer();

		log("delete wire: WIRE_DELETED event is expected");
		wa.deleteWire(w);
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received",
				Arrays.asList(new Object[] {new Integer(
						WireAdminEvent.WIRE_DELETED)}), result);

		log("cause exception form producersConnected of a 'free' Consumer. CONSUMER_EXCEPTION is expected");
		helper.registerEventConsumer(true);
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Object[] {"testing"}), result);

		log("cause exception form consumersConnected of a 'free' Producer. PRODUCER_EXCEPTION is expected");
		helper.registerEventProducer(true);
		listener.waitForCall(5000 * OSGiTestCaseProperties.getScaling());
		result = listener.resetValuesReceived();
		assertEquals("incorrect values received", Arrays
				.asList(new Object[] {"testing"}), result);

		helper.unregisterEventConsumer();
		helper.unregisterEventProducer();
		listenerReg.unregister();
		dummyReg.unregister();
		result = dummy.resetValuesReceived();
		assertEquals("dummy listener was called", Collections.EMPTY_LIST,
				result);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////wire admin test methods end
	// here ////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////state manager test methods start
	// here //////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void testNullScopes() throws Exception {
		Bundle consumer = installBundle("tb1.jar");
		Bundle producer = installBundle("tb4.jar");
		try {
			Wire wire = helper.createWire(wa, "producer.ProducerImplA",
					"consumer.ConsumerImplA", null);
			assertNull("Test Wire Scope without WirePermission: NOT OK.", wire
					.getScope());

			wa.deleteWire(wire);

			setWirePermissions(new String[] {"*"}, consumer, true);
			wire = helper.createWire(wa, "producer.ProducerImplA",
					"consumer.ConsumerImplA", null);
			assertTrue(
					"Test Wire Scope with null scope, after setting WirePermissions: NOT OK.",
					compareScopes(wire.getScope(), null));
			BasicEnvelope envelope = new BasicEnvelope("true", "door1", "A");
			wire.update(envelope);
			assertEquals("Test Wire update with null scope: NOT OK.", envelope,
					wire.getLastValue());
		}
		finally {
			consumer.uninstall();
			producer.uninstall();
		}
	}

	public void testConsumerScope() throws Exception {
		Bundle consumer = installBundle("tb2.jar");
		Bundle producer = installBundle("tb4.jar");
		try {
			Wire wire = helper.createWire(wa, "producer.ProducerImplA",
					"consumer.ConsumerImplB", null);
			assertNull(
					"Test Wire Scope with null producer scope without WirePermission: NOT OK.",
					wire.getScope());
			wa.deleteWire(wire);
			setWirePermissions(new String[] {"*"}, consumer, true);
			setWirePermissions(new String[] {"*"}, producer, false);
			wire = helper.createWire(wa, "producer.ProducerImplA",
					"consumer.ConsumerImplB", null);
			assertTrue(
					"Test Wire Scope with null producer scope, after setting WirePermission: NOT OK.",
					compareScopes(wire.getScope(), null));
			BasicEnvelope envelope = new BasicEnvelope(new Integer(42),
					"window1", "A");
			wire.update(envelope);

			assertEquals("Test Wire update with null scope: NOT OK.", envelope,
					wire.getLastValue());
		}
		finally {
			consumer.uninstall();
			producer.uninstall();
		}
	}

	public void testAllScope() throws Exception {
		Bundle consumer = installBundle("tb2.jar");
		Bundle producer = installBundle("tb5.jar");
		try {
			Wire wire = helper.createWire(wa, "producer.ProducerImplB",
					"consumer.ConsumerImplB", null);
			assertTrue(
					"Test Wire Scope without WirePermission: NOT OK.",
					compareScopes(wire.getScope(), new String[] {"A", "B", "C"}));
			BasicEnvelope envelope = new BasicEnvelope(new Integer(42),
					"window2", "YY");
			wire.update(envelope);
			assertNull("Test Wire update without permission: NOT OK.", wire
					.getLastValue());
			wa.deleteWire(wire);
			setWirePermissions(new String[] {"B", "C"}, consumer, true);
			setWirePermissions(new String[] {"A", "B"}, producer, false);
			wire = helper.createWire(wa, "producer.ProducerImplB",
					"consumer.ConsumerImplB", null);
			assertTrue(
					"Test Wire Scope, after setting WirePermission: NOT OK.",
					compareScopes(wire.getScope(), new String[] {"B"}));
			envelope = new BasicEnvelope(new Integer(42), "window2", "B");
			wire.update(envelope);
			assertEquals("Test Wire update with envelope object: NOT OK.",
					envelope, wire.getLastValue());
		}
		finally {
			consumer.uninstall();
			producer.uninstall();
		}
	}

	public void testAsteriskConsumer() throws Exception {
		Bundle consumer = installBundle("tb3.jar");
		Bundle producer = installBundle("tb5.jar");
		try {
			Wire wire = helper.createWire(wa, "producer.ProducerImplB",
					"consumer.ConsumerImplC", null);
			assertTrue(
					"Test Wire Scope without WirePermission: NOT OK.",
					compareScopes(wire.getScope(), new String[] {"A", "B", "C"}));
			BasicEnvelope envelope = new BasicEnvelope("open", "door2", "DD");
			wire.update(envelope);
			assertNull("Test Wire update without permission: NOT OK.", wire
					.getLastValue());
			setWirePermissions(new String[] {"*"}, consumer, true);
			setWirePermissions(new String[] {"A", "B", "C"}, producer, false);
			wa.deleteWire(wire);
			wire = helper.createWire(wa, "producer.ProducerImplB",
					"consumer.ConsumerImplC", null);
			assertTrue(
					"Test Wire Scope, after setting WirePermission: NOT OK.",
					compareScopes(wire.getScope(), new String[] {"A", "B", "C"}));
			envelope = new BasicEnvelope("locked", "backdoor", "B");
			wire.update(envelope);
			assertEquals("Test Wire update with envelope object: NOT OK.",
					envelope, wire.getLastValue());
		}
		finally {
			consumer.uninstall();
			producer.uninstall();
		}
	}

	public void testAllAsterisk() throws Exception {
		Bundle consumer = installBundle("tb3.jar");
		Bundle producer = installBundle("tb6.jar");
		try {
			setWirePermissions(new String[] {"*"}, consumer, true);
			setWirePermissions(new String[] {"*"}, producer, false);
			Wire wire = helper.createWire(wa, "producer.ProducerImplC",
					"consumer.ConsumerImplC", null);
			assertTrue(
					"Test Wire Scope with *(scope) and *(WirePermission): NOT OK.",
					compareScopes(wire.getScope(), new String[] {"*"}));
			BasicEnvelope envelope = new BasicEnvelope(new Integer(5),
					"numberDoors", "AA");
			wire.update(envelope);
			assertEquals("Test Wire update with envelope object: NOT OK.",
					envelope, wire.getLastValue());
		}
		finally {
			consumer.uninstall();
			producer.uninstall();
		}
	}

	public void testMissingScopePermissions() throws Exception {
		Bundle consumer = installBundle("tb2.jar");
		Bundle producer = installBundle("tb5.jar");
		try {
			setWirePermissions(new String[] {"A", "B", "C"}, consumer, true);
			setWirePermissions(new String[] {"X"}, producer, false);
			Wire wire = helper.createWire(wa, "producer.ProducerImplB",
					"consumer.ConsumerImplB", null);
			assertTrue("Test Wire Scope: NOT OK.", compareScopes(wire
					.getScope(), new String[] {}));
			BasicEnvelope envelope = new BasicEnvelope(new Integer(44),
					"numberWindows", "A");
			wire.update(envelope);
			assertNull(
					"Test Wire update with incompatible wire scope: NOT OK.",
					wire.getLastValue());
		}
		finally {
			consumer.uninstall();
			producer.uninstall();
		}
	}

	public void testScopeIntersection() throws Exception {
		Bundle consumer = installBundle("tb2.jar");
		Bundle producer = installBundle("tb7.jar");
		try {
			setWirePermissions(new String[] {"*"}, consumer, true);
			setWirePermissions(new String[] {"C", "E"}, producer, false);
			Wire wire = helper.createWire(wa, "producer.ProducerImplD",
					"consumer.ConsumerImplB", null);
			assertTrue("Test Wire Scope: NOT OK.", compareScopes(wire
					.getScope(), new String[] {"C"}));
			BasicEnvelope envelope = new BasicEnvelope("unlocked",
					"front.Door", "A");
			wire.update(envelope);
			assertNull(
					"Test Wire update with incompatible wire scope: NOT OK.",
					wire.getLastValue());
			envelope = new BasicEnvelope("closed", "front.Door", "C");
			wire.update(envelope);
			assertEquals(
					"Test Wire update with correct envelope object: NOT OK.",
					envelope, wire.getLastValue());
		}
		finally {
			consumer.uninstall();
			producer.uninstall();
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////state manager test methods end
	// here //////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////// auxil methods
	// ///////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * flag - true -> consumer, false -> producer
	 */
	private void setWirePermissions(String[] permissions, Bundle bundle,
			boolean flag) {
		String type = flag ? WirePermission.CONSUME : WirePermission.PRODUCE;
		PermissionInfo[] perms = new PermissionInfo[permissions.length];
		for (int i = 0; i < permissions.length; i++) {
			perms[i] = new PermissionInfo(WirePermission.class.getName(),
					permissions[i], type);
		}
		pa.setPermissions(bundle.getLocation(), perms);
		synchronized (permBundles) {
			permBundles.add(bundle.getLocation());
		}
	}

	private void removePermissions() {
		synchronized (permBundles) {
			for (Iterator i = permBundles.iterator(); i.hasNext();) {
				pa.setPermissions((String) i.next(), null);
			}
			permBundles.clear();
		}
	}

	private boolean compareScopes(String[] current, String[] correct) {
		boolean flag = false;
		if (current == null && correct == null)
			return true;
		if (current == null || correct == null)
			return false;
		if (current.length != correct.length) {
			return false;
		}
		else {
			for (int counter = 0; counter < correct.length; counter++) {
				for (int counter1 = 0; counter1 < current.length; counter1++) {
					if (current[counter1].equals(correct[counter])) {
						flag = true;
						break;
					}
				}
				if (!flag)
					return false;
			}
		}
		return true;
	}

	private Wire[] getConnected(Wire[] all) {
		Vector v = new Vector();
		if (all == null)
			return null;
		for (int counter = 0; counter < all.length; counter++) {
			if (all[counter].isConnected())
				v.addElement(all[counter]);
		}
		if (v.size() == 0)
			return null;
		Wire[] toReturn = new Wire[v.size()];
		v.copyInto(toReturn);
		return toReturn;
	}

	private void compareConnected(String producerPID, String consumerPID) {
		Wire[] wires;
		Wire[] returned;
		if (!producerPID.equals("")) {
			wires = getConnected(getWiresForFilter("(wireadmin.producer.pid="
					+ producerPID + ")"));
			returned = (Wire[]) returnedWires.remove(producerPID);
			assertNotNull("{" + producerPID
					+ "} Invokes consumersConnected(..): NOT OK, Not invoked",
					returned);
			if (returned.length == 0) {
				returned = null;
			}
			assertTrue("{" + producerPID
					+ "} Invokes consumersConnected(..): NOT OK, Wrong Wire[]",
					compareWires_Flavors(wires, returned));
		}
		if (!consumerPID.equals("")) {
			wires = getConnected(getWiresForFilter("(wireadmin.consumer.pid="
					+ consumerPID + ")"));
			returned = (Wire[]) returnedWires.remove(consumerPID);
			assertNotNull("{" + consumerPID
					+ "} Invokes producersConnected(..): NOT OK, Not invoked",
					returned);
			if (returned.length == 0) {
				returned = null;
			}
			assertTrue("{" + consumerPID
					+ "} Invokes producersConnected(..): NOT OK, Wrong Wire[]",
					compareWires_Flavors(wires, returned));
		}
	}

	void addInHashtable(String pid, Wire[] wires) {
		returnedWires.put(pid, (wires == null) ? new Wire[0] : wires);
	}

	private boolean compareWires_Flavors(Object[] current, Object[] correct) {
		boolean flag = false;
		if (current == null && correct == null)
			return true;
		if (current == null || correct == null)
			return false;
		if (current.length != correct.length)
			return false;
		for (int counter = 0; counter < correct.length; counter++) {
			for (int counter1 = 0; counter1 < current.length; counter1++) {
				if (current[counter1].equals(correct[counter])) {
					flag = true;
					break;
				}
			}
			if (!flag)
				return false;
		}
		return true;
	}

	private boolean compareWireProperties(Dictionary current, Dictionary correct) {
		Enumeration keys = correct.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (!(correct.get(key).equals(current.get(key)))) {
				return false;
			}
		}
		return true;
	}

	private boolean notUpdated(Wire wire) {
		wire.update("TEST FOR WIRE UPDATE");
		return wire.getLastValue() == null;
	}

	private Wire[] getWiresForFilter(String filter) {
		try {
			return wa.getWires(filter);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// //////// some helper methods for additonal debugging ... not used by
	// default
	private void dumpWires(String filter) {
		synchronized (this.getClass()) { // to avoid messups when called from
			// different threads
			try {
				Wire[] wiress = wa.getWires(filter);
				if (wiress == null) {
					System.out.println("No wires registered for filter "
							+ filter);
					return;
				}
				for (int i = 0; i < wiress.length; i++) {
					System.out.println(wiress[i]);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void dumpProducers(String filter) {
		synchronized (this.getClass()) { // to avoid messups when called from
			// different threads
			try {
				ServiceReference[] producers = getContext()
						.getServiceReferences(Producer.class.getName(), filter);
				if (producers == null) {
					System.out.println("No producers registered for filter "
							+ filter);
					return;
				}
				for (int i = 0; i < producers.length; i++) {
					String[] props = producers[i].getPropertyKeys();
					System.out.println("Producer: "
							+ getContext().getService(producers[i]));
					getContext().ungetService(producers[i]);
					System.out.println("Producer props:");
					for (int j = 0; j < props.length; j++) {
						System.out.println("property: " + props[j]);
						System.out.println("value: "
								+ producers[i].getProperty(props[j]));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void dumpConsumers(String filter) {
		synchronized (this.getClass()) { // to avoid messups when called from
			// different threads
			try {
				ServiceReference[] consumers = getContext()
						.getServiceReferences(Consumer.class.getName(), filter);
				if (consumers == null) {
					System.out.println("No consumer registered for filter "
							+ filter);
					return;
				}
				for (int i = 0; i < consumers.length; i++) {
					String[] props = consumers[i].getPropertyKeys();
					System.out.println("Consumer: "
							+ getContext().getService(consumers[i]));
					getContext().ungetService(consumers[i]);
					System.out.println("Consumer props:");
					for (int j = 0; j < props.length; j++) {
						System.out.println("property: " + props[j]);
						System.out.println("value: "
								+ consumers[i].getProperty(props[j]));
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}