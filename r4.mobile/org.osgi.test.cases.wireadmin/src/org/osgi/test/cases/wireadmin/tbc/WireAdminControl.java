package org.osgi.test.cases.wireadmin.tbc;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.permissionadmin.*;
import org.osgi.service.wireadmin.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * Contains the test methods of the wireadmin test case
 * 
 * $Log$
 * Revision 1.9  2006/05/22 10:17:14  sboshev
 * Fixed bug - filter test did fail sometimes because the Thread didn't wait for 500ms as requested.
 *
 * Revision 1.8  2005/11/30 08:44:34  sboshev
 * Fixed a bug which caused sometimes the test case to fail
 *
 * Revision 1.7  2005/08/03 16:32:17  pdobrev
 * fix last issues
 *
 * Revision 1.6  2005/07/27 15:13:31  pdobrev
 * fixes issue#387
 *
 * Revision 1.4  2004/12/03 09:12:32  pkriens
 * Added service project
 * Revision 1.3 2004/11/03 11:47:09 pkriens
 * Format and clean up of warnings Revision 1.2 2004/11/03 10:55:32 pkriens
 * Format and clean up of warnings Revision 1.1 2004/07/07 13:15:26 pkriens ***
 * empty log message ***
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
	private Bundle				consumer		= null;
	private Bundle				producer		= null;
	private PermissionAdmin		pa				= null;
	private BasicEnvelope		envelope		= null;
	public Helper				helper			= null;
	public Wire					wire			= null;
	public WireAdmin			wa				= null;
	private ServiceReference	waRef			= null;
	private ServiceReference	pRef			= null;
	public BundleContext		context;
	public static Object		synch			= new Object();
	private Hashtable			returnedWires	= new Hashtable();
	public int					synchCounterx	= 0;
	private Hashtable			p				= new Hashtable();
	static String[]				methods			= new String[] {"prepare",
			"test_wireadmin_CreateWire", "test_wireadmin_DeleteWire",
			"test_wireadmin_IncorrectCreateWire",
			"test_wireadmin_IncorrectUpdateWire",
			"test_wireadmin_IncorrectGetWires",
			"test_wireadmin_RegisterUnregisterConsumerProducer",
			"test_wireadmin_UpdateAfterUnregister",
			"test_wireadmin_RestartWireadmin",
			"test_wireadmin_multipleRegistration", "test_wireadmin_GetFlavors",
			"testPollUpdate", "testValueFiltering", "testEvents",
			"test_statemanagement_null_scopes",
			"test_statemanagement_consumer_scope",
			"test_statemanagement_all_scope",
			"test_statemanagement_asterisk_consumer",
			"test_statemanagement_all_asterisk",
			"test_statemanagement_missing_scope_permissions",
			"test_statemanagement_scope_intersection"};

	public String[] getMethods() {
		return methods;
	}

	public void stop(BundleContext bc) {
		super.stop(bc);
		helper.unregisterAll();
		helper.deleteAllWires(wa);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////wire admin test methods start
	// here ///////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Increase the test case timeout
	 */
	public void prepare() {
		wa = getWireadmin();
		pa = getPermissionAdmin();
		helper = new Helper(context, this);
		helper.deleteAllWires(wa);
	}

	/**
	 * The most basic tests: create and connect check if the methods of the Wire
	 * interface return a correct results check if the proper methods are called
	 * (producersConnected, consumersConnected)
	 */
	public void test_wireadmin_CreateWire() throws Exception {
		//  "(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))"
		log("Must Call producersConnected(..) for consumer.ConsumerImplA");
		helper.registerConsumer("consumer.ConsumerImplA", new Class[] {String.class,
				Integer.class, Float.class});
		delay(1, 100);		
		compareConnected("", "consumer.ConsumerImplA");
		
		log("Must Call consumersConnected(..) for producer.ProducerImplA");
		helper.registerProducer("producer.ProducerImplA", new Class[] {
				String.class, Float.class});
		delay(1, 100);		
		compareConnected("producer.ProducerImplA", "");
		
		Hashtable properties = new Hashtable();
		properties.put("property1", new Integer(1));
		properties.put("property2", new Float(1.0));
		properties.put("property3", new Boolean(false));
		properties.put("org.osgi.test.wireadmin", "yes");
		
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConnected(..) for consumer.ConsumerImplA");
		wire = helper.createWire(wa,"producer.ProducerImplA", "consumer.ConsumerImplA", properties);
		delay(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");

		if (wire.isValid()) {
			log("Test wire.isValid after createWire ", "Operation passed: OK.");
		} else {
			log("Test wire.isValid after createWire ", "NOT OK. ");
		}
		if (wire.isConnected()) {
			log("Test wire.isConnected after createWire ", "Operation passed: OK.");
		} else {
			log("Test wire.isConnected after createWire ", "NOT OK. ");
		}
		if (compareWires_Flavors(wire.getFlavors(), new Class[] { String.class, Integer.class, Float.class })) {
			log("Test wire flavors after createWire", "Operation passed: OK.");
		} else {
			log("Test wire flavors after createWire", "NOT OK. ");
		}

		wire.update("NEW VALUE");
		
		if ("NEW VALUE".equals(wire.getLastValue())) {
			log("Test update wire ", "Operation passed: OK.");
		}
		else {
			log("Test update wire ", "NOT OK. ");
		}
		Hashtable newProperties = new Hashtable();
		newProperties.put("property1", new Integer(2));
		newProperties.put("property2", new Float(2.0));
		newProperties.put("property3", new Boolean(false));
		newProperties.put("org.osgi.test.wireadmin", "yes");
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConnected(..) for consumer.ConsumerImplA");
		wa.updateWire(wire, newProperties);
		delay(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");
		if (compareWireProperties(wire.getProperties(), newProperties)) {
			log("Test wire properties after updateWire ",
					"Operation passes: OK.");
		}
		else {
			log("Test wire properties after updateWire ", "NOT OK.");
		}
    try {
      // Wait some seconds to avoid upcomming invalid sync notifications
      Thread.sleep(1000); 
    } catch (InterruptedException ie) {}
    synchCounterx = 0; // reset the sync counter synce it may have been updated
	}

	/**
	 * @param requiredSyncs
	 * @param maxDelay
	 * @throws InterruptedException
	 */
	static Object	lock	= new Object();

	private void delay(int requiredSyncs, int maxDelay)	throws InterruptedException {
		//System.out.println("**** delaying " + synchCounterx + " " + requiredSyncs);
		while (synchCounterx < requiredSyncs && maxDelay-- > 0)
			Thread.sleep(100);
		if (maxDelay <= 0)
			log("**** timed out");
		synchCounterx = 0;
	}
	
	private boolean checkForAdditionalNotifications(int maxDelay) throws InterruptedException {
		while (synchCounterx == 0 && maxDelay-- > 0)
			Thread.sleep(100);
		if (synchCounterx > 0) {
		  log("Warning:Additional producer/consumer notification detected");
		  synchCounterx = 0;
		  return true;
		}
		return false;
	}

	public void syncup(String pid) {
		//System.out.println("Syncing up " + pid);
		synchCounterx++;
	}

	/**
	 * Test behaviour of deleted wires
	 * 
	 * isValid (should be false after deletion) isConnected (should be false
	 * after deletion) Wire.getFlavors (should return null) WireAdmin.getWires
	 * (should return null when filter matches the deleted wire)
	 */
	public void test_wireadmin_DeleteWire() throws Exception {
		String subtest = "test delete wire";
		log(subtest, "createing two wires");
		Hashtable props = new Hashtable();
		props.put("org.osgi.test.wireadmin", "yes");
		Wire wire1 = wa.createWire("deletedWireProducer",
				"deletedWireConsumer", props);
		Wire wire2 = wa.createWire("deletedWireProducer",
				"deletedWireConsumer", props);
		log(
				subtest,
				"registering consumer and producer so the wires are connected when we delete one of them later");
		helper.registerProducer("deletedWireProducer",
				new Class[] {java.lang.String.class});
		helper.registerConsumer("deletedWireConsumer",
				new Class[] {java.lang.String.class});
    /* The number of the notifications is 5 : 
    1 for "deletedWireProducer" with parameter null and 2x2 for each wire and each producer/consumer */ 
		delay(5, 200);
    checkForAdditionalNotifications(5);
		log(subtest,
				"Deleting wire. Must call consumersConnected and producersConnected");
		wa.deleteWire(wire1);
		delay(2, 200);
		Wire[] wires = (Wire[]) returnedWires.get("deletedWireProducer");
		if ((wires != null) && (wires.length == 1)) {
			log(subtest,
					"{deletedWireProducer} Invokes consumersConnected(..) CORRECT");
		}
		else {
			log(subtest,
					"{deletedWireProducer} Invokes consumersConnected(..) NOT OK, Wrong Wire[]");
		}
		wires = (Wire[]) returnedWires.get("deletedWireConsumer");
		if ((wires != null) && (wires.length == 1)) {
			log(subtest,
					"{deletedWireConsumer} Invokes producersConnected(..) CORRECT");
		}
		else {
			log(subtest,
					"{deletedWireConsumer} Invokes producersConnected(..) NOT OK, Wrong Wire[]");
		}
		log(subtest, "Deleting the second wire.");
		wa.deleteWire(wire2);
		delay(2, 200);
		compareConnected("deletedWireProducer", "deletedWireConsumer");
		if (wire1.isValid() && wire2.isValid()) {
			log(subtest,
					"Test Wire.isValid after deleteWire falied! Wire.isValid() returns true ");
		}
		else {
			log(subtest, "Test Wire.isValid after deleteWire OK.");
		}
		if (wire1.isConnected() && wire2.isConnected()) {
			log(subtest,
					"Test wire.isConnected after deleteWire failed! Wire.isConnected returns true");
		}
		else {
			log(subtest, "Test wire.isConnected after deleteWire OK.");
		}
		if ((wire1.getFlavors() == null) && (wire2.getFlavors() == null)) {
			log(subtest, "Test wire flavors after deleteWire OK.");
		}
		else {
			log(subtest, "Test wire flavors after deleteWire failed.");
		}
		wires = wa
				.getWires("(&(wireadmin.producer.pid=deletedWireProducer)(wireadmin.consumer.pid=deletedWireConsumer))");
		if (wires == null) {
			log(subtest, "Test getWires after deleteWire OK.");
		}
		else {
			log(subtest, "Test getWires after deleteWire failed.");
		}
		helper.unregisterConsumer("deletedWireConsumer");
		helper.unregisterProducer("deletedWireProducer");
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
	public void test_wireadmin_IncorrectCreateWire() throws Exception {
		wire = wa.createWire("aa.bb.cc", "cc.dd", null);
		if (wire.isValid() && !wire.isConnected()) {
			log("Test createWire with non-existing PIDs ",
					"Operation passed: OK.");
		}
		else {
			log("Test createWire with non-existing PIDs ", "NOT OK. ");
		}
		wa.deleteWire(wire);
		try {
			wire = wa.createWire(null, "cc.dd", null);
			log("Test createWire with null PIDs", "NOT OK.");
			wa.deleteWire(wire);
		}
		catch (Exception e) {
			log("Test createWire with null PIDs",
					"Operation passed: OK.Exception thrown.");
		}
		Hashtable properties1 = new Hashtable();
		properties1.put(new Integer(1), "test");
		properties1.put("org.osgi.test.wireadmin", "yes");
		try {
			wire = wa.createWire("abc.bac", "bac.abc", properties1);
			log("Test createWire with incorrect properties' key", "NOT OK. ");
			wa.deleteWire(wire);
		}
		catch (IllegalArgumentException e) {
			log("Test createWire with incorrect properties' key",
					"Operation passed: OK.Exception thrown.");
		}
		Hashtable properties2 = new Hashtable();
		properties2.put("Test", new Integer(1));
		properties2.put("test", new Integer(2));
		properties2.put("org.osgi.test.wireadmin", "yes");
		try {
			wire = wa.createWire("abc.bac", "bac.abc", properties2);
			log("Test createWire with case insensensitive keys", "NOT OK.");
			wa.deleteWire(wire);
		}
		catch (IllegalArgumentException e) {
			log("Test createWire with case insensensitive keys",
					"Operation passed: OK.Exception thrown.");
		}
		log("Must call producersConnected(..) for consumer.ConsumerImplB");
		helper.registerConsumer("consumer.ConsumerImplB",
				new Class[] {Date.class});
		delay(1, 100);
		compareConnected("", "consumer.ConsumerImplB");
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConected(..) for consumer.ConsumerImplB");
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplA", "consumer.ConsumerImplB", h);
		delay(2, 200);
		checkForAdditionalNotifications(5); // check for possible unneeded/undesired producer/consumer notifications
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplB");
		if (wire.isValid() && wire.isConnected() && notUpdated(wire)) {
			log("Test createWire with incompatible flavors",
					"Operation passed: OK.");
		}
		else {
			log("Test createWire with incompatible flavors", "NOT OK.");
		}
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConected(..) for consumer.ConsumerImplB");
		wa.deleteWire(wire);
		delay(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplB");
	}

	/**
	 * Simulate "incorrect" situations when wire is updated
	 * 
	 * Update with invalid properties (Exception should be thrown) Update a
	 * deleted wire (not defined in the specification - the test expecs an
	 * Exception to be thrown)
	 */
	public void test_wireadmin_IncorrectUpdateWire() throws Exception {
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConected(..) for consumer.ConsumerImplA");
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplA", "consumer.ConsumerImplA", h);
		delay(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");
		Hashtable properties = new Hashtable();
		properties.put(new Integer(1), "TEST");
		properties.put("org.osgi.test.wireadmin", "yes");
		try {
			wa.updateWire(wire, properties);
			log("Test updateWire with incorrect properties' keys", "NOT OK.");
		}
		catch (Exception e) {
			log("Test updateWire with incorrect properties' keys",
					"Operation passed: OK.Exception thrown.");
		}
		properties = new Hashtable();
		properties.put("property1", "TEST1");
		properties.put("PROPERTY1", "TEST2");
		properties.put("org.osgi.test.wireadmin", "yes");
		try {
			wa.updateWire(wire, properties);
			log("Test updateWire with case insensitive properties' keys","NOT OK. Exception was not thrown");
			try {
		      // Wait some seconds to avoid upcomming invalid sync notifications
			  Thread.sleep(3000); 
			  synchCounterx = 0; // reset the sync counter synce it may have been updated
			} catch (InterruptedException ie) {}
		} catch (Exception e) {
			log("Test updateWire with case insensitive properties' keys",
					"Operation passed: OK.Exception thrown.");
			e.printStackTrace();
		}
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConected(..) for consumer.ConsumerImplA");
		wa.deleteWire(wire);
		delay(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");
		properties = new Hashtable();
		properties.put("property", "correct");
		properties.put("org.osgi.test.wireadmin", "yes");
		try {
			wa.updateWire(wire, properties);
			if (compareWireProperties(wire.getProperties(), properties)) {
				log("Test updateWire with non- existing wire", "NOT OK.");
				try {
			      // Wait some seconds to avoid upcomming invalid sync notifications
				  Thread.sleep(3000); 
				  synchCounterx = 0; // reset the sync counter synce it may have been updated
				} catch (InterruptedException ie) {}
			} else {
				log("Test updateWire with non- existing wire", "Operation passed: OK.");
			}
		}
		catch (Exception e) {
			log("Test updateWire with non-existing wire",
					"Operation passed: OK.Exception thrown.");
			e.printStackTrace();
		}
	}

	/**
	 * Test getting a wire with incoorect filter (Exception should be thrown
	 */
	public void test_wireadmin_IncorrectGetWires() throws Exception {
		try {
			wa.getWires("asdasd=dd");
			log("Test getWires with invalid filter", "NOT OK.");
		}
		catch (InvalidSyntaxException e) {
			log("Test getWires with invalid filter",
					"Operation passed: OK.Exception thrown.");
		}
	}

	/**
	 * Test what happens with the wire when consumer/producers are
	 * registered/unregistered Wire should remain persistent after such
	 * operation
	 */
	public void test_wireadmin_RegisterUnregisterConsumerProducer()	throws Exception {
		log("Must call producersConnected(..) for consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		delay(1, 100);
		compareConnected("", "consumer.ConsumerImplC");
		log("Must call consumersConnected(..) for producer.ProducerImplC");
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		delay(1, 100);
		compareConnected("producer.ProducerImplC", "");
		Hashtable properties = new Hashtable();
		properties.put("property1", new String("TEST"));
		properties.put("property2", new Float(5.0));
		properties.put("property3", new Boolean(false));
		properties.put("org.osgi.test.wireadmin", "yes");
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		wire = wa.createWire("producer.ProducerImplC",
				"consumer.ConsumerImplC", properties);
		delay(2, 100);
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		Dictionary oldProperties = wire.getProperties();
		log("Must call producersConnected(..) for producer.ProducerImplC");
		helper.unregisterProducer("producer.ProducerImplC");
		delay(1, 100);
		compareConnected("", "consumer.ConsumerImplC");
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		delay(2, 100);
		checkForAdditionalNotifications(5); // check for possible unneeded/undesired producer/consumer notifications
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		try {
			wire = wa
					.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
			if (compareWireProperties(wire.getProperties(), oldProperties)) {
				log("Test wire properties after Producer's re- registration",
						"Operation passed: OK.");
			}
			else {
				log("Test wire properties after Producer's re- registration",
						"NOT OK.");
			}
		}
		catch (Exception e) {
			log("Test wire properties after Producer's re- registration",
					"NOT OK.");
			e.printStackTrace();
		}
		oldProperties = wire.getProperties();
		log("Must call consumersConnected(..) for producer.ProducerImplC");
		helper.unregisterConsumer("consumer.ConsumerImplC");
		delay(1, 100);
		compareConnected("producer.ProducerImplC", "");
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		delay(2, 100);
		checkForAdditionalNotifications(5); // check for possible unneeded/undesired producer/consumer notifications
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		try {
			wire = wa
					.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
			if (compareWireProperties(wire.getProperties(), oldProperties)) {
				log("Test wire properties after Consumer's re- registration",
						"Operation passed: OK.");
			}
			else {
				log("Test wire properties after Consumer's re- registration",
						"NOT OK.");
			}
		}
		catch (Exception e) {
			log("Test wire properties after Consumer's re- registration",
					"NOT OK.");
			e.printStackTrace();
		}
	}

	/**
	 * Test updating a wire, befor/after its consumer or producer was
	 * unregistered operation should be valid
	 */
	public void test_wireadmin_UpdateAfterUnregister() throws Exception {
		try {
			wire = wa
					.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
		}
		catch (Exception e) {
			log("Test updateWire with unregistered Producer", "NOT DONE.");
			e.printStackTrace();
		}
		log("Must call producersConnected(..) for consumer.ConsumerImplC");
		helper.unregisterProducer("producer.ProducerImplC");
		delay(1, 100);
		compareConnected("", "consumer.ConsumerImplC");
		Hashtable properties = new Hashtable();
		properties.put("property1", new String("NEW TEST FOR PRODUCER"));
		properties.put("property2", new Float(2.0));
		properties.put("org.osgi.test.wireadmin", "yes");
		wa.updateWire(wire, properties);
		
	    int i = 0;
	    while (i < 30 && synchCounterx == 0) {
	      Thread.sleep(100);
	      i++;
	    }
	    if (synchCounterx > 0) {
		  log("Test updateWire with unregistered Producer", "The Consumer was notified - NOT OK.");
		  synchCounterx = 0;
	    }
	    
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		delay(2, 100);
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		try {
			wire = wa.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
			if (compareWireProperties(wire.getProperties(), properties)) {
				log("Test updateWire with unregistered Producer", "Operation passed: OK.");
			} else {
				log("Test updateWire with unregistered Producer", "NOT OK.");
			}
		}
		catch (Exception e) {
			log("Test updateWire with unregistered Producer", "NOT OK.");
			e.printStackTrace();
		}
		log("Must call consumersConnected(..) for producer.ProducerImplC");
		helper.unregisterConsumer("consumer.ConsumerImplC");
		delay(1, 100);
		compareConnected("producer.ProducerImplC", "");
		properties = new Hashtable();
		properties.put("property1", new String("NEW TEST FOR CONSUMER"));
		properties.put("property2", new Float(1.0));
		properties.put("org.osgi.test.wireadmin", "yes");
		wa.updateWire(wire, properties);
		
	    i = synchCounterx = 0;
	    while (i < 30 && synchCounterx == 0) {
	      Thread.sleep(100);
	      i++;
	    }
	    if (synchCounterx > 0) {
		  log("Test updateWire with unregistered Consumer", "The Producer was notified - NOT OK.");
		  synchCounterx = 0;
	    }
		
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		delay(2, 100);
		checkForAdditionalNotifications(5); // check for possible unneeded/undesired producer/consumer notifications
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		try {
			wire = wa
					.getWires("(&(wireadmin.producer.pid=producer.ProducerImplC)(wireadmin.consumer.pid=consumer.ConsumerImplC))")[0];
			if (compareWireProperties(wire.getProperties(), properties)) {
				log("Test updateWire with unregistered Consumer",
						"Operation passed: OK.");
			}
			else {
				log("Test updateWire with unregistered Consumer", "NOT OK.");
			}
		}
		catch (Exception e) {
			log("Test updateWire with unregistered Consumer", "NOT OK.");
			e.printStackTrace();
		}
	}

	/**
	 * Test Wire persistency after restarting the WireAdmin bundle
	 */
	public void test_wireadmin_RestartWireadmin() throws Exception {
		helper.unregisterAll();
		String subtest = "test stop wireadmin";
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		log(subtest, "createing first wire");
		Wire wire1 = wa.createWire("producer1", "consumer1", h);
		log(subtest, "createing second wire");
		Wire wire2 = wa.createWire("producer2", "consumer2", h);
		log(subtest, "registering producer and consumer for first wire");
		helper.registerProducer("producer1",
				new Class[] {java.lang.String.class});
		helper.registerConsumer("consumer1",
				new Class[] {java.lang.String.class});
		log(subtest, "stopping wire admin bundle");
		Bundle wireadmin = waRef.getBundle();
		try {
			wireadmin.stop();
		}
		catch (BundleException e) {
			e.printStackTrace();
		}
		
		// The test must wait several seconds. 
		//The total number of consumers and producers could vary therefore 
		// it cannot count on the number of notifications  
		try {
		  Thread.sleep(5000);
		} catch (InterruptedException ie) {}
		
		log(subtest, "registering producer and consumer for the second wire");
		helper.registerProducer("producer2",
				new Class[] {java.lang.String.class});
		helper.registerConsumer("consumer2",
				new Class[] {java.lang.String.class});
		log(subtest, "starting wire admin bundle again");
		log(subtest,
				"Must call consumersConnected(..) for: producer1, producer2");
		log(subtest, "and producersConnected(..) for: consumer1, consumer2");
		
		synchCounterx = 0; // reset the synch counter
		wireadmin.start();
		// The test must wait several seconds. 
		// The total number of consumers and producers could vary therefore 
		// it cannot count on the number of notifications  
		try {
		  Thread.sleep(5000);
		} catch (InterruptedException ie) {}
		
		waRef = context.getServiceReference(org.osgi.service.wireadmin.WireAdmin.class.getName());
		wa = (WireAdmin) context.getService(waRef);
		compareConnected("producer1", "consumer1");
		compareConnected("producer2", "consumer2");
		try {
			Wire[] wires = wa.getWires("(&(wireadmin.producer.pid=producer1)(wireadmin.consumer.pid=consumer1))");
			if (wires != null) {
				wire1 = wires[0];
				if (wire1 != null && wire1.isValid() && wire1.isConnected()) {
					log(subtest,
							"Test wire state after WireAdmin restart: first wire is valid and connected OK.");
				} else {
					log(subtest,
							"Test wire state after WireAdmin restart: first wire isn't valid or connected.");
				}
			}
			else {
				log(subtest,
						"Test wire state after WireAdmin restarting: Wire is null.");
			}
			wires = wa.getWires("(&(wireadmin.producer.pid=producer2)(wireadmin.consumer.pid=consumer2))");
			if (wires != null) {
				wire2 = wires[0];
				if (wire2 != null && wire2.isValid() && wire2.isConnected()) {
					log(subtest,
							"Test wire state after WireAdmin restart: second wire is valid and connected OK.");
				} else {
					log(subtest,
							"Test wire state after WireAdmin restart: second wire isn't valid or connected.");
				}
			}
			else {
				log(subtest,
						"Test wire state after WireAdmin restarting: Wire is null.");
			}
		}
		catch (Exception e) {
			log(subtest,"Test wire state after WireAdmin restarting failed. Exception thrown");
		}
		helper.unregisterAll();
		helper.deleteAllWires(wa);
		// The test must wait several seconds in order to finish the cleanup 
		try {
		  Thread.sleep(3000);
		  synchCounterx = 0; //reset the synch counter since it may have changed
		} catch (InterruptedException ie) {
		}
	}

	/**
	 * Test if Wire.getFlavors when flavors aren't Class[] returns null
	 */
	public void test_wireadmin_GetFlavors() throws Exception {
		helper.registerConsumer("consumer.ConsumerImplF", new String[] {"AA",
				"BB"});
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		Wire wire1 = wa.createWire("producer.ProducerImplC",
				"consumer.ConsumerImplF", h);
		if (wire1.getFlavors() == null) {
			log("Test wire getFlavors with incorrect consumer's flavors",
					"Operation passed: OK.");
		}
		else {
			log("Test wire getFlavors with incorrect consumer's flavors",
					"NOT OK.");
		}
	}

	/**
	 * Test procucesr/consumers taking part in several wires
	 */
	public void test_wireadmin_multipleRegistration() throws Exception {
		Wire[] correct = null;
		
		helper.registerConsumer("consumer.ConsumerImplA", new Class[] {String.class,
				Integer.class, Float.class});
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		helper.registerProducer("producer.ProducerImplA", new Class[] {String.class,
						Float.class});
		helper.registerProducer("producer.ProducerImplC", new Class[] {
				String.class, Float.class, Boolean.class});
		
		//wait for producers/consumersConnected(null) calls when beeing registered without existing wires  
		delay(4, 100);
		checkForAdditionalNotifications(5); // check for possible unneeded/undesired producer/consumer notifications
		
		Hashtable properties = new Hashtable();
		properties.put("property1", new Integer(1));
		properties.put("property2", new Float(1.0));
		properties.put("property3", new Boolean(false));
		properties.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplA", "consumer.ConsumerImplA", properties);
		wire = wa.createWire("producer.ProducerImplC", "consumer.ConsumerImplC", properties);
		delay(4, 100);
		log("Must call both consumersConnected(..) for producer.ProducerImplA and producersConnected(..) for consumer.ConsumerImplC");
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplA", "consumer.ConsumerImplC", h);
		delay(2, 100);
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplC");
		log("Must call both consumersConnected(..) for producer.ProducerImplC and producersConnected(..) for consumer.ConsumerImplA");
		wire = wa.createWire("producer.ProducerImplC", "consumer.ConsumerImplA", h);
		delay(2, 100);
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplA");
		correct = wa.getWires("(org.osgi.test.wireadmin=yes)");
		log("Must call producersConnected(..) for: consumer.ConsumerImplA and consumer.ConsumerImplC");
		helper.unregisterProducer("producer.ProducerImplA");
		delay(2, 100);
		compareConnected("", "consumer.ConsumerImplA");
		compareConnected("", "consumer.ConsumerImplC");
		if (compareWires_Flavors(correct, wa
				.getWires("(org.osgi.test.wireadmin=yes)"))) {
			log("Test Wireadmin Wires after Producer's unregistration",
					"Operation passed: OK.");
		}
		else {
			log("Test Wireadmin Wires after Producer's unregistration",
					"NOT OK.");
		}
		log("Must call consumersConnected(..) for: producer.ProducerImplA");
		log("and producersConnected(..) for: consumer.ConsumerImplA and consumer.ConsumerImplC");
		helper.registerProducer("producer.ProducerImplA", new Class[] {String.class, Float.class});
		delay(4, 100);
		checkForAdditionalNotifications(5); // check for possible unneeded/undesired producer/consumer notifications
		compareConnected("producer.ProducerImplA", "consumer.ConsumerImplA");
		compareConnected("", "consumer.ConsumerImplC");
		if (compareWires_Flavors(correct, wa.getWires("(org.osgi.test.wireadmin=yes)"))) {
			log("Test Wireadmin Wires after Producer's registration",
					"Operation passed: OK.");
		}
		else {
			log("Test Wireadmin Wires after Producer's registration", "NOT OK.");
		}
		log("Must call consumersConnected(..) for: producer.ProducerImplA and producer.ProducerImplC");
		helper.unregisterConsumer("consumer.ConsumerImplC");
		delay(2, 100);
		compareConnected("producer.ProducerImplA", "");
		compareConnected("producer.ProducerImplC", "");
		if (compareWires_Flavors(correct, wa.getWires("(org.osgi.test.wireadmin=yes)"))) {
			log("Test Wireadmin Wires after Consumer's unregistration",
					"Operation passed: OK.");
		}
		else {
			log("Test Wireadmin Wires after Consumer's unregistration",	"NOT OK.");
		}
		log("Must call consumersConnected(..) for: producer.ProducerImplA and producer.ProducerImplC");
		log("and producersConnected(..) for: consumer.ConsumerImplC");
		helper.registerConsumer("consumer.ConsumerImplC", new Class[] {
				String.class, Integer.class, Float.class});
		delay(4, 100);
		checkForAdditionalNotifications(5); // check for possible unneeded/undesired producer/consumer notifications
		compareConnected("producer.ProducerImplA", "");
		compareConnected("producer.ProducerImplC", "consumer.ConsumerImplC");
		if (compareWires_Flavors(correct, wa.getWires("(org.osgi.test.wireadmin=yes)"))) {
			log("Test Wireadmin Wires after Consumer's registration", "Operation passed: OK.");
		}
		else {
			log("Test Wireadmin Wires after Consumer's registration", "NOT OK.");
		}
	}

	/**
	 * Creates a wire, binds it and checks its poll and update methods
	 */
	public void testPollUpdate() throws Exception {
		// create wire
		log("wire api test", "create wire for the test");
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin.property", "42");
		h.put("org.osgi.test.wireadmin", "yes");
		Wire localWire = wa.createWire("wireAPITest.producer.pid",
				"wireAPITest.consumer.pid", h);
		// check if the wire is connected
		if (localWire.isConnected()) {
			log("wire api test", "wire is connected");
		}
		else {
			log("wire api test", "wire is NOT connected");
		}
		// poll unconnected wire
		log("wire api test", "polling unconnected wire");
		Object value = (Object) localWire.poll();
		log("wire api test", "received value (null) " + value);
		// update unconnected wire
		log("wire api test", "updateing unconnected wire");
		localWire.update(new Integer(4242));
		// register producer
		log("wire api test", "register test producer");
		Hashtable p_h = new Hashtable();
		p_h.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS,
				new Class[] {Integer.class});
		p_h.put(Constants.SERVICE_PID, "wireAPITest.producer.pid");
		WireAPITestProducerImpl producer = new WireAPITestProducerImpl(this);
		ServiceRegistration reg_producer = context.registerService(
				Producer.class.getName(), producer, p_h);
		// register consumer
		log("wire api test", "register test consumer");
		Hashtable c_h = new Hashtable();
		c_h.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS,
				new Class[] {Integer.class});
		c_h.put(Constants.SERVICE_PID, "wireAPITest.consumer.pid");
		WireAPITestConsumerImpl consumer = new WireAPITestConsumerImpl(this);
		ServiceRegistration reg_consumer = context.registerService(
				Consumer.class.getName(), consumer, c_h);
		int count = 0;
		while (!localWire.isConnected() && (count++ < 100)) {
			Thread.sleep(50);
		}
		// check if the wire is connected
		if (localWire.isConnected()) {
			log("wire api test", "wire is connected");
		}
		else {
			log("wire api test", "wire is NOT connected");
		}
		// poll
		log("wire api test", "polling connected wire");
		value = (Object) localWire.poll();
		log("wire api test", "received value (42) " + value);
		// update
		log("wire api test", "updateing connected wire");
		localWire.update(new Integer(4242));
		// poll wire with producer throwing exception in its polled method
		log("wire api test",
				"polling connected wire with producer throwing exception");
		producer.throwsException = true;
		value = (Object) localWire.poll();
		log("wire api test", "received value (null) " + value);
		producer.throwsException = false;
		reg_producer.unregister();
		reg_consumer.unregister();
		wa.deleteWire(localWire);
	}

	/**
	 * Tests wire flow control
	 */
	public void testValueFiltering() throws Exception {
		String delimiter = "------------------------------------------------------------";
		String subtest = "value filtering test";
		String filter = "(" + WireConstants.WIREVALUE_CURRENT + "=5)";
		// create wire
		log(subtest, "create wire for the test");
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin.property", "42");
		h.put("org.osgi.test.wireadmin", "yes");
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		Wire localWire = wa.createWire("filter.test.producer.pid",
				"filter.test.conusmer.pid", h);
		// register producer
		log(subtest, "register test producer");
		Hashtable p_h = new Hashtable();
		p_h.put(WireConstants.WIREADMIN_PRODUCER_FLAVORS,
				new Class[] {Integer.class});
		p_h.put(Constants.SERVICE_PID, "filter.test.producer.pid");
		FilteredProducerImpl fpi = new FilteredProducerImpl();
		ServiceRegistration reg_producer = context.registerService(
				Producer.class.getName(), fpi, p_h);
		// register consumer
		log(subtest, "register test consumer");
		Hashtable c_h = new Hashtable();
		c_h.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS,
				new Class[] {Integer.class});
		c_h.put(Constants.SERVICE_PID, "filter.test.conusmer.pid");
		ServiceRegistration reg_consumer = context.registerService(
				Consumer.class.getName(), new FilteredConsumerImpl(this), c_h);
		// wait until filtered producer's method consumersConnected is called
		// with the test wire
		int i = 0;
		while ((fpi.wire == null) && (i++ < 500)) {
			try {
				Thread.sleep(20);
			}
			catch (InterruptedException e) {
			}
		}
		if (fpi.wire == null) {
			throw new Exception("producer not connected (wire is null)");
		}
		// current value test - filter is set on wire creation
		log(subtest,
				"Current value test - notifying producer. Will send integers [0..9]");
		log(subtest, "Filter is " + filter);
		log(subtest, "Consumer should receive values [5]");
		fpi.updateWire(1, false);
		int counter = 0;
		while ((counter++ < 100) && (FilteredConsumerImpl.valuesReceived < 1)) {
			Thread.sleep(50);
		}
		FilteredConsumerImpl.valuesReceived = 0;
		log(subtest, delimiter);
		// previous value test
		// last received value was 5 so the consumer should receive 0 and
		// nothing else
		filter = "(" + WireConstants.WIREVALUE_PREVIOUS + "=5)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		wa.updateWire(localWire, h);
		log(subtest,
				"Previous value test - notifying producer. Will send integers [0..9]");
		log(subtest, "Filter is " + filter);
		log(subtest, "Consumer should receive values [0]");
		fpi.updateWire(1, false);
		counter = 0;
		while ((counter++ < 100) && (FilteredConsumerImpl.valuesReceived < 1)) {
			Thread.sleep(50);
		}
		FilteredConsumerImpl.valuesReceived = 0;
		log(subtest, delimiter);
		// delta absolute test
		// last received value was 0 so we should receive:
		// 4 - absolute delta 4
		// 8 - absolute delta 8
		filter = "(" + WireConstants.WIREVALUE_DELTA_ABSOLUTE + ">=3)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		wa.updateWire(localWire, h);
		// delta absolute and delta relative tests need the delta increased to 2
		log(
				subtest,
				"Value delta absolute test - notifying producer. Will send integers [0, 2, 4, 6, 8]");
		log(subtest, "Filter is " + filter);
		log(subtest, "Consumer should receive values [4, 8]");
		fpi.updateWire(2, false);
		counter = 0;
		while ((counter++ < 100) && (FilteredConsumerImpl.valuesReceived < 2)) {
			Thread.sleep(50);
		}
		FilteredConsumerImpl.valuesReceived = 0;
		log(subtest, delimiter);
		// delta relative test
		// last received value was 8 so we should receive
		/* !!! last received value changes dinamicly during that test !!! */
		// 0 - relative delta Infinity
		// 2 - relative delta 1
		// 4 - relative delta 0.5
		// 8 - relative delta 0.5
		filter = "(" + WireConstants.WIREVALUE_DELTA_RELATIVE + ">=0.4)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		wa.updateWire(localWire, h);
		log(
				subtest,
				"Value delta relative test - notifying producer. Will send integers [0, 2, 4, 6, 8]");
		log(subtest, "Filter is " + filter);
		log(subtest, "Consumer should receive values [0, 2, 4, 8]");
		fpi.updateWire(2, false);
		counter = 0;
		while ((counter++ < 100) && (FilteredConsumerImpl.valuesReceived < 4)) {
			Thread.sleep(50);
		}
		FilteredConsumerImpl.valuesReceived = 0;
		log(subtest, delimiter);
		// time elapsed test
		// 4 should be received since it is the first one that has waited so
		// long
		filter = "(" + WireConstants.WIREVALUE_ELAPSED + ">=500)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		wa.updateWire(localWire, h);
		Thread.sleep(550); // ensure the time is elapsed
		log(subtest,
				"Time elapsed test - notifying producer. Will send integers [0, 2, 4, 6, 8]");
		log(subtest, "Filter is " + filter);
		log(subtest, "Consumer should receive values [0, 4, 6, 8]");
		// make the producer wait before sending updates
		fpi.updateWire(2, true);
		counter = 0;
		while ((counter++ < 500) && (FilteredConsumerImpl.valuesReceived < 4)) { // timeout
			// increased
			// to
			// 25
			// seconds
			Thread.sleep(50);
		}
		FilteredConsumerImpl.valuesReceived = 0;
		log(subtest, delimiter);
		// disable filtering test
		log(subtest,
				"update the producer properties - add WIREADMIN_PRODUCER_FILTERS property");
		p_h.put(WireConstants.WIREADMIN_PRODUCER_FILTERS, new Object());
		reg_producer.setProperties(p_h);
		filter = "(" + WireConstants.WIREVALUE_CURRENT + ">=5)";
		h.put(WireConstants.WIREADMIN_FILTER, filter);
		wa.updateWire(localWire, h);
		log(subtest,
				"Filtering disabled test - notifying producer. Will send integers [0, 3, 6, 9]");
		log(subtest, "Filter is " + filter);
		log(subtest, "Consumer should receive values [0, 3, 6, 9]");
		fpi.updateWire(3, false);
		counter = 0;
		while ((counter++ < 100) && (FilteredConsumerImpl.valuesReceived < 4)) {
			Thread.sleep(50);
		}
		FilteredConsumerImpl.valuesReceived = 0;
		log(subtest, delimiter);
		log(subtest, "Finished");
		wa.deleteWire(localWire);
		reg_producer.unregister();
		reg_consumer.unregister();
		
		// The test must wait several seconds. 
		//The events, genereated from the operations above, must be delivered in order not to interfere with the next test 
		try {
		  Thread.sleep(4000);
		} catch (InterruptedException ie) {}
	}

	/**
	 * Tests event dispatchment in the wire admin
	 */
	public void testEvents() throws Exception {
		String subtest = "event test";
		// a full mask
		Integer mask = new Integer(0xFFFFFFFF);
		// register the listeners
		TestWireAdminListener listener = new TestWireAdminListener(this, false); // should
		// receive
		// all
		// events
		TestWireAdminListener dummy = new TestWireAdminListener(this, true); // shouldn't
		// receive
		// events
		//real listener
		Hashtable walProps = new Hashtable();
		walProps.put(WireConstants.WIREADMIN_EVENTS, mask);
		ServiceRegistration listenerReg = context.registerService(
				WireAdminListener.class.getName(), listener, walProps);
		//dummy listener
		Hashtable dummyProps = new Hashtable();
		dummyProps.put(WireConstants.WIREADMIN_EVENTS, new Integer(0));
		ServiceRegistration dummyReg = context.registerService(
				WireAdminListener.class.getName(), dummy, dummyProps);
		// real test
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin.property", "42");
		h.put("org.osgi.test.wireadmin", "yes");
		log(subtest, "Create a wire: WIRE_CREATED event is expected");
		Wire w = null;
		listener.called = false;
		w = wa.createWire("producer.event.test.pid", "consumer.event.test.pid",
				h);
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		log(subtest, "update a wire: WIRE_UPDATED event is expected");
		listener.called = false;
		wa.updateWire(w, h);
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		log(subtest, "connect a wire: WIRE_CONNECTED event is expected");
		listener.called = false;
		helper.registerEventConsumer(false);
		helper.registerEventProducer(false);
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		log(subtest, "poll the wire: WIRE_TRACE event is expected");
		listener.called = false;
		w.poll();
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		log(subtest, "disconnect wire: WIRE_DISCONNECTED event is expected");
		listener.called = false;
		helper.unregisterEventConsumer();
		helper.unregisterEventProducer();
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		// receive only PRODUCERS_EXCEPTION, CONSUMERS_EXCEPTION and
		// WIRE_DELETED events from now
		mask = new Integer(WireAdminEvent.CONSUMER_EXCEPTION
				| WireAdminEvent.PRODUCER_EXCEPTION
				| WireAdminEvent.WIRE_DELETED);
		walProps.put(WireConstants.WIREADMIN_EVENTS, mask);
		listenerReg.setProperties(walProps);
		log(subtest,
				"cause exception from producersConnected: CONSUMER_EXCEPTION is expected");
		listener.called = false;
		helper.registerEventProducer(false);
		helper.registerEventConsumer(true);
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		log(subtest,
				"cause exception from updated: CONSUMER_EXCEPTION is expected");
		listener.called = false;
		w.update("42");
		synchronized (synch) {
			if (!listener.called)
				synch.wait(20000);
		}
		helper.unregisterEventConsumer();
		helper.unregisterEventProducer();
		log(subtest,
				"cause exception from consumersConnected: PRODUCER_EXCEPTION is expected");
		listener.called = false;
		helper.registerEventConsumer(false);
		helper.registerEventProducer(true);
		synchronized (synch) {
			if (!listener.called)
				synch.wait(20000);
		}
		log(subtest,
				"cause exception from polled: PRODUCER_EXCEPTION is expected");
		listener.called = false;
		w.poll();
		synchronized (synch) {
			if (!listener.called)
				synch.wait(20000);
		}
		helper.unregisterEventConsumer();
		helper.unregisterEventProducer();
		log(subtest, "delete wire: WIRE_DELETED event is expected");
		listener.called = false;
		wa.deleteWire(w);
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		log(
				subtest,
				"cause exception form producersConnected of a 'free' Consumer. CONSUMER_EXCEPTION is expected");
		listener.called = false;
		helper.registerEventConsumer(true);
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		log(
				subtest,
				"cause exception form consumersConnected of a 'free' Producer. PRODUCER_EXCEPTION is expected");
		listener.called = false;
		helper.registerEventProducer(true);
		synchronized (synch) {
			if (!listener.called)
				synch.wait(5000);
		}
		helper.unregisterEventConsumer();
		helper.unregisterEventProducer();
		listenerReg.unregister();
		dummyReg.unregister();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////wire admin test methods end
	// here ////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////state manager test methods start
	// here //////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void test_statemanagement_null_scopes() {
		// clean up the wire admin test case regs ...
		helper.unregisterAll();
		helper.deleteAllWires(wa);
		try {
			consumer = installBundle("tb1.jar");
			producer = installBundle("tb4.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		wire = helper.createWire(wa,"producer.ProducerImplA", "consumer.ConsumerImplA", null);
		if (wire.getScope() == null) {
			log("Test Wire Scope with null scope and without WirePermission",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire Scope without WirePermission", "NOT OK.");
		}
		
		wa.deleteWire(wire);
		
		
		setWirePermissions(new String[] {"*"}, consumer, true);
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplA", "consumer.ConsumerImplA", h);
		if (compareScopes(wire.getScope(), null)) {
			log(
					"Test Wire Scope with null scope, after setting WirePermissions",
					"Operation passed: OK.");
		}
		else {
			log(
					"Test Wire Scope with null scope, after setting WirePermissions",
					"NOT OK.");
		}
		envelope = new BasicEnvelope("true", "door1", "A");
		wire.update(envelope);
		if (!envelope.equals(wire.getLastValue())) {
			log("Test Wire update with null scope", "NOT OK.");
		}
		else {
			log("Test Wire update with null scope", "Operation passed: OK.");
		}
		removePermissions();
	}

	public void test_statemanagement_consumer_scope() {
		try {
			consumer = installBundle("tb2.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplA", "consumer.ConsumerImplB", h);
		if (wire.getScope() == null) {
			log(
					"Test Wire Scope with null producer scope without WirePermission",
					"Operation passed: OK.");
		}
		else {
			log(
					"Test Wire Scope with null producer scope without WirePermission",
					"NOT OK.");
		}
		wa.deleteWire(wire);
		setWirePermissions(new String[] {"*"}, consumer, true);
		setWirePermissions(new String[] {"*"}, producer, false);
		h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplA", "consumer.ConsumerImplB", h);
		if (compareScopes(wire.getScope(), null)) {
			log(
					"Test Wire Scope with null producer scope, after setting WirePermission",
					"Operation passed: OK.");
		}
		else {
			log(
					"Test Wire Scope with null producer scope, after setting WirePermission",
					"NOT OK.");
		}
		envelope = new BasicEnvelope(new Integer(42), "window1", "A");
		wire.update(envelope);
		if (!envelope.equals(wire.getLastValue())) {
			log("Test Wire update with null scope", "NOT OK.");
		}
		else {
			log("Test Wire update with null scope", "Operation passed: OK.");
		}
		removePermissions();
	}

	public void test_statemanagement_all_scope() {
		try {
			producer = installBundle("tb5.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplB", "consumer.ConsumerImplB", h);
		if (compareScopes(wire.getScope(), new String[] {"A", "B", "C"})) {
			log("Test Wire Scope without WirePermission",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire Scope without WirePermission", "NOT OK.");
		}
		envelope = new BasicEnvelope(new Integer(42), "window2", "YY");
		wire.update(envelope);
		if (wire.getLastValue() != null) {
			log("Test Wire update without permission", "NOT OK.");
		}
		else {
			log("Test Wire update without permission", "Operation passed: OK.");
		}
		wa.deleteWire(wire);
		setWirePermissions(new String[] {"B", "C"}, consumer, true);
		setWirePermissions(new String[] {"A", "B"}, producer, false);
		h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplB", "consumer.ConsumerImplB", h);
		if (compareScopes(wire.getScope(), new String[] {"B"})) {
			log("Test Wire Scope, after setting WirePermission",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire Scope, after setting WirePermission", "NOT OK.");
		}
		envelope = new BasicEnvelope(new Integer(42), "window2", "B");
		wire.update(envelope);
		if (envelope.equals(wire.getLastValue())) {
			log("Test Wire update with envelope object",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire update with envelope object", "NOT OK.");
		}
		removePermissions();
	}

	public void test_statemanagement_asterisk_consumer() {
		try {
			consumer = installBundle("tb3.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplB", "consumer.ConsumerImplC", h);
		if (compareScopes(wire.getScope(), new String[] {"A", "B", "C"})) {
			log("Test Wire Scope without WirePermission",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire Scope without WirePermission", "NOT OK.");
		}
		envelope = new BasicEnvelope("open", "door2", "DD");
		wire.update(envelope);
		if (wire.getLastValue() == null) {
			log("Test Wire update without permission", "Operation passed: OK.");
		}
		else {
			log("Test Wire update without permission", "NOT OK.");
		}
		setWirePermissions(new String[] {"*"}, consumer, true);
		setWirePermissions(new String[] {"A", "B", "C"}, producer, false);
		wa.deleteWire(wire);
		h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplB", "consumer.ConsumerImplC", h);
		if (compareScopes(wire.getScope(), new String[] {"A", "B", "C"})) {
			log("Test Wire Scope, after setting WirePermission",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire Scope, after setting WirePermission", "NOT OK.");
		}
		envelope = new BasicEnvelope("locked", "backdoor", "B");
		wire.update(envelope);
		if (envelope.equals(wire.getLastValue())) {
			log("Test Wire update with envelope object",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire update with envelope object", "NOT OK.");
		}
		removePermissions();
	}

	public void test_statemanagement_all_asterisk() {
		try {
			producer = installBundle("tb6.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		setWirePermissions(new String[] {"*"}, consumer, true);
		setWirePermissions(new String[] {"*"}, producer, false);
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplC", "consumer.ConsumerImplC", h);
		if (compareScopes(wire.getScope(), new String[] {"*"})) {
			log("Test Wire Scope with *(scope) and *(WirePermission)",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire Scope with *(scope) and *(WirePermission)",
					"NOT OK.");
		}
		envelope = new BasicEnvelope(new Integer(5), "numberDoors", "AA");
		wire.update(envelope);
		if (envelope.equals(wire.getLastValue())) {
			log("Test Wire update with envelope object",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire update with envelope object", "NOT OK.");
		}
		removePermissions();
	}

	public void test_statemanagement_missing_scope_permissions() {
		try {
			consumer = installBundle("tb2.jar");
			producer = installBundle("tb5.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		setWirePermissions(new String[] {"A", "B", "C"}, consumer, true);
		setWirePermissions(new String[] {"X"}, producer, false);
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplB", "consumer.ConsumerImplB", h);
		if (compareScopes(wire.getScope(), new String[] {})) {
			log("Test Wire Scope", "Operation passed: OK.");
		}
		else {
			log("Test Wire Scope", "NOT OK.");
		}
		envelope = new BasicEnvelope(new Integer(44), "numberWindows", "A");
		wire.update(envelope);
		if (wire.getLastValue() == null) {
			log("Test Wire update with incompatible wire scope",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire update with incompatible wire scope", "NOT OK.");
		}
		removePermissions();
	}

	public void test_statemanagement_scope_intersection() {
		try {
			producer = installBundle("tb7.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		setWirePermissions(new String[] {"*"}, consumer, true);
		setWirePermissions(new String[] {"C", "E"}, producer, false);
		Hashtable h = new Hashtable();
		h.put("org.osgi.test.wireadmin", "yes");
		wire = wa.createWire("producer.ProducerImplD", "consumer.ConsumerImplB", h);
		if (compareScopes(wire.getScope(), new String[] {"C"})) {
			log("Test Wire Scope", "Operation passed: OK.");
		}
		else {
			log("Test Wire Scope", "NOT OK.");
		}
		envelope = new BasicEnvelope("unlocked", "front.Door", "A");
		wire.update(envelope);
		if (wire.getLastValue() == null) {
			log("Test Wire update with incompatible wire scope",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire update with incompatible wire scope", "NOT OK.");
		}
		envelope = new BasicEnvelope("closed", "front.Door", "C");
		wire.update(envelope);
		if (envelope.equals(wire.getLastValue())) {
			log("Test Wire update with correct envelope object",
					"Operation passed: OK.");
		}
		else {
			log("Test Wire update with correct envelope object", "NOT OK.");
		}
		removePermissions();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////state manager test methods end
	// here //////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////// auxil methods
	// ///////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * flag - true -> consumer, false -> producer
	 *  
	 */
	private void setWirePermissions(String[] permissions, Bundle bundle,
			boolean flag) {
		String type = flag ? WirePermission.CONSUME : WirePermission.PRODUCE;
		PermissionInfo[] perms = new PermissionInfo[permissions.length];
		for (int i = 0; i < permissions.length; i++) {
			perms[i] = new PermissionInfo(
					"org.osgi.service.wireadmin.WirePermission",
					permissions[i], type);
		}
		pa.setPermissions(bundle.getLocation(), perms);
		p.put(bundle.getLocation(), bundle.getLocation());
	}

	protected boolean compareScopes(String[] current, String[] correct) {
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

	private void removePermissions() {
		Enumeration e = p.keys();
		while (e.hasMoreElements()) {
			pa.setPermissions((String) e.nextElement(), null);
		}
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
			if (returnedWires.get(producerPID) == null) {
				log("{" + producerPID + "} Invokes consumersConnected(..) ",
						"NOT OK, Not invoked");
			}
			else {
				if (((Wire[]) returnedWires.get(producerPID)).length == 0) {
					returned = null;
				}
				else {
					returned = (Wire[]) returnedWires.get(producerPID);
				}
				if (compareWires_Flavors(wires, returned)) {
					log(
							"{" + producerPID
									+ "} Invokes consumersConnected(..) ",
							"CORRECT");
				}
				else {
					log(
							"{" + producerPID
									+ "} Invokes consumersConnected(..) ",
							"NOT OK, Wrong Wire[]");
				}
				returnedWires.remove(producerPID);
			}
		}
		if (!consumerPID.equals("")) {
			wires = getConnected(getWiresForFilter("(wireadmin.consumer.pid="
					+ consumerPID + ")"));
			if (returnedWires.get(consumerPID) == null) {
				log("{" + consumerPID + "} Invokes producersConnected(..) ",
						"NOT OK, Not invoked");
			}
			else {
				if (((Wire[]) returnedWires.get(consumerPID)).length == 0) {
					returned = null;
				}
				else
					returned = (Wire[]) returnedWires.get(consumerPID);
				if (compareWires_Flavors(wires, returned)) {
					log(
							"{" + consumerPID
									+ "} Invokes producersConnected(..) ",
							"CORRECT");
				}
				else {
					log(
							"{" + consumerPID
									+ "} Invokes producersConnected(..) ",
							"NOT OK, Wrong Wire[]");
				}
				returnedWires.remove(consumerPID);
			}
		}
	}

	public void addInHashtable(String pid, Wire[] wires) {
		returnedWires.put(pid, (wires == null) ? new Wire[0] : wires);
	}

	protected boolean compareWires_Flavors(Object[] current, Object[] correct) {
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

	protected Wire[] getWiresForFilter(String filter) {
		try {
			return wa.getWires(filter);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private PermissionAdmin getPermissionAdmin() {
		pRef = context
				.getServiceReference("org.osgi.service.permissionadmin.PermissionAdmin");
		if (pRef != null) {
			return (PermissionAdmin) context.getService(pRef);
		}
		else {
			throw new RuntimeException(
					"PermissionAdmin service not present! Unable to proceed test");
		}
	}

	private WireAdmin getWireadmin() {
		context = getContext();
		waRef = context
				.getServiceReference("org.osgi.service.wireadmin.WireAdmin");
		if (waRef != null) {
			return (WireAdmin) context.getService(waRef);
		}
		else {
			throw new RuntimeException(
					"Wire admin service not present! Unable to proceed test");
		}
	}

	////////// some helper methods for additonal debugging ... not used by
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
				ServiceReference[] producers = context.getServiceReferences(
						Producer.class.getName(), filter);
				if (producers == null) {
					System.out.println("No producers registered for filter "
							+ filter);
					return;
				}
				for (int i = 0; i < producers.length; i++) {
					String[] props = producers[i].getPropertyKeys();
					System.out.println("Producer: "
							+ context.getService(producers[i]));
					context.ungetService(producers[i]);
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
				ServiceReference[] consumers = context.getServiceReferences(
						Consumer.class.getName(), filter);
				if (consumers == null) {
					System.out.println("No consumer registered for filter "
							+ filter);
					return;
				}
				for (int i = 0; i < consumers.length; i++) {
					String[] props = consumers[i].getPropertyKeys();
					System.out.println("Consumer: "
							+ context.getService(consumers[i]));
					context.ungetService(consumers[i]);
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