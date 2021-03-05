/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jul 12, 2005  Luiz Felipe Guimaraes
 * 118           Implement sendAlert
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.tc2.tb1.NotificationService;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.notification.AlertItem;
import org.osgi.service.dmt.notification.NotificationService;
import org.osgi.service.dmt.security.AlertPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.tc2.tbc.DmtConstants;
import org.osgi.test.cases.dmt.tc2.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.tc2.tbc.TestInterface;
import org.osgi.test.cases.dmt.tc2.tbc.Activators.DefaultRemoteAlertSenderActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Activators.DefaultRemoteAlertSenderImpl;
import org.osgi.test.cases.dmt.tc2.tbc.Activators.HighestRankingRemoteAlertSenderActivator;
import org.osgi.test.cases.dmt.tc2.tbc.Activators.HighestRankingRemoteAlertSenderImpl;
import org.osgi.test.cases.dmt.tc2.tbc.Activators.RemoteAlertSenderImpl;
import org.osgi.test.cases.dmt.tc2.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

import junit.framework.TestCase;

/**
 * @author Luiz Felipe Guimaraes
 * 
 *         This test case validates the implementation of
 *         <code>sendNotification</code> method of NotificationService,
 *         according to MEG specification
 */
public class SendNotification implements TestInterface {
	private DmtTestControl tbc;

	private NotificationService notificationService;
	
