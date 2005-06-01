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
 * 04/03/2005   Leonardo Barros
 * CR 33        Implement MEG TCK
 * ===========  ==============================================================
 * 06/03/2005   Eduardo Oliveira
 * CR 33        Implement MEG TCK
 */
package org.osgi.test.cases.policy.tbc;

import java.security.MessageDigest;

import org.osgi.framework.Bundle;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.test.cases.policy.tbc.TransferCostCondition.ResetTransferCost;
import org.osgi.test.cases.policy.tbc.TransferCostCondition.SetTransferCost;
import org.osgi.test.cases.policy.tbc.TransferCostCondition.TransferCostConditionConstants;
import org.osgi.test.cases.policy.tbc.util.Base64Encoder;
import org.osgi.test.cases.policy.tbc.util.TestBundle;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * Controls the execution of the test case
 */
public class PolicyTestControl extends DefaultTestBundleControl {
	
	public static final String IMEI_PHONE_CODE = System
			.getProperty("org.osgi.util.gsm.imei");


	public static final String IMSI_PHONE_CODE = System
			.getProperty("org.osgi.util.gsm.imsi");

	
	public static final String IMEI_VALID_CODE = "012345678912345";

	public static final String IMEI_CHAR_CODE = "abcdefghijklmno";

	public static final String IMEI_LESS_DIGIT_CODE = "12345";
	
	public static final String IMEI_MORE_DIGIT_CODE = "1234567890123456";
	
	public static final String IMSI_VALID_CODE = "012345678912345";
	
	public static final String IMSI_CHAR_CODE = "abcdefghijklmno";

	public static final String IMSI_LESS_DIGIT_CODE = "12345";
	
	public static final String IMSI_MORE_DIGIT_CODE = "1234567890123456";

	public static final String INVALID_CODE = "@#$%sA!&_";

	public static final String PRINCIPAL = "www.cesar.org.br";

	public static final String PRINCIPAL_HASH = "orcrg8rpqoT4sm1pCeZDObtaZGI";

	public static final String PRINCIPAL_DMT = "./OSGi/Policies/Java/DmtPrincipal";
	
	public static final String CONDITION_HASH = "Egtd5i+S33Y94dHent1bFdlb_ak";
	
	public static final String CONDITION_DMT = "./OSGi/Policies/Java/ConditionalPermission";
	
	public static final String PERMISSION_DMT = "./OSGi/Policies/Java/Bundle";
	
	public static final String DEFAULT_DMT = "./OSGi/Policies/Java/Bundle/Default";
	
	public static final String PERMISSION_INFO = "(org.osgi.framework.AdminPermission \"*\" \"*\")\n";
	
	public static final String PERMISSION_DEFAULT = "(org.osgi.framework.AdminPermission \"*\" \"GET\")\n";
	
	public static final Bundle TEST_BUNDLE = new TestBundle();
	
	public static final String INVALID_COST_LIMIT = "TEST";
	
	public static final String CATALOG_NAME = "com.provider.messages.userprompt";
	
	private DmtSession session;
	
	private ConditionalPermissionAdmin cpa;

	private PermissionAdmin pa;
	
	private DmtAdmin da;

	private Bundle bundle;
	
	public void prepare() {
		cpa = (ConditionalPermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						ConditionalPermissionAdmin.class.getName()));

		pa = (PermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						PermissionAdmin.class.getName()));

		da = (DmtAdmin) getContext().getService(
				getContext().getServiceReference(DmtAdmin.class.getName()));
        
        bundle = new TestBundle();
		
	}

	/*
	 * Calls IMSICondition.getCondition test methods
	 */
	public void testIMSIConditionGetCondition() {
		new org.osgi.test.cases.policy.tbc.IMSICondition.GetCondition(this)
				.run();
	}

	
	/*
	 * Calls IMEICondition.getCondition test methods
	 */
	public void testIMEIConditionGetCondition() {
		new org.osgi.test.cases.policy.tbc.IMEICondition.GetCondition(this)
				.run();
	}

	/*
	 * Calls TransferCostCondition's constants test method
	 */
	public void testTransferCostConditionConstants() {
		new TransferCostConditionConstants(this).run();
	}

	/*
	 * Calls TransferCostCondition.getCondition test methods
	 */
	public void testTransferCostConditionGetCondition() {
		new org.osgi.test.cases.policy.tbc.TransferCostCondition.GetCondition(
				this).run();
	}

	/*
	 * Calls TransferCostCondition.isMutable test methods
	 */
	public void testTransferCostConditionIsMutable() {
		new org.osgi.test.cases.policy.tbc.TransferCostCondition.IsMutable(this)
				.run();
	}

	/*
	 * Calls TransferCostCondition.isSatisfied test methods
	 */
	public void testTransferCostConditionIsSatisfied() {
		new org.osgi.test.cases.policy.tbc.TransferCostCondition.IsSatisfied(
				this).run();
	}
	
	/*
	 * Calls TransferCostCondition.resetTransferCost test methods
	 */
	public void testTransferCostConditionResetTransferCost() {
		new ResetTransferCost(this).run();
	}

	/*
	 * Calls TransferCostCondition.SetTransferCost test methods
	 */
	public void testTransferCostConditionSetTransferCost() {
		new SetTransferCost(this).run();
	}
	
	public ConditionalPermissionAdmin getConditionalPermissionAdmin() {
		return cpa;
	}

	public PermissionAdmin getPermissionAdmin() {
		return pa;
	}

	public DmtAdmin getDmtAdmin() {
		return da;
	}
	
	public String getHash(String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] b = md.digest(str.getBytes("UTF-8"));
		String temp = Base64Encoder.encode(b);
		temp = temp.replace('=', '\0');
		return temp.replace('/', '_');
	}

	public Bundle getBundle() {
		if (bundle == null)
			throw new NullPointerException("null Bundle.");
		return bundle;
	}


	public DmtSession getSession(String nodeURI, int lockMode) {
		
		if (nodeURI != null) {
			try {
				if (session != null && session.getState() != DmtSession.STATE_CLOSED ) {
					session.close();
				}
				session = da.getSession(nodeURI, lockMode);
			} catch (DmtException e) {
				log("TestControl: Fail to get the session");
			} catch (IllegalStateException e) {
				try {
					session = da.getSession(nodeURI, lockMode);
				} catch (DmtException e1) {
					log("TestControl: Fail to get the session");
				}
				return session;
			} catch (Exception e) {
				log("TestControl: Unexpected Exception "
						+ e.getClass().getName());
			}
			return session;
		} else {
			throw new IllegalArgumentException("node URI is null or invalid.");
		}
	}
	
}
