/*
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 05/04/2005   Alexandre Santos
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ScheduledApplication;
import org.osgi.service.application.meglet.MegletDescriptor;
import org.osgi.service.application.meglet.MegletHandle;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.Event.EventHandlerActivator;
import org.osgi.test.cases.application.tbc.ApplicationAdminPermission.ApplicationAdminPermissionConstants;
import org.osgi.test.cases.application.tbc.ApplicationDescriptor.ApplicationDescriptorConstants;
import org.osgi.test.cases.application.tbc.ApplicationHandle.ApplicationHandleConstants;
import org.osgi.test.cases.application.tbc.TreeStructure.TreeStructure;
import org.osgi.test.cases.application.tbc.UseCases.LifecycleStates;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * Controls the execution of the test case
 */
public class ApplicationTestControl extends DefaultTestBundleControl {

	public static final String ACTIONS_INVALID_FORMAT = ApplicationAdminPermission.LIFECYCLE + "&"
			+ ApplicationAdminPermission.LOCK + "&"
			+ ApplicationAdminPermission.SCHEDULE;

	public static final String ACTIONS1 = ApplicationAdminPermission.LIFECYCLE
			+ "," + ApplicationAdminPermission.LOCK;
	
	public static final String TIMER_EVENT_TOPIC = "org/osgi/timer";
	
	public static final String TEST1_PID = "Meglet-Sample-Version-1.0";

	public static final String TEST1_NAME_EN = "MEG Test Application";

	public static final String TEST1_NAME_BR = "MEG Test Application";

	public static final String TEST1_ICON_EN = "/TestIcon.gif";

	public static final String TEST1_ICON_BR = "/TestIcon.gif";

	public static final String TEST1_VERSION = "1.0";

	public static final String TEST1_VENDOR = "Cesar";

	public static final Boolean TEST1_SINGLETON = new Boolean(true);

	public static final Boolean TEST1_AUTOSTART = new Boolean(true);

	public static final Boolean TEST1_VISIBLE = new Boolean(true);

	public static final Boolean TEST1_LAUNCHABLE = new Boolean(true);

	public static final Boolean TEST1_LOCKED = new Boolean(false);

	public static final String TEST2_PID = "Meglet-Sample-Version-2.0";

	public static final String TEST2_NAME_EN = "MEG Test Application";

	public static final String TEST2_NAME_BR = "MEG Test Application";

	public static final String TEST2_ICON_EN = "/TestIcon.gif";

	public static final String TEST2_ICON_BR = "/TestIcon.gif";

	public static final String TEST2_VERSION = "2.0";

	public static final String TEST2_VENDOR = "Cesar";

	public static final Boolean TEST2_SINGLETON = new Boolean(false);

	public static final Boolean TEST2_AUTOSTART = new Boolean(true);

	public static final Boolean TEST2_VISIBLE = new Boolean(true);

	public static final Boolean TEST2_LAUNCHABLE = new Boolean(true);

	public static final Boolean TEST2_LOCKED = new Boolean(true);

	public static Map meg1Properties = new HashMap();

	public static Map meg2Properties = new HashMap();
	
	public static Map meg2PropertiesTest = new HashMap();
	
	public static final int TIME_OUT = 5000;

	public static final String OSGI_ROOT_URI = "OSGi";
	public static final String APPS_URI = OSGI_ROOT_URI+"/apps";
	public static final String APPS_INSTANCE_URI = OSGI_ROOT_URI+"/app_instances";
	public static final String APPS_INSTANCE_STATE = "/state";
	public static final String APPS_INSTANCE_TYPE = "/type";
	public static final String APPS_APP_NAME = APPS_URI+"/"+TEST1_PID+"/localizedname";
	public static final String APPS_APP_VERSION = APPS_URI+"/"+TEST1_PID+"/version";
	public static final String APPS_APP_VENDOR = APPS_URI+"/"+TEST1_PID+"/vendor";
	public static final String APPS_APP_AUTOSTART = APPS_URI+"/"+TEST1_PID+"/autostart";
	public static final String APPS_APP_LOCKED = APPS_URI+"/"+TEST1_PID+"/locked";
	public static final String APPS_APP_SINGLETON = APPS_URI+"/"+TEST1_PID+"/singleton";
	public static final String APPS_APP_BUNDLE_ID = APPS_URI+"/"+TEST1_PID+"/bundle_id";

	
	private String tb2Location;

