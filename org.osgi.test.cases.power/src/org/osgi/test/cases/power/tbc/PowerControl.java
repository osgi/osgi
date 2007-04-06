/*
 * $Id$
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
import org.osgi.service.permissionadmin.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * A test case for the System Power service.
 */
public class PowerControl extends DefaultTestBundleControl implements
		SystemPowerStateListener, DevicePowerStateListener {
	PermissionAdmin		permissionAdmin;
	SystemPower			sysPower;
	DevicePower			devPower;
	ServiceRegistration	regSPSL;
	ServiceRegistration	regDPSL;

	PermissionInfo		allPermission	= new PermissionInfo(
												"java.security.AllPermission",
												"", "");

	/**
	 * List of test methods used in this test case.
	 */
	// static String[] methods = new String[] {"testLegalTransitions,
	// testIllegalTransitions"};
	/**
	 * Returns the list of test methods contained in this test case.
	 * 
	 * @return list of test methods
	 */
	// public String[] getMethods() {
	// return methods;
	// }
	/**
	 * Check the availability of the System Power service.
	 */
	public boolean checkPrerequisites() {
		try {
			installBundle("tb1.jar");
		}
		catch (Exception e) {
		}
		return serviceAvailable(SystemPower.class); // &&
													// serviceAvailable(DevicePower.class);
	}

	/**
	 * Get the SystemPower service
	 */
	public void prepare() {
		regSPSL = this.getContext().registerService(
				SystemPowerStateListener.class.getName(), this, null);
		try {
			sysPower = (SystemPower) getService(SystemPower.class);
		}
		catch (Exception e) {
			log("Cannot get System power service! " + e);
			e.printStackTrace();
		}
		regDPSL = this.getContext().registerService(
				DevicePowerStateListener.class.getName(), this, null);
		try {
			BundleContext bc = getContext();
			devPower = (DevicePower) bc.getService(bc
					.getServiceReference(DevicePower.class.getName()));
		}
		catch (Exception e) {
			log("Cannot get Device power service! " + e);
			e.printStackTrace();
		}
		try {
			/* Get the PermissionAdmin service */
			permissionAdmin = (PermissionAdmin) getService(PermissionAdmin.class);
		}
		catch (Exception e) {
			log("Cannot get PermissionAdmin service! " + e);
			e.printStackTrace();
		}
	}

	public void unprepare() {
		if (regSPSL != null) {
			regSPSL.unregister();
		}
		if (regDPSL != null) {
			regDPSL.unregister();
		}
	}

	public void testLegalTransitions() {
		int[][] legalTransitions = new int[][] {
				/* OFF */{SystemPowerState.PM_ACTIVE,
						SystemPowerState.FULL_POWER},
				/* SUSPEND */{SystemPowerState.PM_ACTIVE,
						SystemPowerState.FULL_POWER},
				/* SLEEP */{SystemPowerState.PM_ACTIVE,
						SystemPowerState.FULL_POWER, SystemPowerState.SUSPEND},
				/* PM_ACTIVE */{SystemPowerState.OFF, SystemPowerState.SUSPEND,
						SystemPowerState.SLEEP, SystemPowerState.FULL_POWER},
				/* FULL_POWER */{SystemPowerState.OFF, SystemPowerState.SUSPEND,
						SystemPowerState.SLEEP, SystemPowerState.PM_ACTIVE}};
		log("Current system power state: "
				+ getSystemPowerStateDescription(sysPower.getPowerState()));
		for (int i = 1; i < legalTransitions.length + 1; i++) {
			try {
				if (sysPower.getPowerState() != i) {
					log("Change to system power state: "
							+ getSystemPowerStateDescription(i));
					sysPower.setPowerState(i, true);
				}
				int[] legalStates = legalTransitions[i - 1];
				for (int j = 0; j < legalStates.length; j++) {
					if (sysPower.getPowerState() != legalStates[j]) {
						sysPower.setPowerState(legalStates[j], true);
						assertEquals(
								"System power state transition: from: "
										+ getSystemPowerStateDescription(i)
										+ " to: "
										+ getSystemPowerStateDescription(legalStates[j])
										+ ".", sysPower.getPowerState(),
								legalStates[j]);
						if (j < legalStates.length - 2) {
							sysPower.setPowerState(i, true);
						}
					}
				}
			}
			catch (PowerException pe) {
				assertException("Error code: "
						+ getErrorCodeDescription(pe.getErrorCode()),
						PowerException.class, pe);
			}
		}
	}

	public void testIllegalTransitions() {
		int[][] illegalTransitions = new int[][] {
		/* OFF */{SystemPowerState.SUSPEND, SystemPowerState.SLEEP},
		/* SUSPEND */{SystemPowerState.OFF, SystemPowerState.SLEEP},};
		log("Current system power state: "
				+ getSystemPowerStateDescription(sysPower.getPowerState()));
		for (int i = 1; i < illegalTransitions.length + 1; i++) {
			if (sysPower.getPowerState() != i) {
				try {
					sysPower.setPowerState(i, true);
				}
				catch (PowerException pe) {
					assertException(
							"Error while changing system power state: from "
									+ getSystemPowerStateDescription(sysPower
											.getPowerState()) + " to "
									+ getSystemPowerStateDescription(i) + ".",
							PowerException.class, pe);
				}
			}
			int[] illegalStates = illegalTransitions[i - 1];
			for (int j = 0; j < illegalStates.length; j++) {
				try {
					sysPower.setPowerState(illegalStates[j], true);
				}
				catch (PowerException pe) {
					assertException("Illegal transition '"
							+ getSystemPowerStateDescription(i) + " -> "
							+ getSystemPowerStateDescription(illegalStates[j])
							+ "' is managed correctly! ", PowerException.class,
							pe);
				}
				if (sysPower.getPowerState() == illegalStates[j]) {
					log(" !!! Illegal system power state transition: from: "
							+ getSystemPowerStateDescription(i) + " to: "
							+ getSystemPowerStateDescription(illegalStates[j])
							+ ".");
				}
			}
			try {
				sysPower.setPowerState(SystemPowerState.FULL_POWER, true);
			}
			catch (PowerException pe) {
				// do nothing
			}
		}
	}

	public void testPowerManagerRestrictions() {
		try {
			// power restriction
			sysPower.setPowerState(SystemPowerState.FULL_POWER, true);
			// TODO removed boolean, please check
			devPower.setPowerState(DevicePowerState.D1);
			assertEquals(
					"Error: The power restriction is that in Full Power state all devices must be in D0 device state!",
					DevicePowerState.D0, devPower.getPowerState());
			sysPower.setPowerState(SystemPowerState.OFF, true);
			// TODO removed boolean, please check
			devPower.setPowerState(DevicePowerState.D2);
			assertEquals(
					"Error: The power restriction is that in OFF Power state all devices must be in D3 device state!",
					DevicePowerState.D3, devPower.getPowerState());
			sysPower.setPowerState(SystemPowerState.FULL_POWER, true);
		}
		catch (PowerException pe) {
			pe.printStackTrace();
		}
	}

	public void testPermissions() {
		if (permissionAdmin != null) {
			if (sysPower != null) {
				String systemPowerLocation = ((getContext()
						.getServiceReference(SystemPower.class.getName()))
						.getBundle()).getLocation();
				PermissionInfo[] originalPermissions = permissionAdmin
						.getPermissions(systemPowerLocation);
				log("Set wrong permissions for System power!");
				PermissionInfo[] wrongPermissions = new PermissionInfo[] {
						new PermissionInfo(PowerPermission.class.getName(),
								"agaf*", ""),
						new PermissionInfo(PowerPermission.class.getName(),
								"systemdag", ""),};
				permissionAdmin.setPermissions(systemPowerLocation,
						wrongPermissions);
				try {
					sysPower.setPowerState(SystemPowerState.FULL_POWER, true);
					log("Error: Security check FAILED! System power state is changed with wrong permissions.");
				}
				catch (SecurityException se) {
					log("Security check PASSED!");
				}
				catch (PowerException pe) {
					pe.printStackTrace();
				}
				log("Set correct permissions for System power!");
				PermissionInfo[] correctPermissions = new PermissionInfo[] {
						new PermissionInfo(PowerPermission.class.getName(),
								"*", ""),
						new PermissionInfo(PowerPermission.class.getName(),
								"system", ""),};
				permissionAdmin.setPermissions(systemPowerLocation,
						correctPermissions);
				try {
					sysPower.setPowerState(SystemPowerState.FULL_POWER, true);
					log("Security check PASSED!");
				}
				catch (SecurityException se) {
					assertException(
							"Error: Security check FAILED! System power state can not changed with correct permissions.",
							SecurityException.class, se);
				}
				catch (PowerException pe) {
					pe.printStackTrace();
				}
				// restore original permissions
				permissionAdmin.setPermissions(systemPowerLocation,
						originalPermissions);
			}
			else {
				log("SystemPower service is not available!");
			}
			if (devPower != null) {
				String devicePowerLocation = ((getContext()
						.getServiceReference(DevicePower.class.getName()))
						.getBundle()).getLocation();
				PermissionInfo[] originalPermissions = permissionAdmin
						.getPermissions(devicePowerLocation);
				log("Set correct permissions for Device power!");
				PermissionInfo[] correctPermissions = new PermissionInfo[] {
						new PermissionInfo(PowerPermission.class.getName(),
								"*", ""),
						new PermissionInfo(PowerPermission.class.getName(),
								"<<ALL DEVICES>>", ""),};
				permissionAdmin.setPermissions(devicePowerLocation,
						correctPermissions);
				try {
					sysPower.setPowerState(DevicePowerState.D0, true);
					log("Security check PASSED!");
				}
				catch (SecurityException se) {
					assertException(
							"Error: Security check FAILED! Device power state can not changed with correct permissions.",
							SecurityException.class, se);
				}
				catch (PowerException pe) {
					pe.printStackTrace();
				}
				log("Set wrong permissions for Device power!");
				PermissionInfo[] wrongPermissions = new PermissionInfo[] {
						new PermissionInfo(PowerPermission.class.getName(),
								"*ljh", ""),
						new PermissionInfo(PowerPermission.class.getName(),
								"<<ALL DEVICES>>dag", ""),};
				permissionAdmin.setPermissions(devicePowerLocation,
						wrongPermissions);
				try {
					// TODO removed boolean, please check
					devPower.setPowerState(DevicePowerState.D0);
					log("Error: Security check FAILED! Device power state is changed with wrong permissions.");
				}
				catch (SecurityException se) {
					log("Security check PASSED!");
				}
				// restore original permissions
				permissionAdmin.setPermissions(devicePowerLocation,
						originalPermissions);
			}
			else {
				log("DevicePower service is not available!");
			}
		}
		else {
			log("PermissionAdmin service is not available!");
		}
	}

	public void systemPowerStateChange(PowerStateEvent event)
			throws PowerException {
		log("SPListener. New system power state: "
				+ getSystemPowerStateDescription(event.getNewState())
				+ " ; Previous system power state: "
				+ getSystemPowerStateDescription(event.getPreviousState())
				+ " ; Urgency: " + event.isUrgent());
	}

	public void devicePowerStateChange(PowerStateEvent event)
			throws PowerException {
		log("DPListener. New device power state: "
				+ getDevicePowerStateDescription(event.getNewState())
				+ " ; Previous device power state: "
				+ getDevicePowerStateDescription(event.getPreviousState())
				+ " ; Urgency: " + event.isUrgent());
	}

	private String getSystemPowerStateDescription(int state) {
		String desc = "Error. Wrong System Power State!";
		switch (state) {
			case SystemPowerState.OFF :
				desc = "OFF";
				break;
			case SystemPowerState.SUSPEND :
				desc = "SUSPEND";
				break;
			case SystemPowerState.SLEEP :
				desc = "SLEEP";
				break;
			case SystemPowerState.PM_ACTIVE :
				desc = "PM_ACTIVE";
				break;
			case SystemPowerState.FULL_POWER :
				desc = "FULL_POWER";
				break;
		}
		return desc;
	}

	private String getDevicePowerStateDescription(int state) {
		String desc = "Error. Wrong Device Power State!";
		switch (state) {
			case DevicePowerState.D0 :
				desc = "ON";
				break;
			case DevicePowerState.D1 :
				desc = "DEVICE SPECIFIC(D1)";
				break;
			case DevicePowerState.D2 :
				desc = "DEVICE SPECIFIC(D2)";
				break;
			case DevicePowerState.D3 :
				desc = "OFF";
				break;
		}
		return desc;
	}

	private String getErrorCodeDescription(int errCode) {
		String desc = "Error. Wrong error code!";
		switch (errCode) {
			case PowerException.ILLEGAL_STATE_TRANSITION_REQUEST :
				desc = "The power state change request is illegal.";
				break;
			case PowerException.KEEP_CURRENT_STATE :
				desc = "The power state change is opposed.";
				break;
			case PowerException.STATE_TRANSITION_FAILURE :
				desc = "The power state change failed.";
				break;
		}
		return desc;
	}

}