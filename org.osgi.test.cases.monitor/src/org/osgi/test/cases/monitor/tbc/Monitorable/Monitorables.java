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
 * 19/05/2005   Alexandre Santos
 * 42           Implement MEGTCK for the application RFC 
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.Monitorable;

import java.util.Arrays;

import junit.framework.Assert;

import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.Activators.MonitorableActivatorInvalid;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Alexandre Alves
 * 
 * This Class Validates the constraints for monitorable registration.
 * 
 */
public class Monitorables {
	private MonitorTestControl tbc;

	/**
	 * @param tbc
	 */
	public Monitorables(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testMonitorables001();
		testMonitorables002();
		testMonitorables003();
	}

	/**
	 * This method asserts that when we use an empty string as service.pid
	 * in a monitorable registration, the monitorable is ignored.
	 * 
	 * @spec 120.7.2 Monitorable
	 */	
	private void testMonitorables001() {
		try {
			tbc.log("#testMonitorables001");
			String[] monitorablesBefore = tbc.getMonitorAdmin().getMonitorableNames();
			MonitorableActivatorInvalid monitorableActivatorEmpty = new MonitorableActivatorInvalid(tbc, "");
			monitorableActivatorEmpty.start(tbc.getContext());
			String[] monitorablesAfter = tbc.getMonitorAdmin().getMonitorableNames();
			Assert.assertTrue("Asserting if no monitorables was installed when we use a monitorable an empty string as service.pid.", Arrays.equals(monitorablesBefore, monitorablesAfter));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	

	/**
	 * This method asserts that when we use invalid characters as service.pid
	 * in a monitorable registration, the monitorable is ignored.
	 * 
	 * @spec 120.7.2 Monitorable
	 */	
	private void testMonitorables002() {
		try {
			tbc.log("#testMonitorables002");
			String[] monitorablesBefore = tbc.getMonitorAdmin().getMonitorableNames();
			MonitorableActivatorInvalid monitorableActivatorInvalid = new MonitorableActivatorInvalid(tbc, MonitorConstants.INVALID_ID);
			monitorableActivatorInvalid.start(tbc.getContext());		
			String[] monitorablesAfter = tbc.getMonitorAdmin().getMonitorableNames();
			Assert.assertTrue("Asserting if no monitorables was installed when we use a monitorable an empty string as service.pid.", Arrays.equals(monitorablesBefore, monitorablesAfter));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
	
	
	/**
	 * This method asserts that when we use a monitorable id with more than 20 characters
	 * in a monitorable registration, the monitorable is ignored.
	 * 
	 * @spec 120.7.2 Monitorable
	 */		
	private void testMonitorables003() {
		try {
			tbc.log("#testMonitorables003");
			String[] monitorablesBefore = tbc.getMonitorAdmin().getMonitorableNames();
			MonitorableActivatorInvalid monitorableActivatorInvalid = new MonitorableActivatorInvalid(tbc, MonitorConstants.LONGID);
			monitorableActivatorInvalid.start(tbc.getContext());		
			String[] monitorablesAfter = tbc.getMonitorAdmin().getMonitorableNames();
			Assert.assertTrue("Asserting if no monitorables was installed when we use a monitorable id with more than 20 characters.", Arrays.equals(monitorablesBefore, monitorablesAfter));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": " + e.getClass().getName());
		}
	}	
}
