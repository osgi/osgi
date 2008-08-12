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
 * 11/03/2005   Eduardo Oliveira
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 * 23/03/2005   Eduardo Oliveira
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * 24/03/2005   Alexandre Alves
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 * May 20, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 * Jun 28, 2005	Andre Assad
 * 92           Changes after face to face meeting
 * ===========  ==============================================================
 **/
package org.osgi.test.cases.monitor.tb1.MonitorAdmin;

import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.TestInterface;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Test Class Validates the implementation of
 * <code>getMonitorableNames</code> method, according to MEG reference
 * documentation.
 */
public class GetMonitorableNames implements TestInterface {
	private MonitorTestControl tbc;

	public GetMonitorableNames(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testGetMonitorableNames001();
		testGetMonitorableNames002();
		testGetMonitorableNames003();
	}

	/**
	 * This method asserts if the MonitorAdmin returns
	 * the correct list of monitorables.
	 * 
	 * @spec MonitorAdmin.getMonitorableNames()
	 */
	private void testGetMonitorableNames001() {
		tbc.log("#testGetMonitorableNames001");
		try {
			// makes sure that only two monitorables will be in the registry
			tbc.stopMonitorables();
			tbc.installMonitorables();

			String[] monitorableNames = tbc.getMonitorAdmin().getMonitorableNames();

			boolean hasMon1 = false;
			boolean hasMon2 = false;
			for (int i = 0; i < monitorableNames.length
					&& !(hasMon1 && hasMon2); i++) {
				if (monitorableNames[i].equals(MonitorConstants.SV_MONITORABLEID1)) {
					hasMon1 = true;
				} else if (monitorableNames[i].equals(MonitorConstants.SV_MONITORABLEID2)) {
					hasMon2 = true;
				}
			}

			tbc.assertTrue("There are only two monitorables", (2 == monitorableNames.length));
			tbc.assertTrue("MonitorAdmin has returned only the monitorables that we have registered.",(hasMon1 && hasMon2));

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the MonitorAdmin returns
	 * the correct list of monitorables in alphabetical order.
	 * 
	 * @spec MonitorAdmin.getMonitorableNames()
	 */
	private void testGetMonitorableNames002() {
		tbc.log("#testGetMonitorableNames002");
		try {

			String[] monitorableNames = tbc.getMonitorAdmin().getMonitorableNames();

			boolean passed = true;
			for (int i = 0; i < monitorableNames.length - 1; i++) {
				if (monitorableNames[i].compareTo(monitorableNames[i + 1]) > 0) {
					passed = false;
				}
			}

			tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE,
					new String[] { "The returned monitorable names is in alphabetical order." }),passed);
			
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}

	/**
	 * This method asserts if an empty array is returned 
	 * if no Monitorable service is registered.
	 * 
	 * @spec MonitorAdmin.getMonitorableNames()
	 */
	private void testGetMonitorableNames003() {
		tbc.log("#testGetMonitorableNames003");
		tbc.stopMonitorables();
		try {

			String[] monitorableNames = tbc.getMonitorAdmin().getMonitorableNames();

			tbc.assertTrue("Asserting if the returned monitorables names list is empty. ",(monitorableNames.length == 0));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		} finally {
			tbc.installMonitorables();
		}
	}
}
