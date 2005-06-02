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
 * 02/05/2005   Leonardo Barros
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 * 13/05/2005   Alexandre Santos
 * 38           Update to fix errors 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.MegletDescriptor;

import java.util.Map;

import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.ApplicationDescriptor#getProperties
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getProperties<code> method, according to MEG reference
 *                     documentation.
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
		testGetProperties004();
		testGetProperties005();
	}

	/**
	 * @testID testGetProperties001
	 * @testDescription Asserts if the properties of the application descriptor
	 *                  is correctly returned when a default locale is used
	 */
	public void testGetProperties001() {
		try {
			tbc.log("#testGetProperties001");
			Map props = tbc.getAppDescriptor1().getProperties(null);

			tbc.assertEquals("Asserting application name",
					ApplicationTestControl.TEST1_NAME_EN, (String) props
							.get(ApplicationDescriptor.APPLICATION_NAME));

			String icon = (String) props
			.get(ApplicationDescriptor.APPLICATION_ICON);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { " the returned properties has the correct icon." } )
					, icon.endsWith(ApplicationTestControl.TEST1_ICON_EN));
			
			assertProperties(props);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * @testID testGetProperties002
	 * @testDescription Asserts if the properties of the application descriptor
	 *                  is correctly returned when a locale(en) is passed as
	 *                  parameter
	 */
	public void testGetProperties002() {
		try {
			tbc.log("#testGetProperties002");
			Map props = tbc.getAppDescriptor1().getProperties("en");

			tbc.assertEquals("Asserting application name",
					ApplicationTestControl.TEST1_NAME_EN, (String) props
							.get(ApplicationDescriptor.APPLICATION_NAME));

			String icon = (String) props
			.get(ApplicationDescriptor.APPLICATION_ICON);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { " the returned properties has the correct icon." } )
					, icon.endsWith(ApplicationTestControl.TEST1_ICON_EN));
					
			assertProperties(props);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * @testID testGetProperties003
	 * @testDescription Asserts if the properties of the application descriptor
	 *                  is correctly returned when a locale(br) is passed as
	 *                  parameter
	 */
	public void testGetProperties003() {
		try {
			tbc.log("#testGetProperties003");
			Map props = tbc.getAppDescriptor1().getProperties("br");

			tbc.assertEquals("Asserting application name",
					ApplicationTestControl.TEST1_NAME_BR, (String) props
							.get(ApplicationDescriptor.APPLICATION_NAME));

			String icon = (String) props
			.get(ApplicationDescriptor.APPLICATION_ICON);
			
			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { " the returned properties has the correct icon." } )
					, icon.endsWith(ApplicationTestControl.TEST1_ICON_BR));

			assertProperties(props);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * @testID testGetProperties004
	 * @testDescription Asserts if IllegalStateException is thrown when
	 *                  application descriptor is unregistered
	 */
	public void testGetProperties004() {
		try {
			tbc.log("#testGetProperties004");
			tbc.stopServices();
			
			tbc.getAppDescriptor1().getProperties(null);
			tbc.failException("", IllegalStateException.class);
		} catch (IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalStateException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.installBundleMeglet();
		}
		
	}
	
	/**
	 * @testID testGetProperties005
	 * @testDescription Asserts if an empty string is passed as parameter
	 *                  a non-null value is returned
	 */
	public void testGetProperties005() {
		try {
			tbc.log("#testGetProperties005");
			Map props = tbc.getAppDescriptor1().getProperties("");

			tbc.assertNotNull("Asserting if the returned properties is not null", props);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	private void assertProperties(Map props) throws Exception {
		tbc.assertEquals("Asserting service pid",
				ApplicationTestControl.TEST1_PID, (String) props
						.get(ApplicationDescriptor.APPLICATION_PID));

		tbc.assertEquals("Asserting application version",
				ApplicationTestControl.TEST1_VERSION, (String) props
						.get(ApplicationDescriptor.APPLICATION_VERSION));

		tbc.assertEquals("Asserting application vendor",
				ApplicationTestControl.TEST1_VENDOR, (String) props
						.get(ApplicationDescriptor.APPLICATION_VENDOR));

		tbc
				.assertEquals(
						"Asserting if only one instance of the application can run at the same time",
						ApplicationTestControl.TEST1_SINGLETON,
						new Boolean((String) props
								.get(ApplicationDescriptor.APPLICATION_SINGLETON)));

		tbc.assertEquals(
				"Asserting if the application has to be started automatically",
				ApplicationTestControl.TEST1_AUTOSTART, new Boolean((String) props
						.get(ApplicationDescriptor.APPLICATION_AUTOSTART)));

		tbc.assertEquals(
				"Asserting if the application should be visible for the user",
				ApplicationTestControl.TEST1_VISIBLE, new Boolean((String) props
						.get(ApplicationDescriptor.APPLICATION_VISIBLE)));

		tbc.assertEquals(
				"Asserting if the application is ready to be launched",
				ApplicationTestControl.TEST1_LAUNCHABLE, new Boolean((String) props
						.get(ApplicationDescriptor.APPLICATION_LAUNCHABLE)));

		tbc
				.assertEquals(
						"Asserting if the application is locked to prevent launching it",
						ApplicationTestControl.TEST1_LOCKED, new Boolean((String) props
								.get(ApplicationDescriptor.APPLICATION_LOCKED)));

	}

}
