/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
package org.osgi.test.cases.cu.tbc;

import java.util.Hashtable;

import org.osgi.framework.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;
import org.osgi.util.measurement.Measurement;
import org.osgi.util.measurement.Unit;
import org.osgi.service.cu.admin.*;
import org.osgi.service.cu.admin.spi.ManagedControlUnit;
import org.osgi.service.cu.*;


/**
 * <remove>The TemplateControl controls is downloaded in the target and will control the
 * test run. The description of this test cases should contain the overall
 * execution of the run. This description is usuall quite minimal because the
 * main description is in the TemplateTestCase.</remove>
 * 
 * TODO Add Javadoc comment for this.
 * 
 * @version $Revision$
 */

public class TestControl extends DefaultTestBundleControl {
	private ServiceReference ref;
	private ControlUnitAdmin admin;
	private Bundle tb1;
	private Bundle tb2;
	private ServiceRegistration reg;
	
	/**
	 * List of test methods used in this test case.
	 */
	static String[]	methods	= new String[] {"testControlUnitConstants",
											"testCreationOfCUs",
											"testDestructionOfCUs",
											"testFindCUs",
											"testListCUs",
											"testInvokeAction",
											"testQueryStateVariable",
											"testControlUnit",
											"testSVListeners",
											"testCUListeners",
											"testHierarchyListeners"};

	/**
	 * Returns the list of test methods contained in this test case.
	 * 
	 * @return list of test methods
	 */
	public String[] getMethods() {
		return methods;
	}
	
	/**
	 * Checks if some prerequisites are met like getting some services
	 * 
	 * @return true if the pre-requisites are met, otherwise false
	 */
	public boolean checkPrerequisites() {
		ref = getContext().getServiceReference(ControlUnitAdmin.class.getName());
		if (ref == null)
			return false;
		admin = (ControlUnitAdmin) getContext().getService(ref);
		if (admin == null)
			return false;
		return true;
	}
		
	/**
	 * Enumerates through all constants defined in ControlUnitConstants interface.
	 */
	public void testControlUnitConstants() {
		log("org.osgi.control.id", ControlUnitConstants.ID);
		log("org.osgi.control.type", ControlUnitConstants.TYPE);
		log("org.osgi.control.version", ControlUnitConstants.VERSION);
		log("org.osgi.control.parent.id", ControlUnitConstants.PARENT_ID);
		log("org.osgi.control.parent.type", ControlUnitConstants.PARENT_TYPE);
	  	log("org.osgi.control.event.auto_receive", ControlUnitConstants.EVENT_AUTO_RECEIVE);
	  	log("org.osgi.control.event.filter", ControlUnitConstants.EVENT_FILTER);
	  	log("org.osgi.control.event.sync", ControlUnitConstants.EVENT_SYNC);
	  	log("org.osgi.control.var.id", ControlUnitConstants.STATE_VARIABLE_ID);
	  	log("org.osgi.control.var.list", ControlUnitConstants.STATE_VARIABLES_LIST);
	}
	
