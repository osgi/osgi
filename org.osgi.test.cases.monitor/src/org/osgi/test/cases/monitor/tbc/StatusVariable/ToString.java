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
 * 09/03/05 	Leonardo Barros
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.StatusVariable;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.service.monitor.StatusVariable#toString
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>toString<code> method, according to MEG reference
 *                     documentation.
 */
public class ToString {
	private MonitorTestControl tbc;

	public ToString(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testToString001();
		testToString002();
		testToString003();
		testToString004();
	}

	/**
	 * @testID testToString001
	 * @testDescription This method asserts if the toString is using the correct
	 *                  format.
	 */
	public void testToString001() {
		try {

			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_DER,
					MonitorTestControl.SV_LONG_VALUE);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "sv.toString()",
							"StatusVariable("+
									MonitorTestControl.SV_NAME1
									+ ", DER, " + sv.getTimeStamp()
									+ ", LONG, " + sv.getLong() + ")" }), "StatusVariable("
					+ MonitorTestControl.SV_NAME1 + ", DER, "
					+ sv.getTimeStamp() + ", LONG, " + sv.getLong() + ")", sv
					.toString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testToString002
	 * @testDescription This method asserts if the toString is using the correct
	 *                  format.
	 */
	public void testToString002() {
		try {

			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_GAUGE,
					MonitorTestControl.SV_DOUBLE_VALUE);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "sv.toString()",
							"StatusVariable("
									+ MonitorTestControl.SV_NAME1
									+ ", GAUGE, " + sv.getTimeStamp()
									+ ", DOUBLE, " + sv.getDouble() + ")"
							}), "StatusVariable("
					+ MonitorTestControl.SV_NAME1 + ", GAUGE, "
					+ sv.getTimeStamp() + ", DOUBLE, " + sv.getDouble() + ")",
					sv.toString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testToString003
	 * @testDescription This method asserts if the toString is using the correct
	 *                  format.
	 */
	public void testToString003() {
		try {

			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_DER,
					MonitorTestControl.SV_STRING_VALUE);

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "sv.toString()", 
							"StatusVariable("
									+ MonitorTestControl.SV_NAME1
									+ ", DER, " + sv.getTimeStamp()
									+ ", STRING, " + sv.getString() + ")"
							 }), "StatusVariable("
					+ MonitorTestControl.SV_NAME1 + ", DER, "
					+ sv.getTimeStamp() + ", STRING, " + sv.getString() + ")",
					sv.toString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testToString004
	 * @testDescription This method asserts if the toString is using the correct
	 *                  format.
	 */
	public void testToString004() {
		try {

			org.osgi.service.monitor.StatusVariable sv = new org.osgi.service.monitor.StatusVariable(
					MonitorTestControl.SV_NAME1,
					org.osgi.service.monitor.StatusVariable.CM_SI,
					MonitorTestControl.SV_BOOLEAN_VALUE);
			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "sv.toString()",
							"StatusVariable("
									+ MonitorTestControl.SV_NAME1
									+ ", SI, " + sv.getTimeStamp()
									+ ", BOOLEAN, " + sv.getBoolean() + ")"
							 }),
					"StatusVariable(" + MonitorTestControl.SV_NAME1 + ", SI, "
							+ sv.getTimeStamp() + ", BOOLEAN, "
							+ sv.getBoolean() + ")", sv.toString());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

}