	private TestInterface[] testInterfaces;

	private PermissionAdmin permissionAdmin;

	private MegletDescriptor appDescriptor1;

	private MegletDescriptor appDescriptor2;

	private MegletHandle appHandle;
	
	public static final String STARTED = "STARTED"; 
	public static final String DESTROYED = "DESTROYED";
	public static final String LOCKED = "LOCKED"; 
	public static final String UNLOCKED = "UNLOCKED";
	
	public static final String MANIPULATE = "manipulate";
	
	private String state;
	
	private boolean resultState;
	
	private boolean available; 
	
	private boolean availableResultState;
	
	private Bundle bundleMeglet;
	
	private boolean isRegistered = false;
	private boolean isModified = false;
	private boolean isUnregistered = false;
	private boolean isOurMegletActivated = false;
	
	private DmtAdmin dmtAdmin;
	
	private MegletInterface megletInterface;
	
	public synchronized void prepare() {
		try {
			permissionAdmin = (PermissionAdmin) getContext().getService(
					getContext().getServiceReference(
							PermissionAdmin.class.getName()));

			// installing activator
			EventHandlerActivator tb3 = new EventHandlerActivator(this);
			tb3.start(getContext());			
			
			installBundle("tb2.jar");
		
			bundleMeglet = installBundle("tb1.jar");
			
			wait(TIME_OUT);							
										
			ServiceReference[] appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor",
					"(service.pid=" + TEST1_PID + ")");

			appDescriptor1 = (MegletDescriptor) getContext().getService(
					appDescRefs[0]);
			
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor",
					"(service.pid=" + TEST2_PID + ")");

			appDescriptor2 = (MegletDescriptor) getContext().getService(
					appDescRefs[0]);
			
			appDescRefs = getContext()
					.getServiceReferences(
							"org.osgi.service.application.ApplicationHandle",
							"(application.descriptor="
									+ TEST1_PID + ")");

			appHandle = (MegletHandle) getContext().getService(appDescRefs[0]);

			ServiceReference srvReference = getContext().getServiceReference(
					TB2Service.class.getName());
			tb2Location = srvReference.getBundle().getLocation();
			TB2Service tcb1Service = (TB2Service) getContext().getService(
					srvReference);
			testInterfaces = tcb1Service.getTestClasses(this);
			
			initializeMegProperties();
			
