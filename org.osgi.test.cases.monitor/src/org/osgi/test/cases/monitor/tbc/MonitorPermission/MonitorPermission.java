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
 * 24/03/2005   Alexandre Alves
 * 14           Updates after formal inspection (JSTD-MEGTCK-CODE-INSP011)
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.MonitorPermission;

import java.util.StringTokenizer;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @methodUnderTest org.osgi.test.cases.monitor.tbc.MonitorPermission#MonitorPermission,getActions
 * @generalDescription This Test Class Validates the implementation of
 *                     <code>MonitorPermission<code> method, according to MEG reference
 *                     documentation.
 */
public class MonitorPermission {
	private MonitorTestControl tbc;

	public MonitorPermission(MonitorTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
		testMonitorPermission001();
		testMonitorPermission002();
		testMonitorPermission003();
		testMonitorPermission004();
		testMonitorPermission005();
		testMonitorPermission006();
		testMonitorPermission007();
		testMonitorPermission008();
		testMonitorPermission009();
		testMonitorPermission010();
		testMonitorPermission011();
		testMonitorPermission012();
		testMonitorPermission013();
		testMonitorPermission014();
		testMonitorPermission015();
		testMonitorPermission016();
		testMonitorPermission017();
		testMonitorPermission018();
	}

	/**
	 * @testID testMonitorPermission001
	 * @testDescription Tests if the constructor with a valid target passed as
	 *                  parameter does not throws any exception
	 */
	public void testMonitorPermission001() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"com.mycomp.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ);
			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission002
	 * @testDescription Tests if the constructor with a valid target passed as
	 *                  parameter does not throws any exception
	 */
	public void testMonitorPermission002() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"com.mycomp.myapp/*",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission003
	 * @testDescription Tests if the constructor with a valid target passed as
	 *                  parameter does not throws any exception
	 */
	public void testMonitorPermission003() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"com.mycomp.*/queue*",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission004
	 * @testDescription Tests if the constructor with a valid target passed as
	 *                  parameter throws any exception
	 */
	public void testMonitorPermission004() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/queue*", org.osgi.service.monitor.MonitorPermission.READ);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testMonitorPermission005
	 * @testDescription Tests if the constructor with an invalid target passed as
	 *                  parameter throws an exception
	 */
	public void testMonitorPermission005() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testMonitorPermission006
	 * @testDescription Tests if the constructor with a valid target passed as
	 *                  parameter does not throws any exception
	 */
	public void testMonitorPermission006() {
		try {
			org.osgi.service.monitor.MonitorPermission kp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.READ);

			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission007
	 * @testDescription Tests if the constructor with an invalid target passed
	 *                  as parameter throws an exception of correct type
	 */
	public void testMonitorPermission007() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));

		}
	}

	/**
	 * @testID testMonitorPermission008
	 * @testDescription Tests if the constructor with an invalid target passed
	 *                  as parameter throws an exception of correct type
	 */
	public void testMonitorPermission008() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"com.*.myapp/*",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testMonitorPermission009
	 * @testDescription Tests if the constructor with an invalid target passed
	 *                  as parameter throws an exception of correct type
	 */
	public void testMonitorPermission009() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"com.myapp.*/*_length",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testMonitorPermission010
	 * @testDescription Tests if the constructor with an invalid target passed
	 *                  as parameter throws an exception of correct type
	 */
	public void testMonitorPermission010() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"com.myapp.*/queue*length",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testMonitorPermission011
	 * @testDescription Tests if the constructor with an invalid action passed
	 *                  as parameter throws an exception
	 */
	public void testMonitorPermission011() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", "test");

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testMonitorPermission012
	 * @testDescription Tests if the constructor with an invalid action passed
	 *                  as parameter throws an exception
	 */
	public void testMonitorPermission012() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.READ
							+ ",test");

			tbc.failException("", IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			tbc.pass(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_CORRECTLY_THROWN,
					new String[] { IllegalArgumentException.class.getName() }));
		} catch (Exception e) {
			tbc.fail(MessagesConstants.getMessage(
					MessagesConstants.EXCEPTION_THROWN, new String[] {
							IllegalArgumentException.class.getName(),
							e.getClass().getName() }));
		}
	}

	/**
	 * @testID testMonitorPermission013
	 * @testDescription Tests if getActions method returns the correct value as
	 *                  passed to the constructor
	 * 
	 */
	public void testMonitorPermission013() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.READ);

			String actions = mp.getActions();

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "getActions() results",
							org.osgi.service.monitor.MonitorPermission.READ+","+org.osgi.service.monitor.MonitorPermission.DISCOVER}),
							org.osgi.service.monitor.MonitorPermission.READ+","+org.osgi.service.monitor.MonitorPermission.DISCOVER, actions);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission014
	 * @testDescription Tests if getActions method returns the correct value as
	 *                  passed to the constructor
	 * 
	 */
	public void testMonitorPermission014() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.RESET);

			String actions = mp.getActions();

			tbc.assertEquals(MessagesConstants.getMessage(
					MessagesConstants.ASSERT_EQUALS, new String[] { "getActions() results",
							org.osgi.service.monitor.MonitorPermission.RESET }),
					org.osgi.service.monitor.MonitorPermission.RESET, actions);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission015
	 * @testDescription Tests if getActions method returns the correct value as
	 *                  passed to the constructor
	 * 
	 */
	public void testMonitorPermission015() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.PUBLISH);

			String actions = mp.getActions();

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] { "getActions() results", 
													org.osgi.service.monitor.MonitorPermission.PUBLISH }),
							org.osgi.service.monitor.MonitorPermission.PUBLISH,
							actions);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission016
	 * @testDescription Tests if getActions method returns the correct value as
	 *                  passed to the constructor
	 * 
	 */
	public void testMonitorPermission016() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.STARTJOB);

			String actions = mp.getActions();

			tbc
					.assertEquals(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_EQUALS,
											new String[] { "getActions() result", 
													org.osgi.service.monitor.MonitorPermission.STARTJOB }),
							org.osgi.service.monitor.MonitorPermission.STARTJOB,
							actions);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission017
	 * @testDescription Tests if getActions method returns the correct value as
	 *                  passed to the constructor
	 * 
	 */
	public void testMonitorPermission017() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.STARTJOB);

			StringTokenizer st = new StringTokenizer(mp.getActions(), ",");

			boolean isRead = false;
			boolean isStartJob = false;

			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token
						.equals(org.osgi.service.monitor.MonitorPermission.READ)) {
					isRead = true;
				} else if (token
						.equals(org.osgi.service.monitor.MonitorPermission.STARTJOB)) {
					isStartJob = true;
				}
			}

			tbc
					.assertTrue(
							MessagesConstants
									.getMessage(
											MessagesConstants.ASSERT_TRUE,
											new String[] { "getActions method returns the correct value as passed to the constructor" }),
							isRead && isStartJob);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

	/**
	 * @testID testMonitorPermission018
	 * @testDescription Tests if getActions method returns the correct value as
	 *                  passed to the constructor
	 * 
	 */
	public void testMonitorPermission018() {
		try {
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.RESET
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.PUBLISH
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.STARTJOB);

			StringTokenizer st = new StringTokenizer(mp.getActions(), ",");

			boolean isRead = false;
			boolean isStartJob = false;
			boolean isPublish = false;
			boolean isReset = false;

			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token
						.equals(org.osgi.service.monitor.MonitorPermission.READ)) {
					isRead = true;
				} else if (token
						.equals(org.osgi.service.monitor.MonitorPermission.STARTJOB)) {
					isStartJob = true;
				} else if (token
						.equals(org.osgi.service.monitor.MonitorPermission.RESET)) {
					isReset = true;
				} else if (token
						.equals(org.osgi.service.monitor.MonitorPermission.PUBLISH)) {
					isPublish = true;
				}
			}

			tbc.assertTrue(MessagesConstants
					.getMessage(
							MessagesConstants.ASSERT_TRUE,
							new String[] { "getActions method returns the correct value as passed to the constructor" }), isRead && isStartJob
					&& isReset && isPublish);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION);
		}
	}

}
