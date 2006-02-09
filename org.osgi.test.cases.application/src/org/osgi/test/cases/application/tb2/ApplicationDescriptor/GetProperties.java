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

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>getDescription</code> method, according to MEG reference
 * documentation.
 */
public class GetProperties implements TestInterface {
	private ApplicationTestControl tbc;

	public GetProperties(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetProperties001();
		testGetProperties002();
		testGetProperties003();
	}

	/**
	 * This method asserts that null can be passed and
	 * no exception is thrown and assert the returned values.
	 * 
	 * @spec ApplicationDescriptor.getProperties(String)
	 */
	private void testGetProperties001() {
		PermissionInfo[] infos = null;	
		try {
			tbc.log("#testGetProperties001");
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());			
			tbc.setDefaultPermission();
			Map map = tbc.getAppDescriptor().getProperties(null);
			assertMap(map);			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(infos);
		}
	}
	
	/**
	 * This method asserts that an empty string can be passed and
	 * no exception is thrown and assert the returned values.
	 * 
	 * @spec ApplicationDescriptor.getProperties(String)
	 */
	private void testGetProperties002() {
		PermissionInfo[] infos = null;	
		try {
			tbc.log("#testGetProperties002");
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());			
			tbc.setDefaultPermission();
			Map map = tbc.getAppDescriptor().getProperties("");
			assertMap(map);		
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		} finally {
			tbc.cleanUp(infos);
		}
	}	
	
	/**
	 * This method asserts that if the application descriptor is unregistered
	 * IllegalStateException will be thrown.
	 * 
	 * @spec ApplicationDescriptor.getProperties(String)
	 */
	private void testGetProperties003() {
		PermissionInfo[] infos = null;	
		try {
			tbc.log("#testGetProperties003");
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());			
			tbc.setDefaultPermission();
			tbc.unregisterDescriptor();
			tbc.getAppDescriptor().getProperties(null);
			
			tbc.failException("#", IllegalStateException.class);
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
			tbc.cleanUp(infos);
		}
	}
	
	private void assertMap(Map map) {
		tbc.assertEquals("Asserting the application.locked value", Boolean.FALSE, map.get("application.locked"));
		tbc.assertEquals("Asserting the service.vendor value", "Cesar", map.get("service.vendor"));
		tbc.assertEquals("Asserting the application.name value", ApplicationConstants.APPLICATION_NAME, map.get("application.name"));
		tbc.assertEquals("Asserting the application.visible value", Boolean.TRUE, map.get("application.visible"));
		tbc.assertTrue("Asserting if the application.container property exist.", map.containsKey("application.container"));
		tbc.assertEquals("Asserting the application.launchable value", Boolean.TRUE, map.get("application.launchable"));
		tbc.assertEquals("Asserting the application.version value", ApplicationConstants.APP_VERSION, map.get("application.version"));
		
		try {
			InputStream stream = new URL(map.get("application.icon").toString()).openStream();
			tbc.assertNotNull("Asserting if a stream was returned property.", stream);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());			
		}
			
	}

}