			dmtAdmin = (DmtAdmin) getContext().getService(getContext().getServiceReference(DmtAdmin.class.getName()));
			
		} catch (Exception e) {
			this.log("Unexpected exception at prepare.");
		}
	}

	private void initializeMegProperties() {
		meg1Properties.put(ApplicationDescriptor.APPLICATION_NAME,
				TEST1_NAME_EN);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_ICON,
				TEST1_ICON_EN);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_PID,
				TEST1_PID);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_VERSION,
				TEST1_VERSION);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_VENDOR,
				TEST1_VENDOR);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_SINGLETON,
				TEST1_SINGLETON);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_AUTOSTART,
				TEST1_AUTOSTART);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_VISIBLE,
				TEST1_VISIBLE);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_LAUNCHABLE,
				TEST1_LAUNCHABLE);
		meg1Properties.put(ApplicationDescriptor.APPLICATION_LOCKED,
				TEST1_LOCKED);

		meg2Properties.put(ApplicationDescriptor.APPLICATION_NAME,
				TEST2_NAME_EN);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_ICON,
				TEST2_ICON_EN);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_PID,
				TEST2_PID);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_VERSION,
				TEST2_VERSION);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_VENDOR,
				TEST2_VENDOR);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_SINGLETON,
				TEST2_SINGLETON);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_AUTOSTART,
				TEST2_AUTOSTART);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_VISIBLE,
				TEST2_VISIBLE);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_LAUNCHABLE,
				TEST2_LAUNCHABLE);
		meg2Properties.put(ApplicationDescriptor.APPLICATION_LOCKED,
				TEST2_LOCKED);
		
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_NAME,
				TEST1_NAME_EN);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_ICON,
				TEST1_ICON_EN);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_PID,
				TEST1_PID);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_VERSION,
				TEST1_VERSION);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_VENDOR,
				TEST1_VENDOR);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_SINGLETON,
				TEST1_SINGLETON);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_AUTOSTART,
				TEST1_AUTOSTART);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_VISIBLE,
				TEST1_VISIBLE);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_LAUNCHABLE,
				TEST1_LAUNCHABLE);
		meg2PropertiesTest.put(ApplicationDescriptor.APPLICATION_LOCKED,
				TEST1_LOCKED);
		meg2PropertiesTest.put("org.osgi.triggeringevent", "org/osgi/triggeringevent/OURMEGLET");
	}
	/**
	 * Verify if the handle was stopped.
	 */
	public boolean isHandleStopped() {
		try {
			ServiceReference[] appDescRefs = getContext()
			.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor="
							+ TEST1_PID + ")");
			if (appDescRefs == null) {
				return true;
			} else {
				return false;
			}
		} catch (InvalidSyntaxException e) {
			return false;
		}
		
		
	}

	public void setLocalPermission(PermissionInfo permission) {
		PermissionInfo[] defaults = new PermissionInfo[] {
				new PermissionInfo(SocketPermission.class.getName(), "*.br", "accept,connect,listen,resolve"),
				new PermissionInfo(ReflectPermission.class.getName(), "*", "*"),
				new PermissionInfo(RuntimePermission.class.getName(), "*", "*"),
				new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", 
                "read, write, execute, delete"),				
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(PackagePermission.class.getName(), "*",
						"EXPORT, IMPORT"),
				new PermissionInfo(ServicePermission.class.getName(), "*",
						"GET,REGISTER"), permission };
		permissionAdmin.setPermissions(getTb2Location(), defaults);
	}
	
	public void setDefaultPermission() {
		PermissionInfo[] defaults = new PermissionInfo[] {
				new PermissionInfo(SocketPermission.class.getName(), "*.br", "accept,connect,listen,resolve"),
				new PermissionInfo(ReflectPermission.class.getName(), "*", "*"),
				new PermissionInfo(RuntimePermission.class.getName(), "*", "*"),
				new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", 
                "read, write, execute, delete"),	
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(PackagePermission.class.getName(), "*",
						"EXPORT, IMPORT"),
				new PermissionInfo(ServicePermission.class.getName(), "*",
						"GET,REGISTER") };
		permissionAdmin.setPermissions(getTb2Location(), defaults);
	}

	public void setLocalPermission(PermissionInfo[] permissions) {
		PermissionInfo[] defaults = new PermissionInfo[] {
				new PermissionInfo(SocketPermission.class.getName(), "*.br", "accept,connect,listen,resolve"),
				new PermissionInfo(ReflectPermission.class.getName(), "*", "*"),
				new PermissionInfo(RuntimePermission.class.getName(), "*", "*"),
				new PermissionInfo(FilePermission.class.getName(), "<<ALL FILES>>", 
                "read, write, execute, delete"),				
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				new PermissionInfo(PackagePermission.class.getName(), "*",
						"EXPORT, IMPORT"),
				new PermissionInfo(ServicePermission.class.getName(), "*",
						"GET,REGISTER") };
		int size = permissions.length + defaults.length;

		PermissionInfo[] permissao = new PermissionInfo[size];
		System.arraycopy(defaults, 0, permissao, 0, defaults.length);
		System.arraycopy(permissions, 0, permissao, defaults.length,
				permissions.length);

		permissionAdmin.setPermissions(getTb2Location(), permissao);
	}

	public String getTb2Location() {
		return tb2Location;
	}

	/**
	 * Executes Tcs for constants
	 * 
	 */
	public void testApplicationAdminPermissionConstants() throws Exception {
		new ApplicationAdminPermissionConstants(this).run();
	}

	/**
	 * Executes Tcs for ApplicationAdminPermission
	 */
	public void testApplicationAdminPermission() {
		new org.osgi.test.cases.application.tbc.ApplicationAdminPermission.ApplicationAdminPermission(
				this).run();
	}

	/**
	 * Executes Tcs for SingletonException
	 */
	public void testSingletonException() {
		new org.osgi.test.cases.application.tbc.SingletonException.SingletonException(
				this).run();
	}

	public void testApplicationDescriptorLock() {
		testInterfaces[0].run();
	}

	public void testApplicationDescriptorUnLock() {
		testInterfaces[1].run();
	}

	/**
	 * Executes Tcs for MegletHandle#GetApplicationDescriptor
	 */
	public void testMegletHandleGetApplicationDescriptor() {
		testInterfaces[2].run();
	}

	/**
	 * Executes Tcs for MegletHandle#GetState
	 */
	public void testMegletHandleGetInstanceID() {
		testInterfaces[3].run();
	}

	/**
	 * Executes Tcs for MegletHandle#GetState
	 */
	public void testMegletHandleGetState() {
		testInterfaces[4].run();
	}

	/**
	 * Executes Tcs for MegletHandle#Suspend
	 */
	public void testMegletHandleSuspend() {
		testInterfaces[5].run();
	}

	/**
	 * Executes Tcs for MegletDescriptor#GetProperties
	 */
	public void testMegletDescriptorGetProperties() {
		testInterfaces[6].run();
	}

	/**
	 * Executes Tcs for MegletDescriptor#Launch
	 */
	public void testMegletDescriptorLaunch() {
		testInterfaces[7].run();
	}
	
	/**
	 * Executes Tcs for MegletDescriptor#schedule
	 */
	public void testMegletDescriptorSchedule() {
		testInterfaces[10].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#getTopic
	 */
	public void testScheduledApplicationGetTopic() {
		testInterfaces[11].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#getEventFilter
	 */
	public void testScheduledApplicationGetEventFilter() {
		testInterfaces[12].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#isRecurring
	 */
	public void testScheduledApplicationIsRecurring() {
		testInterfaces[13].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#getArguments
	 */
	public void testScheduledApplicationGetArguments() {
		testInterfaces[14].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#getApplicationDescriptor
	 */
	public void testScheduledApplicationGetApplicationDescriptor() {
		testInterfaces[15].run();
	}

	/**
	 * Executes Tcs for ScheduledApplication#remove
	 */
	public void testScheduledApplicationRemove() {
		testInterfaces[16].run();
	}

	/**
	 * Executes Tcs for MegletHandle#Destroy
	 */
	public void testMegletHandleDestroy() {
		testInterfaces[9].run();
	}
	
	/**
	 * Executes Tcs for MegletHandle#Resume
	 */
	public void testMegletHandleResume() {
		testInterfaces[8].run();
	}		

	/**
	 * Executes Tcs for constants
	 */
	public void testApplicationDescriptorConstants() {
		new ApplicationDescriptorConstants(this).run();
	}
	
	/**
	 * Executes use cases
	 */
	public void testLifecycleStates() {
		new LifecycleStates(this).run();
	}
	
	/**
	 * Executes tree structure tests
	 */
	public void testTreeStructure() {
		new TreeStructure(this).run();
	}	

	/**
	 * Executes tests of meglets protected methods
	 */
	public void testMeglets() {
		//new Meglet(this).run();
	}	
	
	/**
	 * Executes Tcs for constants
	 */
	public void testApplicationHandleConstants() {
		new ApplicationHandleConstants(this).run();
	}

	public PermissionAdmin getPermissionAdmin() {
		return permissionAdmin;
	}

	/**
	 * @return Returns the appDescriptor.
	 */
	public MegletDescriptor getAppDescriptor1() {
		return appDescriptor1;
	}

	/**
	 * @return Returns the appDescriptor.
	 */
	public MegletDescriptor getAppDescriptor2() {
		return appDescriptor2;
	}
	
	public Map getMeg1Properties(){
		return meg1Properties;
	}

	public Map getMeg2Properties(){
		return meg2Properties;
	}
	
	public Map getMeg2PropertiesTest(){
		return meg2PropertiesTest;
	}	

	/**
	 * @return Returns the appHandle.
	 */
	public MegletHandle getAppHandle() {
		return appHandle;
	}
	/**
	 * @return Returns the bundleMeglet.
	 */
	public Bundle getBundleMeglet() {
		return bundleMeglet;
	}
	
	public synchronized void installBundleMeglet() {
		ServiceReference[] appDescRefs;
		try {
			bundleMeglet = installBundle("tb1.jar");
			
			wait(TIME_OUT);
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor",
					"(service.pid=" + TEST1_PID + ")");
			
			appDescriptor1 = (MegletDescriptor) getContext().getService(
					appDescRefs[0]);
					
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationDescriptor",
					"(service.pid=" + TEST2_PID + ")");
			
			appDescriptor2 = (MegletDescriptor) getContext().getService(
					appDescRefs[0]);	
				
			appDescRefs = getContext()
			.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor="
					+ TEST1_PID + ")");
			
			appHandle = (MegletHandle) getContext().getService(appDescRefs[0]);
			
		
		} catch (Exception e) {
			fail("install of tb1.jar has failed");
		}
		
	}
		
	public void stopServices() {
		ServiceReference[] appDescRefs;
		try {
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationHandle", 
					"(application.descriptor="
					+ TEST1_PID + ")");
			
			if (appDescRefs != null) {
				for (int i=0; i<appDescRefs.length; i++) {
					((MegletHandle) getContext().getService(appDescRefs[i])).destroy();			
				}
			}
			
			appDescRefs = getContext().getServiceReferences(
					"org.osgi.service.application.ApplicationHandle", 
					"(application.descriptor="
					+ TEST2_PID + ")");
			
			if (appDescRefs != null) {
				for (int i=0; i<appDescRefs.length; i++) {
					((MegletHandle) getContext().getService(appDescRefs[i])).destroy();				
				}		
			}
			
			getBundleMeglet().stop();
			getBundleMeglet().uninstall();
			
			bundleMeglet = null;
			appHandle = null;
			appDescriptor1 = null;
			appDescriptor2 = null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}		
	}
	
	// to launch the meglet using the default locale
	public void launchMegletHandle1() {
		try {
			if (appHandle != null) {
				appHandle.destroy();
			}
		} catch (Exception e) {
				log("#can't destroy the MegletHandle 1.0");
			}
			try {
				appHandle = (MegletHandle) appDescriptor1.launch(null);
			} catch (Exception e1) {
				fail("can't launch the MegletHandle 1.0");
			}
	}			

	// to get the MegletHandle of the specified name
	public MegletHandle getAppHandle(String name) {
		ServiceReference[] appDescRefs;
		try {
			appDescRefs = getContext()
			.getServiceReferences(
					"org.osgi.service.application.ApplicationHandle",
					"(application.descriptor="
							+ name + ")");
			return (appDescRefs != null) ? (MegletHandle) getContext().getService(appDescRefs[0]) : null;
		} catch (InvalidSyntaxException e) {
			return null;
		}		
		
	}
	
	
	/**
	 * @return Returns the isModified.
	 */
	public boolean isModified() {
		return isModified;
	}
	/**
	 * @param isModified The isModified to set.
	 */
	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}
	/**
	 * @return Returns the isRegistered.
	 */
	public boolean isRegistered() {
		return isRegistered;
	}
	/**
	 * @param isRegistered The isRegistered to set.
	 */
	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}
	/**
	 * @return Returns the isUnregistered.
	 */
	public boolean isUnregistered() {
		return isUnregistered;
	}
	/**
	 * @param isUnregistered The isUnregistered to set.
	 */
	public void setUnregistered(boolean isUnregistered) {
		this.isUnregistered = isUnregistered;
	}
	