	public SendNotification(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	@Override
	public void run() {
		prepare();
		if (DmtConstants.SUPPORTS_ASYNCHRONOUS_NOTIFICATION) {
			testSendNotification001();
			testSendNotification002();
			testSendNotification003();
			testSendNotification004();
			testSendNotification005();
			testSendNotification006();
			testSendNotification007();
			testSendNotification008();
			testSendNotification009();
			testSendNotification010();
			testSendNotification011();
		} else {
			testSendNotificationFeatureNotSupported001();
		}
	}

	private void prepare() {
		tbc.setPermissions(new PermissionInfo(AlertPermission.class.getName(),
				"*", "*"));
	}

	private void sendNotification(String principal, int code, String correlator,
			AlertItem[] items) throws DmtException {
		if (notificationService == null) {
			BundleContext bc = tbc.getContext();
			ServiceReference<NotificationService> sr = bc
					.getServiceReference(NotificationService.class);
			notificationService = bc.getService(sr);
		}
		notificationService.sendNotification(principal, code, correlator, items);
	}
	
	/**
	 * It tests if the DmtAdmin.SendNotification really fowards the parameters
	 * to the RemoteAlertSender. It also tests that only the specified principal
	 * needs AlertPermission.
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification001() {
		try {
			DefaultTestBundleControl.log("#testSendNotification001");

			String mark = "mark";
			DmtData data = new DmtData("test");
			AlertItem item = new AlertItem(DmtConstants.OSGi_LOG,
					DmtConstants.MIMETYPE, mark, data);

			String mark2 = "mark2";
			DmtData data2 = new DmtData(10);
			String mimetype2 = "text/xml";
			AlertItem item2 = new AlertItem(TestExecPluginActivator.ROOT,
					mimetype2, mark2, data2);

			String correlator = "correlator";
			int code = 2;
			AlertItem[] items = new AlertItem[] { item, item2 };

			RemoteAlertSenderImpl.resetAlert();

			tbc.setPermissions(new PermissionInfo(AlertPermission.class
					.getName(), DmtConstants.PRINCIPAL, "*"));

			sendNotification(DmtConstants.PRINCIPAL,code,correlator,items);

			TestCase.assertTrue(
					"Asserts that the code sent by sendNotification was the expected",
					RemoteAlertSenderImpl.codeFound == code);
			TestCase.assertEquals(
					"Asserts that the correlator sent by sendNotification was the expected",
					correlator, RemoteAlertSenderImpl.correlatorFound);
			TestCase.assertEquals(
					"Asserts that the principal sent by sendNotification was the expected",
					DmtConstants.PRINCIPAL, RemoteAlertSenderImpl.serverIdFound);
			TestCase.assertEquals(
					"Asserts that the number of AlertItems sent by sendNotification was the expected",
					items.length, RemoteAlertSenderImpl.itemsFound.length);
			TestCase.assertEquals(
					"Asserts that the AlertItems sent by sendNotification were the expected",
					items[0].toString(),
					RemoteAlertSenderImpl.itemsFound[0].toString());
			TestCase.assertEquals(
					"Asserts that the AlertItems sent by sendNotification were the expected",
					items[1].toString(),
					RemoteAlertSenderImpl.itemsFound[1].toString());

		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			prepare();
		}
	}

	/**
	 * It tests if the DmtAdmin.sendNotification really fowards the parameters
	 * to the RemoteAlertSender, even if no principal is specified (when there
	 * is only one protocol adapter). It also validates that an empty array for
	 * alert items can be passed and that null can be passed as principal.
	 * Finally, it tests that if the principal parameter is null, the target of
	 * the AlertPermission must be "*".
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification002() {
		try {
			DefaultTestBundleControl.log("#testSendNotification002");

			int code = 10;
			String correlator = "newCorrelator";
			AlertItem[] items = new AlertItem[] {};

			RemoteAlertSenderImpl.resetAlert();
			sendNotification(null,code,correlator,items);

			TestCase.assertNull(
					"Asserts that if the DmtAdmin is connected to only one protocol adapter, the principal name can be omitted",
					RemoteAlertSenderImpl.serverIdFound);
			TestCase.assertTrue(
					"Asserts that the code sent by sendNotification was the expected",
					RemoteAlertSenderImpl.codeFound == code);
			TestCase.assertEquals(
					"Asserts that the correlator sent by sendNotification was the expected",
					correlator, RemoteAlertSenderImpl.correlatorFound);
			TestCase.assertEquals(
					"Asserts that the number of AlertItems sent by sendNotification was the expected",
					items.length, RemoteAlertSenderImpl.itemsFound.length);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		}
	}

	/**
	 * It tests if the correlator parameter can be null as the specified
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification003() {
		try {
			DefaultTestBundleControl.log("#testSendNotification003");

			String mark = "mark";
			DmtData data = new DmtData("test");
			AlertItem item = new AlertItem(DmtConstants.OSGi_LOG,
					DmtConstants.MIMETYPE, mark, data);

			AlertItem[] items = new AlertItem[] { item };
			int code = 0;
			RemoteAlertSenderImpl.resetAlert();
			sendNotification(DmtConstants.PRINCIPAL,code,null,items);

			TestCase.assertTrue(
					"Asserts that the code sent by sendNotification was the expected",
					RemoteAlertSenderImpl.codeFound == code);
			TestCase.assertNull(
					"Asserts that the correlator sent by sendNotification was the expected",
					RemoteAlertSenderImpl.correlatorFound);
			TestCase.assertEquals(
					"Asserts that the principal sent by sendNotification was the expected",
					DmtConstants.PRINCIPAL, RemoteAlertSenderImpl.serverIdFound);
			TestCase.assertEquals(
					"Asserts that the number of AlertItems sent by sendNotification was the expected",
					items.length, RemoteAlertSenderImpl.itemsFound.length);
			TestCase.assertEquals(
					"Asserts that the AlertItems sent by sendNotification were the expected",
					items[0].toString(),
					RemoteAlertSenderImpl.itemsFound[0].toString());

		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		}
	}

	/**
	 * It tests if the items parameter can be null as the specified
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification004() {
		try {
			DefaultTestBundleControl.log("#testSendNotification004");

			int code = 0;
			String correlator = "newCorrelator";
			RemoteAlertSenderImpl.resetAlert();

			sendNotification(DmtConstants.PRINCIPAL,code,correlator,null);

			TestCase.assertTrue(
					"Asserts that the code sent by sendNotification was the expected",
					RemoteAlertSenderImpl.codeFound == code);
			TestCase.assertEquals(
					"Asserts that the correlator sent by sendNotification was the expected",
					correlator, RemoteAlertSenderImpl.correlatorFound);
			TestCase.assertEquals(
					"Asserts that the principal sent by sendNotification was the expected",
					DmtConstants.PRINCIPAL, RemoteAlertSenderImpl.serverIdFound);
			TestCase.assertNull(
					"Asserts that the number of AlertItems sent by sendNotification was the expected",
					RemoteAlertSenderImpl.itemsFound);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		}
	}

	/**
	 * It tests if DmtException.ALERT_NOT_ROUTED is thrown when the alert can
	 * not be routed to the server
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification005() {
		try {
			DefaultTestBundleControl.log("#testSendNotification005");

			RemoteAlertSenderImpl.resetAlert();
			sendNotification(DmtConstants.PRINCIPAL_3,0,null,null);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals("Asserts that DmtException.ALERT_NOT_ROUTED is thrown when the alert can not be routed to the server",DmtException.ALERT_NOT_ROUTED,e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);

		}
	}

	/**
	 * It tests if any exception thrown on the RemoteAlertSender is propagated
	 * wrapped in a DmtException with the code REMOTE_ERROR
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification006() {
		try {
			DefaultTestBundleControl.log("#testSendNotification006");

			RemoteAlertSenderImpl.resetAlert();
			sendNotification(DmtConstants.PRINCIPAL,
					RemoteAlertSenderImpl.CODE_EXCEPTION, null, null);

			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			TestCase.assertEquals(
					"Asserts that any exception thrown on the RemoteAlertSender is propagated wrapped in a DmtException with the code REMOTE_ERROR",
					DmtException.REMOTE_ERROR, e.getCode());
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);

		}
	}

	/**
	 * If multiple Remote Alert Sender services register for the same service,
	 * then the service with the highest value for the service.ranking property
	 * must be used.
	 * 
	 * @spec 117.8.1 Routing Alerts
	 */
	private void testSendNotification007() {
		HighestRankingRemoteAlertSenderActivator highestRankingRemoteAlertSenderActivator = null;
		try {
			DefaultTestBundleControl.log("#testSendNotification007");
			tbc.setPermissions(new PermissionInfo[] {
					new PermissionInfo(ServicePermission.class.getName(), "*",
							ServicePermission.REGISTER),
					new PermissionInfo(AlertPermission.class.getName(), "*",
							"*") });

			// Installs the highest ranking remote alert sender
			highestRankingRemoteAlertSenderActivator = new HighestRankingRemoteAlertSenderActivator();
			highestRankingRemoteAlertSenderActivator.start(tbc.getContext());

			HighestRankingRemoteAlertSenderImpl.resetAlert();

			int codeExpected = 21;

			sendNotification(DmtConstants.PRINCIPAL, codeExpected, null, null);

			TestCase.assertEquals(
					"Asserts that if multiple Remote Alert Sender services register for the same service,"
							+ " then the service with the highest value for the service.ranking property must be used.",
					codeExpected, HighestRankingRemoteAlertSenderImpl.codeFound);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			try {
				highestRankingRemoteAlertSenderActivator.stop(tbc.getContext());
			} catch (Exception e1) {
				DefaultTestBundleControl.log("#Failed uninstalling the HighestRankingRemoteAlertSender");
			}
			prepare();
		}
	}

	/**
	 * Asserts that if 'principals' property is not registered, the Remote Alert
	 * Sender service is treated as the default sender.
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification008() {
		DefaultRemoteAlertSenderActivator defaultRemoteAlertSenderActivator = null;
		try {
			DefaultTestBundleControl.log("#testSendNotification008");
			tbc.setPermissions(new PermissionInfo[] {
					new PermissionInfo(ServicePermission.class.getName(), "*",
							ServicePermission.REGISTER),
					new PermissionInfo(AlertPermission.class.getName(), "*",
							"*") });
			// Installs a default alert sender (it doesnt specify the principal
			// in its registration)
			defaultRemoteAlertSenderActivator = new DefaultRemoteAlertSenderActivator();
			defaultRemoteAlertSenderActivator.start(tbc.getContext());

			DefaultRemoteAlertSenderImpl.resetAlert();

			int codeExpected = 11;
			sendNotification(null, codeExpected, null, null);

			TestCase.assertEquals(
					"Asserts that if 'servers' property is not registered, the Remote Alert Sender service is treated as the default sender.",
					codeExpected, DefaultRemoteAlertSenderImpl.codeFound);
		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		} finally {
			try {
				defaultRemoteAlertSenderActivator.stop(tbc.getContext());
			} catch (Exception e) {
				DefaultTestBundleControl.log("#Failed stopping the default remote alert sender");
			}
			prepare();
		}
	}

	/**
	 * It tests if the SecurityException is thrown when there is no
	 * AlertPermission for that principal
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification009() {
		try {
			DefaultTestBundleControl.log("#testSendNotification009");
			tbc.setPermissions(new PermissionInfo[0]);

			sendNotification(DmtConstants.PRINCIPAL, 1, "", new AlertItem[0]);
			DefaultTestBundleControl.failException("", SecurityException.class);
		} catch (SecurityException e) {
			DefaultTestBundleControl.pass("SecurityException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(SecurityException.class, e);
		} finally {
			prepare();
		}
	}

	/**
	 * It tests if the DmtAdmin.SendNotification can foward the alert for many
	 * principals (when more than one principal is registered in the respective
	 * RemoteAlertSender)
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification010() {
		try {
			DefaultTestBundleControl.log("#testSendNotification010");

			int code = 11;
			String correlator = "correlator2";
			AlertItem[] items = new AlertItem[] { new AlertItem((String) null,
					null, null, null) };

			RemoteAlertSenderImpl.resetAlert();
			sendNotification(DmtConstants.PRINCIPAL_2,code,correlator,items);
			
			TestCase.assertEquals(
					"Asserts that the RemoteAlertSender can have more than one principal",
					DmtConstants.PRINCIPAL_2,
					RemoteAlertSenderImpl.serverIdFound);
			TestCase.assertTrue(
					"Asserts that the code sent by sendNotification was the expected",
					RemoteAlertSenderImpl.codeFound == code);
			TestCase.assertEquals(
					"Asserts that the correlator sent by sendNotification was the expected",
					correlator, RemoteAlertSenderImpl.correlatorFound);
			TestCase.assertEquals(
					"Asserts that the number of AlertItems sent by sendNotification was the expected",
					items.length, RemoteAlertSenderImpl.itemsFound.length);

		} catch (Exception e) {
			tbc.failUnexpectedException(e);

		}
	}

	/**
	 * Asserts that if the principal parameter is null and the target of the
	 * AlertPermission is not "*" SecurityException is thrown.
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotification011() {
		try {
			DefaultTestBundleControl.log("#testSendNotification011");
			tbc.setPermissions(new PermissionInfo[0]);

			sendNotification(null, 1, "", new AlertItem[0]);
			DefaultTestBundleControl.failException("", SecurityException.class);
		} catch (SecurityException e) {
			DefaultTestBundleControl.pass("SecurityException correctly thrown");
		} catch (Exception e) {
			tbc.failExpectedOtherException(SecurityException.class, e);
		} finally {
			prepare();
		}
	}

	/**
	 * Asserts that if the underlying management protocol doesn't support
	 * asynchronous notifications, DmtException.FEATURE_NOT_SUPPORTED is thrown
	 * 
	 * @spec NotificationService.sendNotification(String,int,String,AlertItem[])
	 */
	private void testSendNotificationFeatureNotSupported001() {
		try {
			DefaultTestBundleControl
					.log("#testSendNotificationFeatureNotSupported001");
			sendNotification(null, 0, null, null);
			DefaultTestBundleControl.failException("", DmtException.class);
		} catch (DmtException e) {
			DefaultTestBundleControl.pass("DmtException.FEATURE_NOT_SUPPORTED correctly thrown because the management "
					+ "protocol doesn't support asynchronous notifications");
		} catch (Exception e) {
			tbc.failExpectedOtherException(DmtException.class, e);

		}
	}
}
