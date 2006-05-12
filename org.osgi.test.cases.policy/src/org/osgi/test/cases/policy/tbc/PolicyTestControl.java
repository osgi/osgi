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

import info.dmtree.DmtAdmin;
import info.dmtree.DmtException;
import info.dmtree.DmtSession;
import info.dmtree.security.DmtPermission;

import java.security.AllPermission;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.policy.tbc.UserPromptCondition.IsMutable;
import org.osgi.test.cases.policy.tbc.UserPromptCondition.IsPostponed;
import org.osgi.test.cases.policy.tbc.UserPromptCondition.IsSatisfied;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * Controls the execution of the test case
 */
public class PolicyTestControl extends DefaultTestBundleControl {
	
	private TB1Service tb1Service;

	private TestInterface[] testBundleTB1;

	private ConditionalPermissionAdmin cpa;

	private PermissionAdmin pa;
	
	private DmtAdmin da;

	private Bundle bundle;
	
	private Bundle providerMessages;
	
	public void prepare() {
		cpa = (ConditionalPermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						ConditionalPermissionAdmin.class.getName()));

		pa = (PermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						PermissionAdmin.class.getName()));

		da = (DmtAdmin) getContext().getService(
				getContext().getServiceReference(DmtAdmin.class.getName()));
        
        bundle = this.getContext().getBundle();
        
        try {
			providerMessages = installBundle("com.provider.messages.jar");
		} catch (Exception e) {
			log("#the installation of com.provider.messages.jar bundle has failed.");
		}
        
        installBundle();		
	}
	
	/**
	 * override getMethods()
	 *
	 */
	public String[] getMethods() {
		String value = System.getProperty("org.osgi.test.cases.policy.automatic");
		if (value != null && value.equals("false")) {
			return new String[] {
					"testIMEIConditionGetCondition",
					"testIMSIConditionGetCondition",
					"testIsMutable",
					"testIsPostponed",
					"testIsSatisfied",
					"testMetaNode",
					"testTreeStructure",
					"testUserPromptConditionGetCondition"			
			};
		} else {
			return new String[] {
					"testIMEIConditionGetCondition",
					"testIMSIConditionGetCondition",
					"testMetaNode",
					"testTreeStructure",
					"testUserPromptConditionGetCondition"		
				};
		}
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
	 * Calls UserPromptCondition.getCondition test methods
	 */
	public void testUserPromptConditionGetCondition() {
		new org.osgi.test.cases.policy.tbc.UserPromptCondition.GetCondition(
				this).run();
	}

	/*
	 * Executes test methods for tree structure
	 */
	public void testTreeStructure() {
		testBundleTB1[0].run();
	}
    
    /*
     * Executes test methods for tree structure meta nodes
     */
    public void testMetaNode() {
        testBundleTB1[1].run();
    }
    
    
    /**
     * Executes test methods for isSatisfied 
     */
    public void testIsSatisfied() {
    	new IsSatisfied(this).run();
    }
    
    /**
     * Executes test methods for isMutable 
     */
    public void testIsMutable() {
    	new IsMutable(this).run();
    }    
    
    /**
     * Executes test methods for isPostponed 
     */
    public void testIsPostponed() {
    	new IsPostponed(this).run();
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
	
	public Bundle getBundle() {
		if (bundle == null)
			throw new NullPointerException("null Bundle.");
		return bundle;
	}

	private void installBundle() {
		try {
			installBundle("tb1.jar");
		} catch (Exception e) {
			log("TestControl: Failed installing a bundle");
		}
		ServiceReference tb1SvrReference = getContext().getServiceReference(
				TB1Service.class.getName());
		PolicyConstants.LOCATION = tb1SvrReference.getBundle().getLocation();
		tb1Service = (TB1Service) getContext().getService(tb1SvrReference);
		testBundleTB1 = tb1Service.getTestClasses(this);
		new DmtPermission(".",DmtPermission.ADD);

		setPermissions(new PermissionInfo(DmtPermission.class.getName(),
				PolicyConstants.ALL_NODES, PolicyConstants.ALL_ACTIONS));
		
	}


	public void setPermissions(PermissionInfo permission) {
		new TopicPermission("*",TopicPermission.PUBLISH);
		pa.setPermissions(PolicyConstants.LOCATION,
				new PermissionInfo[] {
						new PermissionInfo(TopicPermission.class.getName(),
								"org/osgi/service/dmt/ADDED",
								TopicPermission.PUBLISH),
						new PermissionInfo(TopicPermission.class.getName(),
								"org/osgi/service/dmt/REPLACED",
								TopicPermission.PUBLISH),
						new PermissionInfo(TopicPermission.class.getName(),
								"org/osgi/service/dmt/DELETED",
								TopicPermission.PUBLISH),
						new PermissionInfo(TopicPermission.class.getName(),
								"org/osgi/service/dmt/RENAMED",
								TopicPermission.PUBLISH),
						new PermissionInfo(TopicPermission.class.getName(),
								"org/osgi/service/dmt/COPIED",
								TopicPermission.PUBLISH),
						new PermissionInfo(PackagePermission.class.getName(),
								"*", "EXPORT, IMPORT"),
						new PermissionInfo(ServicePermission.class.getName(),
								"*", "GET"),
						new PermissionInfo(AdminPermission.class.getName(),
								"*", "*"), permission , new PermissionInfo(AllPermission.class.getName(),"*","*")});
	}

	public void closeSession(DmtSession session) {
		if (null != session) {
			if (session.getState() == DmtSession.STATE_OPEN) {
				try {
					session.close();
				} catch (DmtException e) {
					log("#Exception closing the session: "
							+ e.getClass().getName() + "Message: ["
							+ e.getMessage() + "]");
				}
			}
		}
	}

	public void cleanUp(DmtSession session, String[] nodeUri) {
		if (session != null && session.getState() == DmtSession.STATE_OPEN) {
			if (nodeUri == null) {
				closeSession(session);
			} else {
				for (int i = 0; i < nodeUri.length; i++) {
					try {
						session.deleteNode(nodeUri[i]);
					} catch (Throwable e) {
						log("#Exception at cleanUp: " + e.getClass().getName()
								+ " [Message: " + e.getMessage() + "]");
					}
				}
				closeSession(session);
			}
		}
	}

	
	public void unprepare() {
		try {
			providerMessages.stop();
			providerMessages.uninstall();
		} catch (BundleException e) {
			log("#error uninstalling the com.provider.messages bundle");
		}
		
	}
}
