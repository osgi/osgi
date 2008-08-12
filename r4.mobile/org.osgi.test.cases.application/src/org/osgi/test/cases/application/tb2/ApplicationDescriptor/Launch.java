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
 * 22/08/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.ApplicationDescriptor;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.application.ApplicationAdminPermission;
import org.osgi.service.application.ApplicationException;
import org.osgi.service.application.ApplicationHandle;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of <code>launch</code> method,
 * according to MEG reference documentation.
 */
public class Launch implements TestInterface {
	private ApplicationTestControl tbc;

	public Launch(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testLaunch001();
		testLaunch002();
		testLaunch003();
		testLaunch004();
		testLaunch005();
		testLaunch006();
		testLaunch007();
		testLaunch008();
		testLaunch009();
		testLaunch010();
		testLaunch011();
		testLaunch012();
		testLaunch013();
		testLaunch014();
	}

	/**
	 * This method asserts that null can be passed as argument and no exception
	 * is thrown.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch001() {
		tbc.log("#testLaunch001");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));

			handle = tbc.getAppDescriptor().launch(null);
			
			tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}

	/**
	 * This method asserts that when the ApplicationDescriptor is locked, an
	 * ApplicationException is thrown.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch002() {
		tbc.log("#testLaunch002");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION+","+ApplicationAdminPermission.LOCK_ACTION));

			tbc.getAppDescriptor().lock();

			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(null);

			tbc.failException("", ApplicationException.class);
		} catch (ApplicationException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { Exception.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							ApplicationException.class.getName(),
							e.getClass().getName() }));				
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}

	/**
	 * This method asserts that if the application descriptor is unregistered
	 * IllegalStateException will be thrown.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch003() {
		tbc.log("#testLaunch003");
		PermissionInfo[] infos = null;
		ApplicationHandle handle = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));		

			tbc.unregisterDescriptor();
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(null);

			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalStateException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.installDescriptor();
			tbc.cleanUp(handle, infos);
		}
	}

	/**
	 * This method asserts that SecurityException is thrown when the caller
	 * doesn't have "lifecycle" ApplicationAdminPermission for the application.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch004() {
		tbc.log("#testLaunch004");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setDefaultPermission();

			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(null);

			tbc.failException("", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { SecurityException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}

	/**
	 * This method asserts that an ApplicationHandle is registered in service
	 * registry after a launch call.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch005() {
		tbc.log("#testLaunch005");
		PermissionInfo[] infos = null;
		ApplicationHandle handle = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));

			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(null);

			tbc
					.assertTrue(
							"Asserting that an instance of ApplicationHandle is registered in ServiceRegistry.",
							tbc.isApplicationHandleRegistered());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}
	
	/**
	 * This method asserts that an Exception is thrown when we pass
	 * a Map with an empty string as MapName.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch006() {
		tbc.log("#testLaunch006");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			Map map = new HashMap();
			map.put("" , "Reject");		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));			
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}
	
	/**
	 * This method asserts that no Exception is thrown when
	 * we pass null as a mapValue.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch007() {
		tbc.log("#testLaunch007");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
				ApplicationAdminPermission.class.getName(),
				ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));

			Map map = new HashMap();
			map.put("Test" , null);		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.pass("No exception was thrown.");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}
	
	/**
	 * This method asserts that an Exception is thrown when we pass
	 * a Map with null as MapName.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch008() {
		tbc.log("#testLaunch008");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			Map map = new HashMap();
			map.put(null , "Reject");		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));	
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}	
	
	/**
	 * This method asserts that an Exception is thrown when we pass
	 * a Map with an Integer as MapName.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch009() {
		tbc.log("#testLaunch009");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			Map map = new HashMap();
			map.put(new Integer(2) , "Reject");		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));	
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}
	
	/**
	 * This method asserts that an Exception is thrown when we pass
	 * a Map with an Object as MapName.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch010() {
		tbc.log("#testLaunch010");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			Map map = new HashMap();
			map.put(new Object() , "Reject");		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));	
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}	
	
	/**
	 * This method asserts that an Exception is thrown when we pass
	 * a Map with a Boolean as MapName.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch011() {
		tbc.log("#testLaunch011");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			Map map = new HashMap();
			map.put(new Boolean(true) , "Reject");		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));	
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}
	
	/**
	 * This method asserts that an Exception is thrown when we pass
	 * a Map with a Float as MapName.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch012() {
		tbc.log("#testLaunch012");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			Map map = new HashMap();
			map.put(new Float(0.2f) , "Reject");		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));	
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}
	
	/**
	 * This method asserts that an Exception is thrown when we pass
	 * a Map with a Byte as MapName.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch013() {
		tbc.log("#testLaunch013");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			Map map = new HashMap();
			map.put(new Byte("a") , "Reject");		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));	
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}	
	
	/**
	 * This method asserts that an Exception is thrown when we pass
	 * a Map with a Double as MapName.
	 * 
	 * @spec ApplicationDescriptor.launch(Map)
	 */
	private void testLaunch014() {
		tbc.log("#testLaunch014");
		ApplicationHandle handle = null;
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());

			tbc.setLocalPermission(new PermissionInfo(
					ApplicationAdminPermission.class.getName(),
					ApplicationConstants.APPLICATION_PERMISSION_FILTER1, ApplicationAdminPermission.LIFECYCLE_ACTION));	

			Map map = new HashMap();
			map.put(new Double(12.1) , "Reject");		
						
			handle = (ApplicationHandle) tbc.getAppDescriptor().launch(map);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));	
		} finally {
			tbc.cleanUp(handle, infos);
		}
	}	

}