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
package org.osgi.test.cases.power.tbc;

import org.osgi.framework.*;
import org.osgi.service.power.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * A test case for the System Power service.
 */
public class PowerControl extends DefaultTestBundleControl implements SystemPowerStateListener {
	SystemPower sysPower;
	ServiceRegistration reg;
	
	/**
	 * List of test methods used in this test case.
	 */
//	static String[]	methods	= new String[] {"testLegalTransitions, testIllegalTransitions"};

	/**
	 * Returns the list of test methods contained in this test case.
	 * 
	 * @return list of test methods
	 */
//	public String[] getMethods() {
//		return methods;
//	}

	/**
	 * Check the availability of the System Power service.
	 */	
	public boolean checkPrerequisites() {
	  return serviceAvailable(SystemPower.class);
	}

	/**
	 * Get the SystemPower service
	 */
	public void prepare() {
	  reg = this.getContext().registerService(SystemPowerStateListener.class.getName(), this, null);
	  try {
	    sysPower = (SystemPower) getService(SystemPower.class);
	  } catch( Exception e ) {
			log( "Cannot start System power service test case " + e );
			e.printStackTrace();
		}
	}
	
	public void unprepare() {
	  if (reg != null) {
	    reg.unregister();
	  }
	}
	
	public void testLegalTransitions() {
	  int[][] legalTransitions = new int[][] {
	      /*OFF*/{ SystemPowerState.PM_ACTIVE, SystemPowerState.FULL_POWER },
	      /*SUSPEND*/{ SystemPowerState.PM_ACTIVE, SystemPowerState.FULL_POWER },
	      /*SLEEP*/{ SystemPowerState.OFF, SystemPowerState.SUSPEND, SystemPowerState.SLEEP, SystemPowerState.PM_ACTIVE, SystemPowerState.FULL_POWER},
	      /*PM_ACTIVE*/{ SystemPowerState.OFF, SystemPowerState.SUSPEND, SystemPowerState.SLEEP, SystemPowerState.PM_ACTIVE, SystemPowerState.FULL_POWER},
	      /*FULL_POWER*/{ SystemPowerState.OFF, SystemPowerState.SUSPEND, SystemPowerState.SLEEP, SystemPowerState.PM_ACTIVE, SystemPowerState.FULL_POWER},
	  };
		log("Current system power state: " + getPowerStateDescription(sysPower.getPowerState()));
		for (int i = 1; i < legalTransitions.length + 1; i++) {
			try {
			  if (sysPower.getPowerState() != i) {
					log("Change to system power state: " + getPowerStateDescription(i));
			    sysPower.setPowerState(i, true);
			  }
			  int[] legalStates = legalTransitions[i - 1];
			  for (int j = 0; j < legalStates.length; j++) {
				  if (sysPower.getPowerState() != legalStates[j]) {
					  sysPower.setPowerState(legalStates[j], true);
					  assertEquals(" System power state transition: from: " + getPowerStateDescription(i) + " to: " + getPowerStateDescription(legalStates[j]) + ".", sysPower.getPowerState(), legalStates[j]);
					  sysPower.setPowerState(i, true);
				  }
			  }
			}	catch (PowerException pe) {
				log("PowerException: Error code: " + getErrorCodeDescription(pe.getErrorCode()));
			  pe.printStackTrace();
				assertException("Exception while changing system power state", PowerException.class, pe);
			}
		}
	}

	public void testIllegalTransitions() {
	  int[][] illegalTransitions = new int[][] {
	      /*OFF*/{SystemPowerState.SUSPEND, SystemPowerState.SLEEP},
	      /*SUSPEND*/{SystemPowerState.OFF, SystemPowerState.SLEEP},
	      /*SLEEP*/{},
	      /*PM_ACTIVE*/{},
	      /*FULL_POWER*/{},
	  };
		log("Current system power state: " + getPowerStateDescription(sysPower.getPowerState()));
		for (int i = 1; i < illegalTransitions.length + 1; i++) {
			try {
			  if (sysPower.getPowerState() != i) {
					log("Change to system power state: " + getPowerStateDescription(i));
			    sysPower.setPowerState(i, true);
			  }
			  int[] illegalStates = illegalTransitions[i - 1];
			  for (int j = 0; j < illegalStates.length; j++) {
				  sysPower.setPowerState(illegalStates[j], true);
				  if (sysPower.getPowerState() == illegalStates[j]) {
						log(" !!! Illegal system power state transition: from: " + getPowerStateDescription(i) + " to: " + getPowerStateDescription(illegalStates[j]) + ".");
				  }
				  sysPower.setPowerState(i, true);
			  }
			}	catch (PowerException pe) {
				log("PowerException: Error code: " + getErrorCodeDescription(pe.getErrorCode()));
			  pe.printStackTrace();
				assertException("Exception while changing system power state", PowerException.class, pe);
			}
		}
	}
	
  public void systemPowerStateChange(PowerStateEvent event) throws PowerException {
		log("SPListener. New system power state: " + getPowerStateDescription(event.getNewState()) + " ; Previous system power state: " + getPowerStateDescription(event.getPreviousState()) + " ; Urgency: " + event.isUrgent());
  }	
  
  private String getPowerStateDescription(int state) {
    String desc = "Error. Wrong Power State!";
    switch (state) {
      case SystemPowerState.OFF : desc = "OFF"; break;
      case SystemPowerState.SUSPEND : desc = "SUSPEND"; break;
      case SystemPowerState.SLEEP : desc = "SLEEP"; break;
      case SystemPowerState.PM_ACTIVE : desc = "PM_ACTIVE"; break;
      case SystemPowerState.FULL_POWER : desc = "FULL_POWER"; break;
    }
    return desc;
  }
  
  private String getErrorCodeDescription(int errCode) {
    String desc = "Error. Wrong error code!";
    switch (errCode) {
      case PowerException.ILLEGAL_STATE_TRANSITION_REQUEST : desc = "The power state change request is illegal."; break;
      case PowerException.KEEP_CURRENT_STATE : desc = "The power state change is opposed."; break;
      case PowerException.STATE_TRANSITION_FAILURE : desc = "The power state change failed."; break;
    }
    return desc;
  }
  
}