/*	public void unprepare() {
		ServiceReference srvRefs = getContext().getServiceReference(MegletInterface.class.getName());
		MegletInterface meglets = (MegletInterface) getContext().getService(srvRefs);
		log("#unPrepare");
		assertTrue("Asserting if the start method of meglet instance has been called.", meglets.isStarted());	
		assertTrue("Asserting if the suspend method of meglet instance has been called.", meglets.isSuspended());
		assertTrue("Asserting if the resume method of meglet instance has been called.", meglets.isResumed());
		assertTrue("Asserting if the stop method of meglet instance has been called.", meglets.isStopped());
		try {
			srvRefs.getBundle().uninstall();
		} catch (BundleException e) {
			log("#fail at uninstall of test meglet.");
		}
	}*/
	
	public void cleanUp(MegletHandle meglet, PermissionInfo[] infos) {
		getPermissionAdmin()
		.setPermissions(getTb2Location(), infos);		

		if (meglet != null) {
			try {
				meglet.destroy();
			} catch (Exception e) {
				fail("#fail at meglet destroy.");
			}
		}
	}
	
	public void cleanUp(PermissionInfo[] infos) {
		getPermissionAdmin().setPermissions(getTb2Location(), infos);
		getAppDescriptor1().unlock();
		launchMegletHandle1();
	}
	
	public void cleanUp(ScheduledApplication sa, PermissionInfo[] infos) {
		getPermissionAdmin()
		.setPermissions(getTb2Location(), infos);	
		if (sa!=null) {
			sa.remove();
		}
		MegletHandle meglet = getAppHandle(ApplicationTestControl.TEST2_PID);
		while (meglet != null) {
			try {
				meglet.destroy();
			} catch (Exception e) {
				log("#Cant destroy the meglet");
			}
			meglet = getAppHandle(ApplicationTestControl.TEST2_PID);				
		}				

		getAppDescriptor2().lock();
	}
	
	public void cleanUpRemove(PermissionInfo[] infos) {
		getPermissionAdmin()
		.setPermissions(getTb2Location(), infos);	
		MegletHandle meglet = getAppHandle(ApplicationTestControl.TEST2_PID);
		while (meglet != null) {
			try {
				meglet.destroy();
			} catch (Exception e) {
				log("#Cant destroy the meglet");
			}
			meglet = getAppHandle(ApplicationTestControl.TEST2_PID);				
		}				
		
		getAppDescriptor2().lock();
	}
	
	/**
	 * @return Returns the dmtAdmin.
	 */
	public DmtAdmin getDmtAdmin() {
		return dmtAdmin;
	}
	/**
	 * @return Returns the isOurMegletActivated.
	 */
	public boolean isOurMegletActivated() {
		return isOurMegletActivated;
	}
	/**
	 * @param isOurMegletActivated The isOurMegletActivated to set.
	 */
	public void setOurMegletActivated(boolean isOurMegletActivated) {
		this.isOurMegletActivated = isOurMegletActivated;
	}
	

	/**
	 * @return Returns the megletInterface.
	 */
	public MegletInterface getMegletInterface() {
		return megletInterface;
	}
}
