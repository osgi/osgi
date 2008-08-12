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
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * Jul 12, 2005  Luiz Felipe Guimaraes
 * 118           Implement sendAlert
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tb1.DmtAdmin;

import org.osgi.framework.ServicePermission;
import org.osgi.service.dmt.AlertItem;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.security.AlertPermission;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtTestControl;
import org.osgi.test.cases.dmt.main.tbc.TestInterface;
import org.osgi.test.cases.dmt.main.tbc.Activators.DefaultRemoteAlertSenderActivator;
import org.osgi.test.cases.dmt.main.tbc.Activators.DefaultRemoteAlertSenderImpl;
import org.osgi.test.cases.dmt.main.tbc.Activators.HighestRankingRemoteAlertSenderActivator;
import org.osgi.test.cases.dmt.main.tbc.Activators.HighestRankingRemoteAlertSenderImpl;
import org.osgi.test.cases.dmt.main.tbc.Activators.RemoteAlertSenderImpl;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;

/**
 * @author Luiz Felipe Guimaraes
 * 
 * This test case validates the implementation of <code>sendAlert</code> method of DmtAdmin, 
 * according to MEG specification
 */
public class SendAlert implements TestInterface {
	private DmtTestControl tbc;

	public SendAlert(DmtTestControl tbc) {
		this.tbc = tbc;
	}

