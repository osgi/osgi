/*
 *  Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 13/10/2005   Alexandre Santos
 * 153          Implement OAT test cases  
 * ===========  ==============================================================
 */

package org.osgi.test.cases.application.tb2.ApplicationDescriptor;

import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.application.tbc.ApplicationConstants;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;
import org.osgi.test.cases.application.tbc.TestInterface;
import org.osgi.test.cases.application.tbc.util.MessagesConstants;

/**
 * @author Alexandre Santos
 * 
 * This test class validates the implementation of <code>matchDNChain</code>
 * method, according to MEG reference documentation.
 */
public class MatchDNChain implements TestInterface {
	private ApplicationTestControl tbc;

	/**
	 * @param tbc
	 */
	public MatchDNChain(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testMatchDNChain001();
		testMatchDNChain002();
		testMatchDNChain003();
		testMatchDNChain004();
		testMatchDNChain005();
	}

	/**
	 * This method asserts that NullPointerException is thrown when we pass null
	 * as parameter.
	 * 
	 * @spec ApplicationDescriptor.matchDNChain(String)
	 */
	private void testMatchDNChain001() {
		tbc.log("#testMatchDNChain001");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());
			tbc.setDefaultPermission();
			tbc.getAppDescriptor().matchDNChain(null);
			tbc.failException("", NullPointerException.class);
		} catch (NullPointerException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { NullPointerException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							NullPointerException.class.getName(),
							e.getClass().getName() }));
		} finally {
			tbc.cleanUp(infos);
		}
	}

	/**
	 * This method asserts that it returns true when we have passed a pattern
	 * that correspond to this descriptor.
	 * 
	 * @spec ApplicationDescriptor.matchDNChain(String)
	 */
	private void testMatchDNChain002() {
		tbc.log("#testMatchDNChain002");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());
			tbc.setDefaultPermission();
			tbc
					.assertTrue(
							"Asserting that true is returned when we have passed a pattern that correspond to this descriptor.",
							tbc.getAppDescriptor().matchDNChain(
									ApplicationConstants.SIGNER_FILTER));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(infos);
		}
	}

	/**
	 * This method asserts that it returns false when we have passed a pattern
	 * that does not correspond to this descriptor.
	 * 
	 * @spec ApplicationDescriptor.matchDNChain(String)
	 */
	private void testMatchDNChain003() {
		tbc.log("#testMatchDNChain003");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());
			tbc.setDefaultPermission();
			tbc
					.assertTrue(
							"Asserting that false is returned when we have passed a pattern that does not correspond to this descriptor.",
							!tbc.getAppDescriptor().matchDNChain(
									ApplicationConstants.SIGNER_FILTER2));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(infos);
		}
	}

	/**
	 * This method asserts that false is returned when we have passed an 
	 * invalid format of signer.
	 * 
	 * @spec ApplicationDescriptor.matchDNChain(String)
	 */
	private void testMatchDNChain004() {
		tbc.log("#testMatchDNChain004");
		PermissionInfo[] infos = null;
		try {
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());
			tbc.setDefaultPermission();
			
			tbc
					.assertTrue(
							"Asserting that false is returned when we have passed an invalid format of signer.",
							!tbc.getAppDescriptor().matchDNChain(ApplicationConstants.INVALID));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.cleanUp(infos);
		}
	}

	/**
	 * This method asserts that if the application descriptor is unregistered
	 * IllegalStateException will be thrown.
	 * 
	 * @spec ApplicationDescriptor.matchDNChain(String)
	 */
	private void testMatchDNChain005() {
		PermissionInfo[] infos = null;
		try {
			tbc.log("#testMatchDNChain005");
			infos = tbc.getPermissionAdmin().getPermissions(
					tbc.getTb2Location());
			tbc.setDefaultPermission();
			tbc.unregisterDescriptor();
			tbc.getAppDescriptor().matchDNChain(
					ApplicationConstants.SIGNER_FILTER);

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

}
