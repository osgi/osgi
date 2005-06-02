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
 * May 25, 2005  Leonardo Barros
 * 38           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.Meglet;

import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.meglet#getComponentContext
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>Meglet<code> methods, according to MEG reference
 *                     documentation.
 */
public class Meglet {
	private ApplicationTestControl tbc;

	public Meglet(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testMeglet001();
		testMeglet002();
		testMeglet003();
		testMeglet004();
	}

	/**
	 * @testID testMeglet001
	 * @testDescription Asserts if getComponentContext returned value is not
	 *                  null.
	 */
	public void testMeglet001() {
		try {
			tbc
					.assertNotNull(
							"Asserts if getComponentContext returned value is not null",
							tbc.getMegletInterface().getCompContext());
		} catch (Exception e) {
			tbc.fail(e.getClass().getName() + " incorrectly thrown.");
		}
	}

	/**
	 * @testID testMeglet002
	 * @testDescription Asserts if getInstanceID returns the correct ID.
	 */
	public void testMeglet002() {
		try {
			tbc.assertEquals("Asserts if getInstanceID returns the correct ID",
					tbc.getAppHandle().getInstanceID(), tbc
							.getMegletInterface().getInstID());
		} catch (Exception e) {
			tbc.fail(e.getClass().getName() + " incorrectly thrown.");
		}
	}

	/**
	 * @testID testMeglet003
	 * @testDescription Asserts if IllegalStateException is thrown when if the
	 *                  Meglet handle is unregistered.
	 */
	public void testMeglet003() {
		try {
			tbc.getAppHandle().destroy();

			tbc.getMegletInterface().getInstID();

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
		}
	}

	/**
	 * @testID testMeglet004
	 * @testDescription Asserts if getProperties returns the correct key-value
	 *                  pairs.
	 */
	public void testMeglet004() {
		try {
			tbc
					.assertEquals(
							"Asserts if getProperties returns the correct key-value pairs",
							tbc.getMeg1Properties(), tbc.getMegletInterface()
									.getProps());
		} catch (Exception e) {
			tbc.fail(e.getClass().getName() + " incorrectly thrown.");
		}
	}

}