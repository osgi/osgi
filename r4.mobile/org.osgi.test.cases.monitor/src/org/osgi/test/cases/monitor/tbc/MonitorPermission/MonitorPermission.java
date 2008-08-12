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
 * May 27, 2005	Alexandre Alves
 * 92           Make changes according to monitor RFC updates
 * ===========  ==============================================================
 */

package org.osgi.test.cases.monitor.tbc.MonitorPermission;

import java.util.StringTokenizer;

import org.osgi.test.cases.monitor.tbc.MonitorTestControl;
import org.osgi.test.cases.monitor.tbc.util.MessagesConstants;

/**
 * @author Leonardo Barros
 * 
 * This test class validates the implementation of
 * <code>MonitorPermission</code> method, according to MEG reference
 * documentation.
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
		testMonitorPermission019();
		testMonitorPermission020();
	}

	/**
	 * This method asserts if the constructor does not throw any exception when
	 * a valid target is passed as parameter
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission001() {
		try {
			tbc.log("#testMonitorPermission001");
			new org.osgi.service.monitor.MonitorPermission(
					"com.mycomp.myapp/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ);
			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the constructor does not throw any exception when
	 * target with wildcard is passed as parameter
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission002() {
		try {
			tbc.log("#testMonitorPermission002");
			new org.osgi.service.monitor.MonitorPermission(
					"com.mycomp.myapp/*",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the constructor does not throw any exception when
	 * wildcard is used at the end of the fragments Monitorable_id and
	 * StatusVariable_id (target parameter)
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission003() {
		try {
			tbc.log("#testMonitorPermission003");
			new org.osgi.service.monitor.MonitorPermission(
					"com.mycomp.*/queue*",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the constructor does not throw any exception when
	 * wildcard is used at the end of the fragments Monitorable_id and
	 * StatusVariable_id (target parameter)
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission004() {
		try {
			tbc.log("#testMonitorPermission004");
			new org.osgi.service.monitor.MonitorPermission(
					"*/queue*", org.osgi.service.monitor.MonitorPermission.READ);

			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the constructor does not throw any exception when
	 * wildcard is used at the end of the fragment Monitorable_id (target
	 * parameter)
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission005() {
		try {
			tbc.log("#testMonitorPermission005");
			new org.osgi.service.monitor.MonitorPermission(
					"*/queue_length",
					org.osgi.service.monitor.MonitorPermission.READ);

			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if the constructor does not throw any exception when
	 * wildcard is used for Monitorable_id and StatusVariable_id fragments
	 * (target parameter)
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission006() {
		try {
			tbc.log("#testMonitorPermission006");
			new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.READ);

			tbc.pass("MonitorPermission object was created successfully");
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown when
	 * Monitorable_id fragment is invalid
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission007() {
		try {
			tbc.log("#testMonitorPermission007");
			new org.osgi.service.monitor.MonitorPermission(
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
	 * This method asserts if IllegalArgumentException is thrown when
	 * Monitorable_id fragment is invalid
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission008() {
		try {
			tbc.log("#testMonitorPermission008");
			new org.osgi.service.monitor.MonitorPermission(
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
	 * This method asserts if IllegalArgumentException is thrown when
	 * StatusVariable_id fragment is invalid
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission009() {
		try {
			tbc.log("#testMonitorPermission009");
			new org.osgi.service.monitor.MonitorPermission(
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
	 * This method asserts if IllegalArgumentException is thrown when
	 * StatusVariable_id fragment is invalid
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission010() {
		try {
			tbc.log("#testMonitorPermission010");
			new org.osgi.service.monitor.MonitorPermission(
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
	 * This method asserts if IllegalArgumentException is thrown when an invalid
	 * action is passed as parameter
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission011() {
		try {
			tbc.log("#testMonitorPermission011");
			new org.osgi.service.monitor.MonitorPermission(
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
	 * This method asserts if IllegalArgumentException is thrown when one of the
	 * actions passed as parameter is invalid
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission012() {
		try {
			tbc.log("#testMonitorPermission012");
			new org.osgi.service.monitor.MonitorPermission(
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
	 * This method asserts if a MonitorPermission is successfully created with
	 * an action defined in the following form: startjob:n
	 * 
	 * @spec MonitorPermission.MonitorPermission(String, String)
	 */
	private void testMonitorPermission013() {
		try {
			tbc.log("#testMonitorPermission013");
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.STARTJOB
							+ ":1");
			tbc
					.assertEquals(
							"Asserts if a MonitorPermission is successfully created with an action defined in the following form: startjob:n",
							org.osgi.service.monitor.MonitorPermission.STARTJOB
									+ ":1", mp.getActions());

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if getActions method returns the correct value as
	 * passed to the constructor
	 * 
	 * @spec MonitorPermission.getActions()
	 */
	private void testMonitorPermission014() {
		try {
			tbc.log("#testMonitorPermission014");
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.RESET);

			String actions = mp.getActions();

			tbc
					.assertEquals(
							"Asserts if getActions method returns the correct value as passed to the constructor",
							org.osgi.service.monitor.MonitorPermission.RESET,
							actions);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if getActions method returns the correct value as
	 * passed to the constructor
	 * 
	 * @spec MonitorPermission.getActions()
	 */
	private void testMonitorPermission015() {
		try {
			tbc.log("#testMonitorPermission015");
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.PUBLISH);

			String actions = mp.getActions();

			tbc
					.assertEquals(
							"Asserts if getActions method returns the correct value as passed to the constructor",
							org.osgi.service.monitor.MonitorPermission.PUBLISH,
							actions);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if getActions method returns the correct value as
	 * passed to the constructor
	 * 
	 * @spec MonitorPermission.getActions()
	 */
	private void testMonitorPermission016() {
		try {
			tbc.log("#testMonitorPermission016");
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*", org.osgi.service.monitor.MonitorPermission.STARTJOB);

			String actions = mp.getActions();

			tbc
					.assertEquals(
							"Asserts if getActions method returns the correct value as passed to the constructor",
							org.osgi.service.monitor.MonitorPermission.STARTJOB,
							actions);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if getActions method returns the correct value as
	 * passed to the constructor
	 * 
	 * @spec MonitorPermission.getActions()
	 */
	private void testMonitorPermission017() {
		try {
			tbc.log("#testMonitorPermission017");
			org.osgi.service.monitor.MonitorPermission mp = new org.osgi.service.monitor.MonitorPermission(
					"*/*",
					org.osgi.service.monitor.MonitorPermission.READ
							+ ","
							+ org.osgi.service.monitor.MonitorPermission.STARTJOB);

			StringTokenizer st = new StringTokenizer(mp.getActions(), ",");

			boolean isRead = false;
			boolean isStartJob = false;
			boolean isOther = false;

			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token
						.equals(org.osgi.service.monitor.MonitorPermission.READ)) {
					isRead = true;
				} else if (token
						.equals(org.osgi.service.monitor.MonitorPermission.STARTJOB)) {
					isStartJob = true;
				} else {
					isOther = true;
				}
			}

			tbc
					.assertTrue(
							"Asserts if getActions method returns the correct value as passed to the constructor",
							isRead && isStartJob && !isOther);
		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if getActions method returns the correct value as
	 * passed to the constructor
	 * 
	 * @spec MonitorPermission.getActions()
	 */
	private void testMonitorPermission018() {
		try {
			tbc.log("#testMonitorPermission018");
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
			boolean isOther = false;

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
				} else {
					isOther = true;
				}
			}

			tbc
					.assertTrue(
							"Asserts if getActions method returns the correct value as passed to the constructor",
							isRead && isStartJob && isReset && isPublish
									&& !isOther);

		} catch (Exception e) {
			tbc.fail(MessagesConstants.UNEXPECTED_EXCEPTION + ": "
					+ e.getClass().getName());
		}
	}

	/**
	 * This method asserts if IllegalArgumentException is thrown if
	 * statusVariable parameter is null
	 * 
	 * @spec MonitorPermission.MonitorPermission(String,String)
	 */
	private void testMonitorPermission019() {
		try {
			tbc.log("#testMonitorPermission019");
			new org.osgi.service.monitor.MonitorPermission(
					null, org.osgi.service.monitor.MonitorPermission.READ);

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
	 * This method asserts if IllegalArgumentException is thrown if
	 * actions parameter is null
	 * 
	 * @spec MonitorPermission.MonitorPermission(String,String)
	 */
	private void testMonitorPermission020() {
		try {
			tbc.log("#testMonitorPermission020");
			new org.osgi.service.monitor.MonitorPermission(
					"*/*", null);

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

}