	public void run() {
        prepare();
		testSendAlert001();
		testSendAlert002();
		testSendAlert003();
		testSendAlert004();
		testSendAlert005();
		testSendAlert006();
		testSendAlert007();
        testSendAlert008();
        testSendAlert009();
        testSendAlert010();
        testSendAlert011();
	}
    private void prepare() {
        tbc.setPermissions(new PermissionInfo(AlertPermission.class.getName(), "*", "*"));
    }
	/**
	 * It tests if the DmtAdmin.sendAlert really fowards the parameters to the RemoteAlertSender.
	 * It also tests that only the specified principal needs AlertPermission.
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[]) 
	 */
	private void testSendAlert001() {
		try {
			tbc.log("#testSendAlert001");
			
			String mark = "mark";
			DmtData data = new DmtData("test");
			AlertItem item = new AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,mark,data);
			
			String mark2 = "mark2";
			DmtData data2 = new DmtData(10);
			String mimetype2 = "text/xml";
			AlertItem item2 = new AlertItem(TestExecPluginActivator.ROOT,mimetype2,mark2,data2);
			
			String correlator = "correlator";
			int code = 2;
			AlertItem[] items = new AlertItem[] { item, item2 };
			
			RemoteAlertSenderImpl.resetAlert();
			
			tbc.setPermissions(new PermissionInfo(AlertPermission.class.getName(), DmtConstants.PRINCIPAL, "*"));
			
			tbc.getDmtAdmin().sendAlert(DmtConstants.PRINCIPAL,code,correlator,items);
			
			tbc.assertTrue("Asserts that the code sent by sendAlert was the expected",RemoteAlertSenderImpl.codeFound == code);
			tbc.assertEquals("Asserts that the correlator sent by sendAlert was the expected",correlator,RemoteAlertSenderImpl.correlatorFound);
			tbc.assertEquals("Asserts that the principal sent by sendAlert was the expected",DmtConstants.PRINCIPAL,RemoteAlertSenderImpl.serverIdFound);
			tbc.assertEquals("Asserts that the number of AlertItems sent by sendAlert was the expected",items.length,RemoteAlertSenderImpl.itemsFound.length);
			tbc.assertEquals("Asserts that the AlertItems sent by sendAlert were the expected",items[0].toString(),RemoteAlertSenderImpl.itemsFound[0].toString());
			tbc.assertEquals("Asserts that the AlertItems sent by sendAlert were the expected",items[1].toString(),RemoteAlertSenderImpl.itemsFound[1].toString());
					
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			prepare();
		}
	}
	
	/**
	 * It tests if the DmtAdmin.sendAlert really fowards the parameters to the RemoteAlertSender,
	 * even if no principal is specified (when there is only one protocol adapter). It also validates that
     * an empty array for alert items can be passed and that null can be passed as principal. Finally, it
     * tests that if the principal parameter is null, the target of the AlertPermission must be "*". 
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[])
	 */
	private void testSendAlert002() {
		try {
			tbc.log("#testSendAlert002");
			
			int code = 10;
			String correlator = "newCorrelator";
			AlertItem[] items = new AlertItem[] {};

			RemoteAlertSenderImpl.resetAlert();
			tbc.getDmtAdmin().sendAlert(null,code,correlator,items);
			

			tbc.assertNull("Asserts that if the DmtAdmin is connected to only one protocol adapter, the principal name can be omitted",RemoteAlertSenderImpl.serverIdFound);
			tbc.assertTrue("Asserts that the code sent by sendAlert was the expected",RemoteAlertSenderImpl.codeFound == code);
			tbc.assertEquals("Asserts that the correlator sent by sendAlert was the expected",correlator,RemoteAlertSenderImpl.correlatorFound);
			tbc.assertEquals("Asserts that the number of AlertItems sent by sendAlert was the expected",items.length,RemoteAlertSenderImpl.itemsFound.length);
					
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		}
	}
	
	/**
	 * It tests if the correlator parameter can be null as the specified
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[])
	 */
	private void testSendAlert003() {
		try {
			tbc.log("#testSendAlert003");
            
            String mark = "mark";
            DmtData data = new DmtData("test");
            AlertItem item = new AlertItem(DmtConstants.OSGi_LOG,DmtConstants.MIMETYPE,mark,data);
            
            AlertItem[] items = new AlertItem[] { item };
			int code = 0;
			RemoteAlertSenderImpl.resetAlert();
            tbc.getDmtAdmin().sendAlert(DmtConstants.PRINCIPAL,code,null,items);
            
            tbc.assertTrue("Asserts that the code sent by sendAlert was the expected",RemoteAlertSenderImpl.codeFound == code);
            tbc.assertNull("Asserts that the correlator sent by sendAlert was the expected",RemoteAlertSenderImpl.correlatorFound);
            tbc.assertEquals("Asserts that the principal sent by sendAlert was the expected",DmtConstants.PRINCIPAL,RemoteAlertSenderImpl.serverIdFound);
            tbc.assertEquals("Asserts that the number of AlertItems sent by sendAlert was the expected",items.length,RemoteAlertSenderImpl.itemsFound.length);
            tbc.assertEquals("Asserts that the AlertItems sent by sendAlert were the expected",items[0].toString(),RemoteAlertSenderImpl.itemsFound[0].toString());
            
			

					
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		}
	}
    
    /**
     * It tests if the items parameter can be null as the specified
     * 
     * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[])
     */
    private void testSendAlert004() {
        try {
            tbc.log("#testSendAlert004");
            
            int code = 0;
            String correlator = "newCorrelator";
            RemoteAlertSenderImpl.resetAlert();
            tbc.getDmtAdmin().sendAlert(DmtConstants.PRINCIPAL,code,correlator,null);
            
            tbc.assertTrue("Asserts that the code sent by sendAlert was the expected",RemoteAlertSenderImpl.codeFound == code);
            tbc.assertEquals("Asserts that the correlator sent by sendAlert was the expected",correlator,RemoteAlertSenderImpl.correlatorFound);
            tbc.assertEquals("Asserts that the principal sent by sendAlert was the expected",DmtConstants.PRINCIPAL,RemoteAlertSenderImpl.serverIdFound);
            tbc.assertNull("Asserts that the number of AlertItems sent by sendAlert was the expected",RemoteAlertSenderImpl.itemsFound);

                    
        } catch (Exception e) {
            tbc.fail("Unexpected Exception: " + e.getClass().getName()
                    + " [Message: " + e.getMessage() + "]");

        }
    }
	/**
	 * It tests if DmtException.ALERT_NOT_ROUTED is thrown when the alert can not be routed to the server
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[]) 
	 */
	private void testSendAlert005() {
		try {
			tbc.log("#testSendAlert005");
			
			RemoteAlertSenderImpl.resetAlert();
			tbc.getDmtAdmin().sendAlert(DmtConstants.PRINCIPAL_3,0,null,null);

			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that DmtException.ALERT_NOT_ROUTED is thrown when the alert can not be routed to the server",DmtException.ALERT_NOT_ROUTED,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		}
	}
	/**
	 * It tests if any exception thrown on the RemoteAlertSender is propagated wrapped in a DmtException with the code REMOTE_ERROR
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[]) 
	 */
	private void testSendAlert006() {
		try {
			tbc.log("#testSendAlert006");
			
			RemoteAlertSenderImpl.resetAlert();
			tbc.getDmtAdmin().sendAlert(DmtConstants.PRINCIPAL,RemoteAlertSenderImpl.CODE_EXCEPTION,null,null);

			tbc.failException("",DmtException.class);
		} catch (DmtException e) {
			tbc.assertEquals("Asserts that any exception thrown on the RemoteAlertSender is propagated wrapped in a DmtException with the code REMOTE_ERROR",DmtException.REMOTE_ERROR,e.getCode());
		} catch (Exception e) {
			tbc.fail("Expected " + DmtException.class.getName() + " but was "
					+ e.getClass().getName());

		}
	}
	
	/**
	 * If multiple Remote Alert Sender services register for the same service, 
	 * then the service with the highest value for the service.ranking property must be used.
	 * 
	 * @spec 117.8.1 Routing Alerts
	 */
	private void testSendAlert007() {
		HighestRankingRemoteAlertSenderActivator highestRankingRemoteAlertSenderActivator=null;
		try {
			tbc.log("#testSendAlert007");
			tbc.setPermissions(new PermissionInfo[] { 
					new PermissionInfo(ServicePermission.class.getName(),"*",ServicePermission.REGISTER),
					new PermissionInfo(AlertPermission.class.getName(), "*", "*") });
			
			//Installs the highest ranking remote alert sender 
			highestRankingRemoteAlertSenderActivator = new HighestRankingRemoteAlertSenderActivator();
			highestRankingRemoteAlertSenderActivator.start(tbc.getContext());
			
			HighestRankingRemoteAlertSenderImpl.resetAlert();

			int codeExpected = 21;
			tbc.getDmtAdmin().sendAlert(DmtConstants.PRINCIPAL,codeExpected,null,null);

			tbc.assertEquals("Asserts that if multiple Remote Alert Sender services register for the same service," +
					" then the service with the highest value for the service.ranking property must be used.",
					codeExpected,HighestRankingRemoteAlertSenderImpl.codeFound);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			try {
				highestRankingRemoteAlertSenderActivator.stop(tbc.getContext());
			} catch (Exception e1) {
				tbc.log("#Failed uninstalling the HighestRankingRemoteAlertSender");
			}
			prepare();
		}
	}	
	/**
	 * Asserts that if 'principals' property is not registered, the Remote Alert Sender service is treated 
	 * as the default sender.
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[]) 
	 */
	private void testSendAlert008() {
		DefaultRemoteAlertSenderActivator defaultRemoteAlertSenderActivator = null;
		try {
			tbc.log("#testSendAlert008");
			tbc.setPermissions(new PermissionInfo[] { 
					new PermissionInfo(ServicePermission.class.getName(),"*",ServicePermission.REGISTER),
					new PermissionInfo(AlertPermission.class.getName(), "*", "*") });
			// Installs a default alert sender (it doesnt specify the principal in its registration)
			defaultRemoteAlertSenderActivator = new DefaultRemoteAlertSenderActivator();
			defaultRemoteAlertSenderActivator.start(tbc.getContext());	

			DefaultRemoteAlertSenderImpl.resetAlert();

			int codeExpected = 11;
			tbc.getDmtAdmin().sendAlert(null,codeExpected,null,null);

			tbc.assertEquals("Asserts that if 'servers' property is not registered, the Remote Alert Sender service is treated as the default sender.",
					codeExpected,DefaultRemoteAlertSenderImpl.codeFound);
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		} finally {
			try {
				defaultRemoteAlertSenderActivator.stop(tbc.getContext());	
			} catch (Exception e) {
				tbc.log("#Failed stopping the default remote alert sender");
			}
			prepare();
		}
	}
	/**
	 * It tests if the SecurityException is thrown when there is no AlertPermission for that principal 
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[])
	 */
	private void testSendAlert009() {
		try {
			tbc.log("#testSendAlert009");
			tbc.setPermissions(new PermissionInfo[0]);
			
			tbc.getDmtAdmin().sendAlert(DmtConstants.PRINCIPAL,1,"",new AlertItem[0]);
			tbc.failException("", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("SecurityException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			prepare();
		}
	}
	
	/**
	 * It tests if the DmtAdmin.sendAlert can foward the alert for many principals 
	 * (when more than one principal is registered in the respective RemoteAlertSender)
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[])
	 */
	private void testSendAlert010() {
		try {
			tbc.log("#testSendAlert010");
			
			int code = 11;
			String correlator = "correlator2";
			AlertItem[] items = new AlertItem[] { new AlertItem((String)null,null,null,null) };

			RemoteAlertSenderImpl.resetAlert();
			tbc.getDmtAdmin().sendAlert(DmtConstants.PRINCIPAL_2,code,correlator,items);
			

			tbc.assertEquals("Asserts that the RemoteAlertSender can have more than one principal",DmtConstants.PRINCIPAL_2,RemoteAlertSenderImpl.serverIdFound);
			tbc.assertTrue("Asserts that the code sent by sendAlert was the expected",RemoteAlertSenderImpl.codeFound == code);
			tbc.assertEquals("Asserts that the correlator sent by sendAlert was the expected",correlator,RemoteAlertSenderImpl.correlatorFound);
			tbc.assertEquals("Asserts that the number of AlertItems sent by sendAlert was the expected",items.length,RemoteAlertSenderImpl.itemsFound.length);
					
		} catch (Exception e) {
			tbc.fail("Unexpected Exception: " + e.getClass().getName()
					+ " [Message: " + e.getMessage() + "]");

		}
	}
	
	/**
	 * Asserts that if the principal parameter is null and the target of the AlertPermission is not "*"
	 * SecurityException is thrown. 
	 * 
	 * @spec DmtAdmin.sendAlert(String,int,String,AlertItem[])
	 */
	private void testSendAlert011() {
		try {
			tbc.log("#testSendAlert011");
			tbc.setPermissions(new PermissionInfo[0]);

			tbc.getDmtAdmin().sendAlert(null,1,"",new AlertItem[0]);
			tbc.failException("", SecurityException.class);
		} catch (SecurityException e) {
			tbc.pass("SecurityException correctly thrown");
		} catch (Exception e) {
			tbc.fail("Expected " + SecurityException.class.getName() + " but was "
					+ e.getClass().getName());
		} finally {
			prepare();
		}
	}
}
