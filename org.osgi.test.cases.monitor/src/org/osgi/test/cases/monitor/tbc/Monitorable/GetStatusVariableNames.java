/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * Mar 9, 2005  Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.Monitorable;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.test.cases.monitor.tbc.Monitorable#getStatusVariableNames
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>getStatusVariableNames<code> method, according to MEG reference
 *                     documentation.
 */
public class GetStatusVariableNames {
	private MonitorTestControl tbc;

	public GetStatusVariableNames(MonitorTestControl tbc) {
		this.tbc = tbc;
	}
	
	public void run(){
		testGetStatusVariableNames001();
	}	
	
	/**
	 * @testID testGetStatusVariableNames001
	 * @testDescription Tests if the returned names
	 *                  was the same used in monitorable impl.
	 */
	public void testGetStatusVariableNames001() {
		String[] statusVariables = tbc.getMonitorable().getStatusVariableNames();
		
		tbc.assertEquals(MessagesConstants.getMessage(MessagesConstants.ASSERT_EQUALS, new String[] { "length of the returned variable names", MonitorTestControl.SV_LENGTH+"" }) , MonitorTestControl.SV_LENGTH, statusVariables.length);
		
		boolean passed = true;
		for (int i=0; i<statusVariables.length; i++) {
			if ((!statusVariables[i].equals(MonitorTestControl.SV_NAME1)) && (!statusVariables[i].equals(MonitorTestControl.SV_NAME2))) {
				passed = false;
			}
		}
		
		tbc.assertTrue(MessagesConstants.getMessage(MessagesConstants.ASSERT_TRUE, new String[] { "ours monitorable returns all the statusvariables." }), passed);
	}
}
