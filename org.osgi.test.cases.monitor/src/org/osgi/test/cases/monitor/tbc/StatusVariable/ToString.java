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

import org.osgi.test.cases.monitor.tbc.MonitorConstants;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Leonardo Barros
 * 
 * This Test Class Validates the implementation of
 * <code>toString</code> method, according to MEG reference
 *                     documentation.
 */
public class ToString {
	private String[] CM = { "CC", "DER", "GAUGE", "SI" };

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
	 * This method asserts if the returned String contains the full path,
	 * collection method, timestamp, type and value parameters of the
	 * StatusVariable in the following format: StatusVariable( <path>, <cm>,
	 * <timestamp>, <type>, <value>). StatusVariable is created using integer
	 * constructor.
	 * 
	 * @spec StatusVariable.toString()
	 */
	private void testToString001() {
		try {
			tbc.log("#testToString001");
			org.osgi.service.monitor.StatusVariable sv = null;

			for (int i = 0; i < 4; i++) {
				sv = new org.osgi.service.monitor.StatusVariable(
						MonitorConstants.SV_NAME1, i,
						MonitorConstants.SV_INT_VALUE);

				tbc.assertEquals(MessagesConstants.getMessage(
						MessagesConstants.ASSERT_EQUALS, new String[] {
								"sv.toString()",
								"StatusVariable(" + MonitorConstants.SV_NAME1
										+ ", " + CM[i] + ", "
										+ sv.getTimeStamp() + ", INTEGER, "
										+ sv.getInteger() + ")" }),
						"StatusVariable(" + MonitorConstants.SV_NAME1 + ", "
								+ CM[i] + ", " + sv.getTimeStamp()
								+ ", INTEGER, " + sv.getInteger() + ")", sv
								.toString());

			}

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the returned String contains the full path,
	 * collection method, timestamp, type and value parameters of the
	 * StatusVariable in the following format: StatusVariable( <path>, <cm>,
	 * <timestamp>, <type>, <value>). StatusVariable is created using boolean
	 * constructor.
	 * 
	 * @spec StatusVariable.toString()
	 */
	private void testToString002() {
		try {
			tbc.log("#testToString002");
			org.osgi.service.monitor.StatusVariable sv = null;

			for (int i = 0; i < 4; i++) {
				sv = new org.osgi.service.monitor.StatusVariable(
						MonitorConstants.SV_NAME1, i,
						MonitorConstants.SV_BOOLEAN_VALUE);

				tbc.assertEquals(MessagesConstants.getMessage(
						MessagesConstants.ASSERT_EQUALS, new String[] {
								"sv.toString()",
								"StatusVariable(" + MonitorConstants.SV_NAME1
										+ ", " + CM[i] + ", "
										+ sv.getTimeStamp() + ", BOOLEAN, "
										+ sv.getBoolean() + ")" }),
						"StatusVariable(" + MonitorConstants.SV_NAME1 + ", "
								+ CM[i] + ", " + sv.getTimeStamp()
								+ ", BOOLEAN, " + sv.getBoolean() + ")", sv
								.toString());

			}

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the returned String contains the full path,
	 * collection method, timestamp, type and value parameters of the
	 * StatusVariable in the following format: StatusVariable( <path>, <cm>,
	 * <timestamp>, <type>, <value>). StatusVariable is created using float
	 * constructor.
	 * 
	 * @spec StatusVariable.toString()
	 */
	private void testToString003() {
		try {
			tbc.log("#testToString003");
			org.osgi.service.monitor.StatusVariable sv = null;

			for (int i = 0; i < 4; i++) {
				sv = new org.osgi.service.monitor.StatusVariable(
						MonitorConstants.SV_NAME1, i,
						MonitorConstants.SV_FLOAT_VALUE);

				tbc.assertEquals(MessagesConstants.getMessage(
						MessagesConstants.ASSERT_EQUALS, new String[] {
								"sv.toString()",
								"StatusVariable(" + MonitorConstants.SV_NAME1
										+ ", " + CM[i] + ", "
										+ sv.getTimeStamp() + ", FLOAT, "
										+ sv.getFloat() + ")" }),
						"StatusVariable(" + MonitorConstants.SV_NAME1 + ", "
								+ CM[i] + ", " + sv.getTimeStamp()
								+ ", FLOAT, " + sv.getFloat() + ")", sv
								.toString());

			}

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the returned String contains the full path,
	 * collection method, timestamp, type and value parameters of the
	 * StatusVariable in the following format: StatusVariable( <path>, <cm>,
	 * <timestamp>, <type>, <value>). StatusVariable is created using String
	 * constructor.
	 * 
	 * @spec StatusVariable.toString()
	 */
	private void testToString004() {
		try {
			tbc.log("#testToString004");
			org.osgi.service.monitor.StatusVariable sv = null;

			for (int i = 0; i < 4; i++) {
				sv = new org.osgi.service.monitor.StatusVariable(
						MonitorConstants.SV_NAME1, i,
						MonitorConstants.SV_STRING_VALUE);

				tbc.assertEquals(MessagesConstants.getMessage(
						MessagesConstants.ASSERT_EQUALS, new String[] {
								"sv.toString()",
								"StatusVariable(" + MonitorConstants.SV_NAME1
										+ ", " + CM[i] + ", "
										+ sv.getTimeStamp() + ", STRING, "
										+ sv.getString() + ")" }),
						"StatusVariable(" + MonitorConstants.SV_NAME1 + ", "
								+ CM[i] + ", " + sv.getTimeStamp()
								+ ", STRING, " + sv.getString() + ")", sv
								.toString());

			}

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

}
