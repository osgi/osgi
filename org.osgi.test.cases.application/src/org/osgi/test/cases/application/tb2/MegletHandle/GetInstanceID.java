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
 * 03/05/2005   Alexandre Santos
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.MegletHandle;

import org.osgi.service.application.meglet.MegletHandle;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.ApplicationHandle#getInstanceID
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>GetInstanceID<code> method, according to MEG reference
 *                     documentation.
 */
public class GetInstanceID implements TestInterface {
	private ApplicationTestControl tbc;

	public GetInstanceID(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetInstanceID001();
		testGetInstanceID002();
	}

	/**
	 * @testID testGetInstanceID001
	 * @testDescription Asserts if the instanceID is equals to Application PID
	 *                  defined in the Meglet XML file.
	 */
	public void testGetInstanceID001() {
		tbc.log("#testGetInstanceID001");
		MegletHandle handle = tbc.getAppHandle();
		tbc.assertTrue("the instanceID is the same used in xml.",
				(handle.getInstanceID().indexOf(
						ApplicationTestControl.TEST1_PID) >= 0));
	}

	/**
	 * @testID testGetInstanceID002
	 * @testDescription Asserts if it correctly thrown IllegalStateException
	 */
	public void testGetInstanceID002() {
		tbc.log("#testGetInstanceID002");
		MegletHandle handle = tbc.getAppHandle();
		try {
			handle.destroy();
			handle.getInstanceID();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { e.getClass().getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + " "
					+ e.getClass().getName());
		} finally {
			tbc.getAppDescriptor1().unlock();
			tbc.launchMegletHandle1();
		}
	}

}
