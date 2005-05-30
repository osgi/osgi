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
package org.osgi.test.cases.diagnostics.tbc;

import org.osgi.framework.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;
import org.osgi.service.cu.admin.*;
import org.osgi.service.cu.diag.DiagnosableControlUnit;
import org.osgi.service.cu.diag.Status;
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
	
	/**
	 * List of test methods used in this test case.
	 */
	static String[]	methods	= new String[] {"testCreationOfDiagCUs",
											                    "testDestructionOfDiagCUs",
											                    "testDiagControlUnit",
											                    "testFindDiagnostableCUs",
											                    "testListDiagCUs",
											                    "testQueryStateVariableDCU",
											                    "testInvokeActionDCU"};

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
		if (ref == null) {
			return false;
    }
		admin = (ControlUnitAdmin) getContext().getService(ref);
		if (admin == null) {
			return false;
    }
		return true;
	}
		
	/**
	 * Test the creation of diagnosable control units using create methods
	 */
	public void testCreationOfDiagCUs() {
		// Test creation with unknow control unit type
		try {
			admin.createControlUnit("test", null, null);
			log("#Succeed: Creation with an unknown DiagnosableControlUnit");
		} catch (Exception e) {
      assertException("Creation with an unknown DiagnosableControlUnit:", ControlUnitException.class, e);
		}
		
		// Test creation with a DiagnosableControlUnit service
		try {
			admin.createControlUnit("tachometer", "$create.tachometer", null);
			log("#Succeed: Creation with a DiagnosableControlUnit");
		} catch (Exception e) {
			assertException("Creation with a DiagnosableControlUnit:", ControlUnitException.class, e);
		}
		
		// Test creation with a DiagnosableControlUnit service
		try {
			admin.createControlUnit("door", null, null);
			log("#Succeed: Creation from a CUFactory with other arguments set to null");
		} catch (ControlUnitException e) {
			assertException("Creation from a CUFactory with other arguments set to null", e.getClass(), e);
		}
		
		// Test creation with a DiagnosableControlUnit service
		try {
			admin.createControlUnit("door", "create.unknown", null);
			fail("Succeed: Creation of DiagnosableControlUnit from a CUFactory with an unknown method");
		} catch (Exception e) {
			assertException("Creation a DiagnosableControlUnit from a CUFactory with an unknown method", 
                      ControlUnitException.class, e);
		}
		
		// Test creation with a DiagnosableControlUnit service
		try {
			admin.createControlUnit("door", "$create.door", "wrong_arg");
			fail("Succeed: Creation of DiagnosableControlUnit from a CUFactory with wrong arguments");
		} catch (Exception e) {
			assertException("Creation a DiagnosableControlUnit from a CUFactory with wrong arguments", 
                      ControlUnitException.class, e);
		}
		
		// Test creation with a DiagnosableControlUnit service
		try {
			admin.createControlUnit("door", "$create.door", null);
			log("#Succeed: Creation from a CUFactory with correct arguments");
		} catch (Exception e) {
			assertException("Creation a DiagnosableControlUnit from a CUFactory with correct arguments", 
                      ControlUnitException.class, e);
		}
	}
	
	/**
	 * Test destruction of diagnostable control units using destroy methods.
	 */
	public void testDestructionOfDiagCUs() {		
		// Test destruction with unknow control unit
		try {
			admin.destroyControlUnit("unknown", "");
			log("#Succeed: Destruction of an unknown CU");
		} catch (Exception e) {
			assertException("Destruction of an unknown CU:", e.getClass(), e);
		}
		
		// Test destruction of a CUFactory with unknown id
		try {
			admin.destroyControlUnit("door", "door.unknown");
			fail("Succeed: Destruction from a CUFactory with type = door, id = door.unknown");
		} catch (Exception e) {
			assertException("Destruction from a CUFactory with type = door, id = door.unknown:", 
                      ControlUnitException.class, e);
		}
		
		// Test destruction of a created CU
		try {
			admin.destroyControlUnit("door", "door.1");
			log("#Succeed: Destruction of CU with type = door, id = door.1");
		} catch (Exception e) {
			assertException("Destruction of CU with type = door, id = door.1", 
                      ControlUnitException.class, e);
		}		
	}
  
  /**
   * Tests access to a control unit, invoke action, query a state variable and check status.
   */
  public void testDiagControlUnit() {
    DiagnosableControlUnit dcu = null;    
    // Test access of a control unit
    try {
      //it's created above and not destroyed yet
      dcu = (DiagnosableControlUnit) admin.getControlUnit("tachometer", "tachometer.id");
      log("#Succed: Get a DiagnosableControlUnit with type 'tachometer': result = " + dcu);
    } catch (Exception e) {
      assertException("Invoke an action: ", ControlUnitAdminException.class, e);
    }
    
    if (dcu != null) {
      log("#type = " + dcu.getType());
      log("#id = " + dcu.getId());
      Status status;
      try {
        status = dcu.checkStatus();
        logStatus(status);
        assertEquals("Result of checking status: ", Status.STATUS_FAILED, status.getStatus());
      } catch (Exception e) {
        assertException("Check status: ", ControlUnitException.class, e);
      }
      
      //Invoke an action with correct arguments - calibrated but dpp is not valid
      try {
        admin.invokeAction("tachometer", "tachometer.id", "tachometer.calibrate", new Byte("-60"));
        log ("Succed: Invoke an action with correct arguments, set dpp '-60'");
        try {
          status = dcu.checkStatus();
          logStatus(status);
          assertEquals("Result of checking status: ", Status.STATUS_FAILED, status.getStatus());
        } catch (Exception cue) {
          assertException("Check status: ", ControlUnitException.class, cue);
        }
      } catch (Exception e) {
        assertException("Invoke an action with correct arguments: ", ControlUnitException.class, e);
      }
      
      //Invoke an action with correct arguments - set correct dpp
      try {
        admin.invokeAction("tachometer", "tachometer.id", "tachometer.calibrate", new Byte("60"));
        log ("Succed: Invoke an action with correct arguments, set dpp '60'");
        //query currecnt dpp
        try {
          Object dpp = dcu.queryStateVariable("tachometer.dpp");
          log("#Succed: Query a state variable 'dpp', value = " + dpp);
        } catch (Exception cue) {
          assertException("Query a state variable value: ", ControlUnitException.class, cue);
        } 
        try {
          status = dcu.checkStatus();
          logStatus(status);
          assertEquals("Result of checking status: ", Status.STATUS_OK, status.getStatus());
        } catch (Exception cue) {
          assertException("Check status: ", ControlUnitException.class, cue);
        }
      } catch (Exception e) {
        assertException("Invoke an action with correct arguments: ", ControlUnitException.class, e);
      }
    }
  }

	/**
	 * Test search of diagnostable control units using find methods.
	 */
	public void testFindDiagnostableCUs() {
		String[] types;
		
		// Creation of all diagnosable CUs for test needs
		try {
  		for (int i=0; i<4; i++) {
  			admin.createControlUnit("door", "$create.door", null);
      }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Search of diagnostable CUs
		try {
			types = admin.findControlUnits("door", "$find.all", null);
			log("#Succeed: Find CUs with a correct find method, type = door");
			if (types != null) logArray(types);
		} catch (ControlUnitException e) {
			assertException("Find CUs with a correct find method, type = door: ", e.getClass(), e);
		}
    
    try {
      types = admin.findControlUnits("tachometer", "$find.all", null);
      log("#Succeed: Find CUs with a correct find method, type = tachometer");
      if (types != null) logArray(types);
    } catch (ControlUnitException e) {
      assertException("Find CUs with a correct find method, type = tachometer", e.getClass(), e);
    }
	}
	
	/**
	 * Test get of control units.
	 */
	public void testListDiagCUs() {
		String[] types;		
		// List all CU Types
		types = admin.getControlUnitTypes();
		log("#Succeed: List All CU Types");		
		if (types != null) logArray(types);
		
		// Retrieve a CU from its type and Id
		DiagnosableControlUnit cu;
		try {
			cu = (DiagnosableControlUnit) admin.getControlUnit("test", "test");
			log("#Succeed: Get a DCU with unknown type and unknown id");
      assertNull("DCU NOT found", cu);
		} catch (Exception e) {
			assertException("Retrieve a CU with type unknown, id unknown: ", 
                      ControlUnitAdminException.class, e);
		}
				
		try {
			cu = (DiagnosableControlUnit) admin.getControlUnit("door", "door.4");
			log("#Succeed: Get a DCU with type = door, id = door.4");
      assertNotNull("DCU found", cu);
      if (cu != null) {
        logStatus(cu.checkStatus());
      }
		} catch (Exception e) {
			assertException("Get a CU with type = door, id = door.4: ", 
                       ControlUnitAdminException.class, e);
		}
		
		try {
			String typeVer = admin.getControlUnitTypeVersion("test");
			log("#Succed: Get Type Version of a CU with type unknown: found = " + typeVer);
		} catch (Exception e) {
			assertException("Retrieve Type Version of a CU with type unknown: ", 
                      ControlUnitAdminException.class, e);
		}

		try {
      String typeVer = admin.getControlUnitTypeVersion("tachometer");
			log("#Succed: Get Type Version of a CU with type = tachometer: found = " + typeVer);
		} catch (Exception e) {
			assertException("Retrieve Type Version of a CU with type = tachometer: ", 
                      ControlUnitAdminException.class, e);
		}		
	}
  

  /**
   * Tests the query of state variables of Diagnosable CU using queryStateVariable method.
   */
  public void testQueryStateVariableDCU() {
    // Query a state variable with all arguments set to null
    try {
      admin.queryStateVariable(null, null, null);
      log ("Succeed: Query a state variable with all arguments set to null");
    } catch (Exception e) {
      assertException("Query a state variable with all arguments set to null: ", e.getClass(), e);
    }
        
    //Query a state variables
    try {
      Object var = admin.queryStateVariable("tachometer", "tachometer.id", "tachometer.dpp");
      log("#Succeed: Query a state variable 'dpp' from 'tachometer' CU: result = " + var);
    } catch (Exception e) {
      assertException("Query a state variable: ", ControlUnitException.class, e);
    }
    
    try {
      Object var = admin.queryStateVariable("tachometer", "tachometer.id", "tachometer.rawOutput");
      log("#Succeed: Query a state variable 'rawOutput' from 'tachometer' CU: result = " + var);
    } catch (Exception e) {
      assertException("Query a state variable: ", ControlUnitException.class, e);
    }
  }
	
	/**
	 * Tests the invocation of actions of Diagnosable CU using invokeAction method.
	 */
	public void testInvokeActionDCU() {
		// Invkoke an unknown action
		try {
			admin.invokeAction("tachometer", "tachometer.id", "test", null);
			fail("Succeed: Invoke an action with all arguments set to null");
		} catch (Exception e) {
			assertException("Invoke an unknown action: ", ControlUnitException.class, e);
		}
		
		// Invoke an action with wrong arguments
		try {
			Object[] args1 = {new Byte("1"), new Byte("3")};
			admin.invokeAction("tachometer", "tachometer.id", "tachometer.calibrate", args1);
			fail("Succeed: Invoke an action with wrong arguments");
		}	catch (Exception e) {
			assertException("Invoke an action with wrong arguments: ", ControlUnitException.class, e);
		}
		
		// Invoke an action with correct arguments
		try {
			admin.invokeAction("tachometer", "tachometer.id", "tachometer.calibrate", new Byte("40"));
			log ("Succeed: Invoke an action with correct arguments");
		} catch (Exception e) {
			assertException("Invoke an action with correct arguments: ", ControlUnitException.class, e);
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
		try {
			installBundle("tb1.jar");
			installBundle("tb2.jar");
		} catch (Exception e) {
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
		} catch (Exception e) {
		}
	}

	private void logArray(String[] array) {
		if (array != null) {
			for (int i=0; i<array.length; i++) {
				log(array[i]);
      }
    }
	}
  
  public void logStatus(Status status) {
    if (status == null) {
      fail("Check status performed: result = null");  
    } else if (status.getStatus() == Status.STATUS_OK) {
      log("#Check status successfully performed. No error found.");  
    } else {
      log("#Error found while checking status: " + status.getMessage()); 
    }
  }
}