	/**
	 * Test the creation of control units using create methods
	 */
	public void testCreationOfCUs() {
		
		// Test creation with null arguments
		try {
			admin.createControlUnit(null, null, null);
		}
		catch (Exception e) {
			assertException("Creation with all arguments set to null", e.getClass(), e);
		}
		
		// Test creation with unknow control unit type
		try {
			admin.createControlUnit("test", null, null);
		}
		catch (Exception e) {
			assertException("Creation with an unknown CU", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("hip", null, null);
		}
		catch (Exception e) {
			assertException("Creation with a ManagedControlUnit", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("door", null, null);
		}
		catch (Exception e) {
			assertException("Creation from a CUFactory with other arguments set to null", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("door", "create.test", null);
		}
		catch (Exception e) {
			assertException("Creation from a CUFactory with an unknown method", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("door", "door.create", new Short("1"));
		}
		catch (Exception e) {
			assertException("Creation from a CUFactory with some arguments", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("door", "door.create", null);
			log("Creation from CUFactory, type = door, id = door.1");
		}
		catch (Exception e) {
			assertException("Creation from a CUFactory with some arguments", e.getClass(), e);
		}
	}
	
	/**
	 * Test destruction of control units using destroy methods.
	 */
	public void testDestructionOfCUs() {
		
		// Test destruction with null arguments
		try {
			admin.destroyControlUnit(null, null);
		}
		catch (Exception e) {
			assertException("Destruction with all arguments set to null", e.getClass(), e);
		}
		
		// Test destruction with unknow control unit
		try {
			admin.destroyControlUnit("test", null);
		}
		catch (Exception e) {
			assertException("Destruction of an unknown CU", e.getClass(), e);
		}
		
		// Test destruction with a ManagedControlUnit service
		try {
			admin.destroyControlUnit("hip", null);
		}
		catch (Exception e) {
			assertException("Destruction of a ManagedControlUnit", e.getClass(), e);
		}
		
		// Test destruction of CUFactory with id set to null
		try {
			admin.destroyControlUnit("door", null);
		}
		catch (Exception e) {
			assertException("Destruction from a CUFactory: type = door, id set to null", e.getClass(), e);
		}
		
		// Test destruction of a CUFactory with unknown id
		try {
			admin.destroyControlUnit("door", "door.x");
		}
		catch (Exception e) {
			assertException("Destruction from a CUFactory: type = door, id = door.x", e.getClass(), e);
		}
		
		// Test destruction of a known CU
		try {
			admin.destroyControlUnit("door", "door.1");
			log("Destruction of CU: type = door, id = door.1");
		}
		catch (Exception e) {
			assertException("Destruction of CU: type = door, id = door.1", e.getClass(), e);
		}		
	}

	/**
	 * Test search of control units using find methods.
	 */
	public void testFindCUs() {
		String[] types;
		
		// Search of CUs with all arguments set to null
		try {
			types = admin.findControlUnits(null, null, null);
			log("Find control units with all arguments set to null: ");
			if (types != null) listStringArray(types);
		}
		catch (Exception e) {
			assertException("Find CUs with all arguments set to null: ", e.getClass(), e);
		}
		
		// Search of CUs with unknown find method
		try {
			types = admin.findControlUnits("door", "test", null);
			log("Find CUs with a wrong find method: ");
			if (types != null) listStringArray(types);
		}
		catch (Exception e) {
			assertException("Find CUs with a wrong find method: ", e.getClass(), e);
		}
		
		// TODO
		// Search of CUs with known find method
	}
	
	/**
	 * Test get of control units using a set of get methods.
	 */
	public void testListCUs() {
		String[] types;
		
		// Creation of all CUs for test
		try {
		for (int i=0; i<4; i++)
			admin.createControlUnit("door", "door.create", null);
		for (int i=0; i<4; i++)
			admin.createControlUnit("window", "window.create", null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// List all CU Types
		log("List All CU Types:");		
		types = admin.getControlUnitTypes();
		if (types != null) listStringArray(types);
		
		// List Parent CUs of a CU
		try {
			types = admin.getParentControlUnits(null, null, null);
		}
		catch (NullPointerException e) {
			assertException("List of Parent CUs with all arguments set to null: ", e.getClass(), e);
		}
		log("List Parent CUs of a Managed CU without parents:");
		types = admin.getParentControlUnits("hip", "hip.id", "");
		if (types != null) listStringArray(types);
		log("List Parent CUs of a Managed CU with parents:");
		types = admin.getParentControlUnits("hip.gyro", "hip.gyro.id", "hip");
		if (types != null) listStringArray(types);
		log("List Parent CUs of a CUFactory without parents:");
		types = admin.getParentControlUnits("door", "door.1", "");
		if (types != null) listStringArray(types);
		log("List Parent CUs of a CUFactory with parents:");
		types = admin.getParentControlUnits("window", "window.1", "door");
		if (types != null) listStringArray(types);
		
		// List Parent Types of a CU Type
		try {
			types = admin.getParentControlUnitTypes(null);
		}
		catch (NullPointerException e) {
			assertException("List Parent Types with arguments set to null: ", e.getClass(), e);
		}
		log("List Parent Types with an unknown CU:");
		types = admin.getParentControlUnitTypes("test");
		if (types != null) listStringArray(types);
		log("List Parent Types of a Managed CU without parents:");
		types = admin.getParentControlUnitTypes("hip");
		if (types != null) listStringArray(types);
		log("List Parent Types of a Managed CU with parents:");
		types = admin.getParentControlUnitTypes("hip.tacho");
		if (types != null) listStringArray(types);
		log("List Parent Types of a FactoryCU without parents:");
		types = admin.getParentControlUnitTypes("door");
		if (types != null) listStringArray(types);
		log("List Parent Types of a FactoryCU with parents:");
		types = admin.getParentControlUnitTypes("window");
		if (types != null) listStringArray(types);
		
		// List SubCUs of a CU
		try {
			types = admin.getSubControlUnits(null, null, null);
		}
		catch (NullPointerException e) {
			assertException("List SubCUs of a Managed CU with arguments set to null: ", e.getClass(), e);
		}
		log("List SubCUs of a Managed CU without children:");
		types = admin.getSubControlUnits("hip.gyro", "hip.gyro.id", "hip");
		if (types != null) listStringArray(types);
		log("List SubCUs of a Managed CU with children:");
		types = admin.getSubControlUnits("hip", "hip.id", "hip.gyro");
		if (types != null) listStringArray(types);
		log("List SubCUs of a CUFactory without children:");
		types = admin.getSubControlUnits("window", "window.1", "door");
		if (types != null) listStringArray(types);
		log("List SubCUs of a CUFactory with children:");
		types = admin.getSubControlUnits("door", "door.1", "window");
		if (types != null) listStringArray(types);
		
		// List SubTypes of a CU Type
		try {
			types = admin.getSubControlUnitTypes(null);
		}
		catch (NullPointerException e) {
			assertException("List Sub Types with arguments set to null: ", e.getClass(), e);
		}
		log("List Sub Types with an unknown CU:");
		types = admin.getSubControlUnitTypes("test");
		if (types != null) listStringArray(types);
		log("List Sub Types of a Managed CU without children:");
		types = admin.getSubControlUnitTypes("hip.gyro");
		if (types != null) listStringArray(types);
		log("List Sub Types of a Managed CU with children:");
		types = admin.getSubControlUnitTypes("hip");
		if (types != null) listStringArray(types);
		log("List Sub Types of a FactoryCU without children:");
		types = admin.getSubControlUnitTypes("window");
		if (types != null) listStringArray(types);
		log("List Sub Types of a FactoryCU with children:");
		types = admin.getSubControlUnitTypes("door");
		if (types != null) listStringArray(types);

		// Retrieve a CU from its type and Id
		ControlUnit cu;
		try {
			cu = admin.getControlUnit(null, null);
		}
		catch (NullPointerException e) {
			assertException("Retrieve a CU with arguments set to null: ", e.getClass(), e);
		}
		log("Retrieve a CU with type unknown, id unknown:");
		cu = admin.getControlUnit("test", "test");
		if (cu!= null) log ("CU found");
		log("Retrieve a CU with type known, id unknown:");
		cu = admin.getControlUnit("hip", "test");
		if (cu!= null) log ("CU found");
		log("Retrieve a CU with type = hip, id = hip.id:");
		cu = admin.getControlUnit("hip", "hip.id");
		if (cu!= null) log ("CU found");
		log("Retrieve a CU with type = door, id = door.4:");
		cu = admin.getControlUnit("door", "door.4");
		if (cu!= null) log ("CU found");
		
		// Retrieve Type Version of a CU Type
		try {
			admin.getControlUnitTypeVersion(null);
		}
		catch (NullPointerException e) {
			assertException("Retrieve Type Version of a CU with type = null: ", e.getClass(), e);
		}
		try {
			admin.getControlUnitTypeVersion("test");
		}
		catch (IllegalArgumentException e) {
			assertException("Retrieve Type Version of a CU with type unknown: ", e.getClass(), e);
		}
		log("Retrieve Type Version of a CU with type = hip: " + admin.getControlUnitTypeVersion("hip"));
	}
	
	/**
	 * Tests the invocation of actions using invokeAction method.
	 */
	public void testInvokeAction() {
		// Invoke an action with all arguments set to null
		try {
			admin.invokeAction(null, null, null, null);
		}
		catch (Exception e) {
			assertException("Invoke an action with all arguments set to null: ", e.getClass(), e);
		}
		
		// Invkoke an unknown action
		try {
			admin.invokeAction("hip.gyro", "hip.gyro.id", "test", null);
		}
		catch (Exception e) {
			assertException("Invoke an unknown action: ", e.getClass(), e);
		}
		
		// Invoke an action without arguments
		try {
			Measurement res;
			res = (Measurement)admin.invokeAction("hip.gyro", "hip.gyro.id", "hip.gyro.getZRO", null);
			log ("Invoke an action without arguments: " + res);
		}
		catch (Exception e) {
			assertException("Invoke an action without arguments: ", e.getClass(), e);
		}
		
		// Invoke an action with wrong arguments
		try {
			Object[] args1 = {new Byte("1"), new Byte("3")};
			admin.invokeAction("hip.gyro", "hip.gyro.id", "hip.gyro.calibrate", args1);
		}
		catch (Exception e) {
			assertException("Invoke an action with wrong arguments: ", e.getClass(), e);
		}
		
		// Invoke an action with some arguments
		try {
			Object[] args2 = {new Measurement(1, Unit.V), new Measurement(3, Unit.rad)};
			admin.invokeAction("hip.gyro", "hip.gyro.id", "hip.gyro.calibrate", args2);
			log ("Invoke an action with arguments: OK");
		}
		catch (Exception e) {
			assertException("Invoke an action with arguments: ", e.getClass(), e);
		}
	}
	
	/**
	 * Tests the query of state variables using queryStateVariable method.
	 */
	public void testQueryStateVariable() {
		// Query a state variable with all arguments set to null
		try {
			admin.queryStateVariable(null, null, null);
		}
		catch (Exception e) {
			assertException("Query a state variable with all arguments set to null: ", e.getClass(), e);
		}
		
		// Query a state of an unknown variable
		try {
			admin.queryStateVariable("hip.gyro", "hip.gyro.id", "test");
		}
		catch (Exception e) {
			assertException("Query an unknown state variable: ", e.getClass(), e);
		}
		
		// Query a state variable
		try {
			Object res;
			res = admin.queryStateVariable("hip.gyro", "hip.gyro.id", "hip.gyro.rawOutput");
			log("Query a state variable: " + res);
		}
		catch (Exception e) {
			assertException("Query a state variable: ", e.getClass(), e);
		}		
	}

	/**
	 * Tests access to control units
	 */
	public void testControlUnit() {
		ControlUnit cu;
		
		// Test access of a control unit
		log("Access a ControlUnit");
		cu = admin.getControlUnit("hip.tacho", "hip.tacho.id");
		
		if (cu != null) {
			log("type = " + cu.getType());
			log("id = " + cu.getId());
			try {
				Object res;
				res = cu.invokeAction("hip.tacho.isDppValid", null);
				log("Invoke an action : " + res);
			}
			catch (Exception e) {
				assertException("Invoke an action: ", e.getClass(), e);
			} 
			
			try {
				Object res;
				res = cu.queryStateVariable("hip.tacho.rawOutput");
				log("Query a state variable value: " + res);
			}
			catch (Exception e) {
				assertException("Query a state variable value: ", e.getClass(), e);
			} 
		}
	}
	
	/**
	 * Tests of listeners of state variable changes
	 */
	public void testSVListeners() {
		Bundle tb3 = null;
		
		try {
			tb3 = installBundle("tb3.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Tests the notification of state changes of a door
		ControlUnit cu = admin.getControlUnit("door", "door.4");
		if (cu != null) {
			try {
				cu.invokeAction("door.open", null);
			}
			catch (Exception e) {
				assertException("Invoke an action: ", e.getClass(), e);
			} 
		}		
		
		// Tests the notification of state changes of a window
		cu = admin.getControlUnit("window", "window.1");
		if (cu != null) {
			try {
				cu.invokeAction("window.open", null);
			}
			catch (Exception e) {
				assertException("Invoke an action: ", e.getClass(), e);
			} 
		}	
		
		// Uninstall bundle to avoid interference with other test methods
		try {
			if (tb3 != null)
				uninstallBundle(tb3);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests of listeners of control unit changes
	 */
	public void testCUListeners() {
		Bundle tb4 = null;
		
		try {
			tb4 = installBundle("tb4.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Tests notification of Control Unit events while destroying all Cus
		// and re-creation of all Cus
		try {
			admin.destroyControlUnit("window","window.1");
			admin.destroyControlUnit("window","window.2");
			admin.destroyControlUnit("window","window.3");
			admin.destroyControlUnit("window","window.4");
			for (int i=0; i<4; i++)
				admin.createControlUnit("window", "window.create", null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (tb2 != null)
				uninstallBundle(tb2);
			installBundle("tb2.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Uninstall bundle to avoid interference with other test methods
		try {
			if (tb4 != null)
				uninstallBundle(tb4);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests of listeners of control unit hierarchy
	 */
	public void testHierarchyListeners() {
		Bundle tb5 = null;
		
		try {
			tb5 = installBundle("tb5.jar");
			if (tb2 != null)
				uninstallBundle(tb2);
			installBundle("tb2.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (reg != null) {
			Hashtable props = new Hashtable();
			
			// Change Properties to add the link to HipModule
			log("Change properties to attach hip.position to its parent hip");
			props.clear();
			props.put(ControlUnitConstants.TYPE, "hip.position");
			props.put(ControlUnitConstants.ID, "hip.position.id");
			props.put(ControlUnitConstants.PARENT_TYPE, "hip");
			props.put(ControlUnitConstants.PARENT_ID, "hip.id");
			reg.setProperties(props);
		
			// Change Properties to remove link to HipModule
			log("Change properties to detach hip.position from its parent hip");
			props.clear();
			props.put(ControlUnitConstants.TYPE, "hip.position");
			props.put(ControlUnitConstants.ID, "hip.position.id");
			reg.setProperties(props);
		
			reg.unregister();
		}
	}

	/**
	 * <remove>Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone. To save a lot
	 * of time in debugging, clean up all possible persistent remains
	 * before the test is run. Clean up is better don in the prepare
	 * because debugging sessions can easily cause the unprepare never
	 * to be called.</remove> 
	 */
	public void prepare() {
		log("#before each run");
		
		// Register a new CU child of HipModule
		Hashtable props = new Hashtable();
		props.put(ControlUnitConstants.TYPE, "hip.position");
		props.put(ControlUnitConstants.ID, "hip.position.id");
		
		reg = getContext().registerService(ManagedControlUnit.class.getName(), new HipPosition(), props);
		
		try {
			tb1 = installBundle("tb1.jar");
			tb2 = installBundle("tb2.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clean up after a run. Notice that during debugging
	 * many times the unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
		try {
			uninstallAllBundles();
		}
		catch (Exception e) {
		}
	}

	/**
	 * Logs elements of an array of String.
	 * @param elts
	 */
	private void listStringArray(String[] elts) {
		if (elts != null) 
			for (int i=0; i<elts.length; i++)
				log(elts[i]);
	}
}