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
			log("Creation with all arguments set to null: OK");
		}
		catch (Exception e) {
			assertException("Creation with all arguments set to null", e.getClass(), e);
		}
		
		// Test creation with unknow control unit type
		try {
			admin.createControlUnit("test", null, null);
			log("Creation with an unknown CU: OK");
		}
		catch (ControlUnitException e) {
			assertException("Creation with an unknown CU", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("hip", null, null);
			log("Creation with a ManagedControlUnit: OK");
		}
		catch (ControlUnitException e) {
			assertException("Creation with a ManagedControlUnit", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("door", null, null);
			log("Creation from a CUFactory with other arguments set to null: OK");
		}
		catch (ControlUnitException e) {
			assertException("Creation from a CUFactory with other arguments set to null", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("door", "create.test", null);
			log("Creation from a CUFactory with an unknown method: OK");
		}
		catch (ControlUnitException e) {
			assertException("Creation from a CUFactory with an unknown method", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("door", "$create.door", new Short("1"));
			log("Creation from a CUFactory with wrong arguments: OK");
		}
		catch (ControlUnitException e) {
			assertException("Creation from a CUFactory with wrong arguments", e.getClass(), e);
		}
		
		// Test creation with a ManagedControlUnit service
		try {
			admin.createControlUnit("door", "$create.door", null);
			log("Creation from a CUFactory with correct arguments: OK");
		}
		catch (ControlUnitException e) {
			assertException("Creation from a CUFactory with correct arguments", e.getClass(), e);
		}
	}
	
	/**
	 * Test destruction of control units using destroy methods.
	 */
	public void testDestructionOfCUs() {
		
		// Test destruction with null arguments
		try {
			admin.destroyControlUnit(null, null);
			log("Destruction with all arguments set to null: OK");
		}
		catch (Exception e) {
			assertException("Destruction with all arguments set to null", e.getClass(), e);
		}
		
		// Test destruction with unknow control unit
		try {
			admin.destroyControlUnit("test", "");
			log("Destruction of an unknown CU: OK");
		}
		catch (Exception e) {
			assertException("Destruction of an unknown CU", e.getClass(), e);
		}
		
		// Test destruction of a CUFactory with unknown id
		try {
			admin.destroyControlUnit("door", "door.x");
			log("Destruction from a CUFactory: type = door, id = door.x: OK");
		}
		catch (ControlUnitException e) {
			assertException("Destruction from a CUFactory: type = door, id = door.x", e.getClass(), e);
		}
		
		// Test destruction of a known CU
		try {
			admin.destroyControlUnit("door", "door.1");
			log("Destruction of CU: type = door, id = door.1: OK");
		}
		catch (ControlUnitException e) {
			assertException("Destruction of CU: type = door, id = door.1", e.getClass(), e);
		}		
	}

	/**
	 * Test search of control units using find methods.
	 */
	public void testFindCUs() {
		String[] types;
		
		// Creation of all CUs for test
		try {
		for (int i=0; i<4; i++)
			admin.createControlUnit("door", "$create.door", null);
		for (int i=0; i<4; i++)
			admin.createControlUnit("window", "$create.window", null);
		}
		catch (ControlUnitException e) {
			e.printStackTrace();
		}
		
		// Search of CUs with all arguments set to null
		try {
			types = admin.findControlUnits(null, null, null);
			log("Find CUs with all arguments set to null: OK");
			if (types != null) listStringArray(types);
		}
		catch (Exception e) {
			assertException("Find CUs with all arguments set to null: ", e.getClass(), e);
		}
		
		// Search of CUs with unknown find method
		try {
			types = admin.findControlUnits("door", "test", null);
			log("Find CUs with a wrong find method: OK");
			if (types != null) listStringArray(types);
		}
		catch (ControlUnitException e) {
			assertException("Find CUs with a wrong find method: ", e.getClass(), e);
		}
		
		// Search of CUs with known find method
		try {
			types = admin.findControlUnits("door", "$find.all", null);
			log("Find CUs with a correct find method: OK");
			if (types != null) listStringArray(types);
		}
		catch (ControlUnitException e) {
			assertException("Find CUs with a correct find method: ", e.getClass(), e);
		}
	}
	
	/**
	 * Test get of control units using a set of get methods.
	 */
	public void testListCUs() {
		String[] types;
		
		// List all CU Types
		types = admin.getControlUnitTypes();
		log("List All CU Types: OK");		
		if (types != null) listStringArray(types);
		
		// List Parent CUs of a CU
		try {
			types = admin.getParentControlUnits(null, null, null);
			log("List of Parent CUs with all arguments set to null: OK");
			if (types != null) listStringArray(types);
		}
		catch (Exception e) {
			assertException("List of Parent CUs with all arguments set to null: ", e.getClass(), e);
		}
		
		try {
			types = admin.getParentControlUnits("hip", "hip.id", "");
			log("List Parent CUs of a Managed CU without parents: OK");
			if (types != null) listStringArray(types);
		}
		catch (ControlUnitAdminException e) {
			assertException("List Parent CUs of a Managed CU without parents: ", e.getClass(), e);
		}
		
		try {
			types = admin.getParentControlUnits("hip.gyro", "hip.gyro.id", "hip");
			log("List Parent CUs of a Managed CU with parents: OK");
			if (types != null) listStringArray(types);
		}
		catch (ControlUnitAdminException e) {
			assertException("List Parent CUs of a Managed CU with parents: ", e.getClass(), e);
		}
		
		try {
			types = admin.getParentControlUnits("door", "door.1", "");
			log("List Parent CUs of a CUFactory without parents: OK");
			if (types != null) listStringArray(types);
		}
		catch (ControlUnitAdminException e) {
			assertException("List Parent CUs of a CUFactory without parents: ", e.getClass(), e);
		}
		
		try {
			types = admin.getParentControlUnits("window", "window.1", "door");
			log("List Parent CUs of a CUFactory with parents: OK");
			if (types != null) listStringArray(types);
		}
		catch (ControlUnitAdminException e) {
			assertException("List Parent CUs of a CUFactory with parents: ", e.getClass(), e);
		}
		
		// List Parent Types of a CU Type
		try {
			types = admin.getParentControlUnitTypes(null);
			log("List Parent Types with arguments set to null: OK");
		}
		catch (NullPointerException e) {
			assertException("List Parent Types with arguments set to null: ", e.getClass(), e);
		}
		
		types = admin.getParentControlUnitTypes("test");
		log("List Parent Types with an unknown CU: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getParentControlUnitTypes("hip");
		log("List Parent Types of a Managed CU without parents: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getParentControlUnitTypes("hip.tacho");
		log("List Parent Types of a Managed CU with parents: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getParentControlUnitTypes("door");
		log("List Parent Types of a FactoryCU without parents: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getParentControlUnitTypes("window");
		log("List Parent Types of a FactoryCU with parents: OK");
		if (types != null) listStringArray(types);
		
		// List SubCUs of a CU
		try {
			types = admin.getSubControlUnits(null, null, null);
			log("List SubCUs of a Managed CU with arguments set to null: OK");
		}
		catch (NullPointerException e) {
			assertException("List SubCUs of a Managed CU with arguments set to null: ", e.getClass(), e);
		}
		
		types = admin.getSubControlUnits("hip.gyro", "hip.gyro.id", "hip");
		log("List SubCUs of a Managed CU without children: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getSubControlUnits("hip", "hip.id", "hip.gyro");
		log("List SubCUs of a Managed CU with children: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getSubControlUnits("window", "window.1", "door");
		log("List SubCUs of a CUFactory without children: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getSubControlUnits("door", "door.1", "window");
		log("List SubCUs of a CUFactory with children: OK");
		if (types != null) listStringArray(types);
		
		// List SubTypes of a CU Type
		try {
			types = admin.getSubControlUnitTypes(null);
			log("List Sub Types with arguments set to null: OK");
		}
		catch (NullPointerException e) {
			assertException("List Sub Types with arguments set to null: ", e.getClass(), e);
		}
		
		types = admin.getSubControlUnitTypes("test");
		log("List Sub Types with an unknown CU: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getSubControlUnitTypes("hip.gyro");
		log("List Sub Types of a Managed CU without children: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getSubControlUnitTypes("hip");
		log("List Sub Types of a Managed CU with children: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getSubControlUnitTypes("window");
		log("List Sub Types of a FactoryCU without children: OK");
		if (types != null) listStringArray(types);
		
		types = admin.getSubControlUnitTypes("door");
		log("List Sub Types of a FactoryCU with children: OK");
		if (types != null) listStringArray(types);

		// Retrieve a CU from its type and Id
		ControlUnit cu;
		try {
			cu = admin.getControlUnit(null, null);
			log("Retrieve a CU with arguments set to null: OK");
		}
		catch (Exception e) {
			assertException("Retrieve a CU with arguments set to null: ", e.getClass(), e);
		}

		try {
			cu = admin.getControlUnit("test", "test");
			log("Retrieve a CU with type unknown, id unknown: OK");
			if (cu!= null) log ("CU found");
		}
		catch (ControlUnitAdminException e) {
			assertException("Retrieve a CU with type unknown, id unknown: ", e.getClass(), e);
		}

		try {
			cu = admin.getControlUnit("hip", "test");
			log("Retrieve a CU with type known, id unknown: OK");
			if (cu!= null) log ("CU found");
		}
		catch (ControlUnitAdminException e) {
			assertException("Retrieve a CU with type known, id unknown: ", e.getClass(), e);
		}
		
		try {
			cu = admin.getControlUnit("hip", "hip.id");
			log("Retrieve a CU with type = hip, id = hip.id: OK");
			if (cu!= null) log ("CU found");
		}
		catch (ControlUnitAdminException e) {
			assertException("Retrieve a CU with type = hip, id = hip.id: ", e.getClass(), e);
		}
		
		try {
			cu = admin.getControlUnit("door", "door.4");
			log("Retrieve a CU with type = door, id = door.4: OK");
			if (cu!= null) log ("CU found");
		}
		catch (ControlUnitAdminException e) {
			assertException("Retrieve a CU with type = door, id = door.4: ", e.getClass(), e);
		}
		
		// Retrieve Type Version of a CU Type
		try {
			admin.getControlUnitTypeVersion(null);
			log("Retrieve Type Version of a CU with type = null: OK");
		}
		catch (Exception e) {
			assertException("Retrieve Type Version of a CU with type = null: ", e.getClass(), e);
		}
		
		try {
			admin.getControlUnitTypeVersion("test");
			log("Retrieve Type Version of a CU with type unknown: OK");
		}
		catch (ControlUnitAdminException e) {
			assertException("Retrieve Type Version of a CU with type unknown: ", e.getClass(), e);
		}

		try {
			admin.getControlUnitTypeVersion("hip");
			log("Retrieve Type Version of a CU with type = hip: OK");
		}
		catch (ControlUnitAdminException e) {
			assertException("Retrieve Type Version of a CU with type = hip: ", e.getClass(), e);
		}		
	}
	
	/**
	 * Tests the invocation of actions using invokeAction method.
	 */
	public void testInvokeAction() {
		// Invoke an action with all arguments set to null
		try {
			admin.invokeAction(null, null, null, null);
			log("Invoke an action with all arguments set to null: OK");
		}
		catch (Exception e) {
			assertException("Invoke an action with all arguments set to null: ", e.getClass(), e);
		}
		
		// Invkoke an unknown action
		try {
			admin.invokeAction("hip.gyro", "hip.gyro.id", "test", null);
			log("Invoke an action with all arguments set to null: OK");
		}
		catch (ControlUnitException e) {
			assertException("Invoke an unknown action: ", e.getClass(), e);
		}
		
		// Invoke an action without arguments
		try {
			Measurement res;
			res = (Measurement)admin.invokeAction("hip.gyro", "hip.gyro.id", "hip.gyro.getZRO", null);
			log ("Invoke an action without arguments: OK, result = " + res);
		}
		catch (ControlUnitException e) {
			assertException("Invoke an action without arguments: ", e.getClass(), e);
		}
		
		// Invoke an action with wrong arguments
		try {
			Object[] args1 = {new Byte("1"), new Byte("3")};
			admin.invokeAction("hip.gyro", "hip.gyro.id", "hip.gyro.calibrate", args1);
			log("Invoke an action with wrong arguments: OK");
		}
		catch (ControlUnitException e) {
			assertException("Invoke an action with wrong arguments: ", e.getClass(), e);
		}
		
		// Invoke an action with some arguments
		try {
			Object[] args2 = {new Measurement(1, Unit.V), new Measurement(3, Unit.rad)};
			admin.invokeAction("hip.gyro", "hip.gyro.id", "hip.gyro.calibrate", args2);
			log ("Invoke an action with correct arguments: OK");
		}
		catch (ControlUnitException e) {
			assertException("Invoke an action with correct arguments: ", e.getClass(), e);
		}
	}
	
	/**
	 * Tests the query of state variables using queryStateVariable method.
	 */
	public void testQueryStateVariable() {
		// Query a state variable with all arguments set to null
		try {
			admin.queryStateVariable(null, null, null);
			log ("Query a state variable with all arguments set to null: OK");
		}
		catch (Exception e) {
			assertException("Query a state variable with all arguments set to null: ", e.getClass(), e);
		}
		
		// Query a state of an unknown variable
		try {
			admin.queryStateVariable("hip.gyro", "hip.gyro.id", "test");
			log ("Query an unknown state variable: OK");
		}
		catch (ControlUnitException e) {
			assertException("Query an unknown state variable: ", e.getClass(), e);
		}
		
		// Query a state variable
		try {
			Object res;
			res = admin.queryStateVariable("hip.gyro", "hip.gyro.id", "hip.gyro.rawOutput");
			log("Query a state variable: OK, result = " + res);
		}
		catch (ControlUnitException e) {
			assertException("Query a state variable: ", e.getClass(), e);
		}		
	}

	/**
	 * Tests access to control units
	 */
	public void testControlUnit() {
		ControlUnit cu = null;
		
		// Test access of a control unit
		try {
			cu = admin.getControlUnit("hip.tacho", "hip.tacho.id");
			log("Access a ControlUnit: OK");
		}
		catch (ControlUnitAdminException e) {
			e.printStackTrace();
		}
		
		if (cu != null) {
			log("type = " + cu.getType());
			log("id = " + cu.getId());
			try {
				Object res;
				res = cu.invokeAction("hip.tacho.isDppValid", null);
				log("Invoke an action : OK, result = " + res);
			}
			catch (ControlUnitException e) {
				assertException("Invoke an action: ", e.getClass(), e);
			} 
			
			try {
				Object res;
				res = cu.queryStateVariable("hip.tacho.rawOutput");
				log("Query a state variable value: OK, result = " + res);
			}
			catch (ControlUnitException e) {
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
		ControlUnit cu = null;
		try {
			cu = admin.getControlUnit("door", "door.4");
		}
		catch (ControlUnitAdminException e) {
			e.printStackTrace();
		}
		if (cu != null) {
			try {
				cu.invokeAction("door.open", null);
			}
			catch (ControlUnitException e) {
				assertException("Invoke an action: ", e.getClass(), e);
			} 
		}		
		
		// Tests the notification of state changes of a window
		try {
			cu = admin.getControlUnit("window", "window.1");
		}
		catch (ControlUnitAdminException e) {
			e.printStackTrace();
		}
		if (cu != null) {
			try {
				cu.invokeAction("window.open", null);
			}
			catch (ControlUnitException e) {
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
				admin.createControlUnit("window", "$create.window", null);
		}
		catch (ControlUnitException e) {
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
			if (tb1 != null)
				uninstallBundle(tb1);
			installBundle("tb1.jar");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (reg != null) {
			Hashtable props = new Hashtable();
			
			// Change Properties to add the link to HipModule
			log("Change properties to attach then detach hip.position to its parent hip");
			props.clear();
			props.put(ControlUnitConstants.TYPE, "hip.position");
			props.put(ControlUnitConstants.ID, "hip.position.id");
			props.put(ControlUnitConstants.PARENT_TYPE, "hip");
			props.put(ControlUnitConstants.PARENT_ID, "hip.id");
			reg.setProperties(props);
		
			// Change Properties to remove link to HipModule
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