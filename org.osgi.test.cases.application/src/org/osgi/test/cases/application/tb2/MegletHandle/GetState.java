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
 * 24/05/2005   Alexandre Santos
 * 38           Rework after inspection 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tb2.MegletHandle;

import org.osgi.service.application.meglet.MegletHandle;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.application.ApplicationHandle#getState,resume
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>GetState<code> method, according to MEG reference
 *                     documentation.
 */
public class GetState implements TestInterface {
	private ApplicationTestControl tbc;

	public GetState(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetState001();
		testGetState002();
		testGetState003();
		testGetState004();
	}

	/**
	 * @testID testGetState001
	 * @testDescription Asserts if the handler state is RUNNING.
	 */
	public void testGetState001() {
		tbc.log("#testGetState001");		
		MegletHandle handle = tbc.getAppHandle();
		tbc.assertEquals("Asserting if the handler state is RUNNING.",MegletHandle.RUNNING, handle.getState());
	}
	
	/**
	 * @testID testGetState002
	 * @testDescription Asserts if the handler state is SUSPEND.
	 */
	public void testGetState002() {
		tbc.log("#testGetState002");
		MegletHandle handle = tbc.getAppHandle();
		try {
			handle.suspend();
			tbc.assertEquals("Asserting if the handler state is SUSPEND.",MegletHandle.SUSPENDED, handle.getState());
			handle.resume();
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}		
	}
	
	/**
	 * @testID testGetState003
	 * @testDescription Asserts if the handler state is setted again to RUNNING.
	 */
	public void testGetState003() {
		tbc.log("#testGetState003");
		MegletHandle handle = tbc.getAppHandle();
		try {
			handle.suspend();
			handle.resume();
			tbc.assertEquals("Asserting if the handler state is RUNNING.",MegletHandle.RUNNING, handle.getState());
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}		
	}		
	
	/**
	 * @testID testGetState004
	 * @testDescription Asserts if it correctly thrown IllegalStateException
	 */
	public void testGetState004() {
		tbc.log("#testGetState004");
		MegletHandle handle = tbc.getAppHandle();
		try {
			handle.destroy();
			handle.getState();
			tbc.failException("", java.lang.IllegalStateException.class);
		} catch (java.lang.IllegalStateException e) {
			tbc.pass(MessagesConstants.getMessage(MessagesConstants.EXCEPTION_CORRECTLY_THROWN, new String[] {
					e.getClass().getName()}));
		} catch (Exception e) {			
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + " " + e.getClass().getName());
		} finally {
			tbc.launchMegletHandle1();
		}	
	}	

}
