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
 * Jan 21, 2005  Andre Assad
 * 1             Implement MEG TCK
 * ============  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ============  ==============================================================
 * Mar 02, 2005  Andre Assad
 * 11            Implement DMT Use Cases 
 * ===========   ==============================================================
 * Mar 04, 2005  Alexandre Santos
 * 23            Updates due to changes in the DmtAcl API
 * ===========   ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtPermission;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.DmtAcl.AddPermission;
import org.osgi.test.cases.dmt.main.tbc.DmtAcl.Clone;
import org.osgi.test.cases.dmt.main.tbc.DmtAcl.DeletePermission;
import org.osgi.test.cases.dmt.main.tbc.DmtAcl.DmtAclConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtAcl.Hashcode;
import org.osgi.test.cases.dmt.main.tbc.DmtAcl.IsPermitted;
import org.osgi.test.cases.dmt.main.tbc.DmtAcl.SetPermission;
import org.osgi.test.cases.dmt.main.tbc.DmtData.DmtData;
import org.osgi.test.cases.dmt.main.tbc.DmtData.DmtDataConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtData.Equals;
import org.osgi.test.cases.dmt.main.tbc.DmtException.DmtExceptionConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtException.PrintStackTrace;
import org.osgi.test.cases.dmt.main.tbc.DmtPrincipalPermission.DmtPrincipalPermission;
import org.osgi.test.cases.dmt.main.tbc.Plugin.TestExecPluginActivator;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class DmtTestControl extends DefaultTestBundleControl {

	public static final String OSGi_ROOT = "./OSGi";

	public static final String OSGi_LOG = OSGi_ROOT + "/log";
	
	public static final String OSGi_CFG = OSGi_ROOT + "/cfg";

	public static final String INVALID = "%&@#&$#";
	
	public static final String LONG_NAME = "sdsdqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfgzcvddddddsd";
	
	public static final String INVALID_NODE = OSGi_LOG + "/invalid";
	
	public static final String ACLSTR = "Add=*&Delete=*&Replace=*&Get=*";

	public static final String ACL_CESAR = "Add=www.cesar.org.br&Delete=www.cesar.org.br&Get=*";

	public static final String TITLE = "Title";

	public static final String INVALID_URI = OSGi_LOG + "/" + INVALID;

	public static final String URI_LONG = OSGi_LOG + "/" + LONG_NAME;

	public static final String XMLSTR = "<?xml version=\"1.0\"?><data><name>data name</name><value>data value</value></data>";

	public static final String PRINCIPAL = "www.cesar.org.br";

	public static final String PRINCIPAL_2 = "www.cin.ufpe.br";
	
	public static final String PRINCIPAL_3 = "www.motorola.com";

	public static final String MIMETYPE = "text/html";
	
	public static final int INVALID_LOCKMODE = 3;
	
	public static final int INVALID_ACL_PERMISSION = 99; 

	public static final String LOG_SEARCH = OSGi_LOG + "/search";

	public static final String MAXSIZE = "maxsize";
	
	public static final String LOG_SEARCH_MAXSIZE = LOG_SEARCH + "/" + MAXSIZE;

	public static final String LOG_SEARCH_MAXRECORDS = LOG_SEARCH + "/maxrecords";

	public static final String LOG_SEARCH_FILTER = LOG_SEARCH + "/filter";

	public static final String LOG_SEARCH_EXCLUDE = LOG_SEARCH + "/exclude";

	public static final String LOG_SEARCH_INVALID = LOG_SEARCH + "/invalid";
	
	public static final String MESSAGE = "Default message";
	
	public static final String ACTIONS = DmtPermission.GET + "," +DmtPermission.REPLACE+","+DmtPermission.ADD;
	
	public static final String DIFFERENT_ACTIONS = DmtPermission.ADD + "," + DmtPermission.EXEC + "," +DmtPermission.GET; 
	
	public static final String ALL_ACTIONS = DmtPermission.ADD + "," + DmtPermission.DELETE + "," +DmtPermission.EXEC + "," + DmtPermission.GET + "," + DmtPermission.REPLACE;
	 
	public static final String ALL_NODES = "./*";
	
	public static final String DDF = "http://www.openmobilealliance.org/tech/DTD/OMA-SyncML-DMDDF-V1_2_0.dtd";
	
	public static final int TIMEOUT = 2000;
	
	private TestExecPluginActivator testExecPluginActivator;
	
	private DmtAdmin dmtAdmin;
	
	private DmtHandlerActivator testDmtHandlerActivator; 
	
	public static String LOCATION = "";

	private PermissionAdmin permissionAdmin;
	
	private TB1Service tb1Service;
	
	private TestInterface[] testBundleTB1;

	public void prepare() {
		ServiceReference dmtAdminReference = getContext().getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) getContext().getService(dmtAdminReference);

		permissionAdmin = (PermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						PermissionAdmin.class.getName()));	
		installBundle();
		installPlugin();
		installHandler();
	}

	private void installBundle() {
		try {
			installBundle("tb1.jar");
		} catch (Exception e) {
			log("#TestControl: Failed installing a bundle");
		}
		ServiceReference tb1SvrReference = getContext().getServiceReference(TB1Service.class.getName());
		LOCATION = tb1SvrReference.getBundle().getLocation();		
		tb1Service = (TB1Service) getContext().getService(tb1SvrReference);
		testBundleTB1 = tb1Service.getTestClasses(this);
		setPermissions(new PermissionInfo(DmtPermission.class.getName(),ALL_NODES,ALL_ACTIONS));		
	}
	private void installPlugin() {
		try {
			testExecPluginActivator = new TestExecPluginActivator(this);
			testExecPluginActivator.start(getContext());		
		} catch (Exception e) {
			log("#TestControl: Failed starting a plugin");
		}
	}
	public void setPermissions(PermissionInfo permission) {
		permissionAdmin.setPermissions(LOCATION,new PermissionInfo[] { 
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/service/dmt/ADDED", TopicPermission.PUBLISH),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/service/dmt/REPLACED", TopicPermission.PUBLISH),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/service/dmt/DELETED", TopicPermission.PUBLISH),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/service/dmt/RENAMED", TopicPermission.PUBLISH),
				new PermissionInfo(TopicPermission.class.getName(), "org/osgi/service/dmt/COPIED", TopicPermission.PUBLISH),
				new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
				new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
				new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
				permission});
	}
	
	public PermissionAdmin getPermissionAdmin() {
		return permissionAdmin;
	}

	public void testDmtSessionConstants() {
		testBundleTB1[21].run();
	}
	
	public void testDmtSessionClose() {
		testBundleTB1[0].run();
		
	}
	public void testDmtSessionCommit() {
		//TODO Remove - It locks the session when an error occur  
		//testBundleTB1[1].run();
		
	}	
	public void testDmtSessionCopy() {
		testBundleTB1[2].run();
		
	}

	public void testDmtSessionCreateInteriorNode() {
		testBundleTB1[3].run();
	}

	public void testDmtSessionCreateLeafNode() {
		testBundleTB1[4].run();
	}

	public void testDmtSessionDeleteNode() {
		testBundleTB1[5].run();
	}

	public void testDmtSessionExecute() {
		testBundleTB1[17].run();
	}

	public void testDmtSessionGetChildNodeNames() {
		testBundleTB1[6].run();
	}

	public void testDmtSessionGetLockType() {
		testBundleTB1[22].run();
	}

	public void testDmtSessionGetEffectiveNodeAcl() {
		testBundleTB1[18].run();
	}
	
	public void testDmtSessionGetMetaNode() {
		testBundleTB1[7].run();
		
	}

	public void testDmtSessionGetSetNodeAcl() {
		testBundleTB1[19].run();
	}

	public void testDmtSessionGetNodeSize() {
		testBundleTB1[8].run();
	}

	public void testDmtSessionGetNodeTimestamp() {
		testBundleTB1[9].run();
	}

	public void testDmtSessionGetNodeVersion() {
		testBundleTB1[10].run();
	}

	public void testDmtSessionGetPrincipal() {
		testBundleTB1[23].run();
	}

	public void testDmtSessionGetRootUri() {
		testBundleTB1[24].run();
	}

	public void testDmtSessionGetSessionId() {
		testBundleTB1[25].run();
	}

	public void testDmtSessionGetSetNodeTitle() {
		testBundleTB1[11].run();
	}

	public void testDmtSessionGetSetNodeType() {
		testBundleTB1[12].run();
	}

	public void testDmtSessionGetSetNodeValue() {
		testBundleTB1[13].run();
	}

	public void testDmtSessionGetState() {
		testBundleTB1[26].run();
	}
	
	public void testDmtSessionIsLeafNode() {
		testBundleTB1[16].run();
	}

	public void testDmtSessionIsNodeUri() {
		testBundleTB1[27].run();
	}

	public void testDmtSessionRenameNode() {
		testBundleTB1[14].run();
	}

	public void testDmtSessionRollback() {
		//TODO Remove - It locks the session when an error occur
		//testBundleTB1[15].run();
	}
	
	//DmtAcl test cases
	public void testDmtAcl() {
		new org.osgi.test.cases.dmt.main.tbc.DmtAcl.DmtAcl(this).run();
	}

	public void testDmtAclAddPermission() {
		new AddPermission(this).run();
	}

	public void testDmtAclDeletePermission() {
		new DeletePermission(this).run();
	}

	public void testDmtAclConstants() {
		new DmtAclConstants(this).run();
	}

	public void testDmtAclIsPermitted() {
		new IsPermitted(this).run();
	}

	public void testDmtAclSetPermission() {
		new SetPermission(this).run();
	}
	
	public void testDmtAclToString() {
		new org.osgi.test.cases.dmt.main.tbc.DmtAcl.ToString(this).run();
	}
	
	public void testDmtAclEquals() {
		new org.osgi.test.cases.dmt.main.tbc.DmtAcl.Equals(this).run();
	}

	public void testDmtAclHashcode() {
		new Hashcode(this).run();
	}
	
	public void testDmtAclClone() {
		new Clone(this).run();
	}	
	//DmtAdmin Test case
	public void testDmtAdminGetSession() {
		testBundleTB1[20].run();
	}
	
	//DmtData Test Cases
	public void testDmtDataConstant() {
		new DmtDataConstants(this).run();
	}

	public void testDmtData()  {
		new DmtData(this).run();
	}

	public void testDmtDataEquals() {
		new Equals(this).run();
	}

	public void testDmtDataToString() {
		new org.osgi.test.cases.dmt.main.tbc.DmtData.ToString(this).run();
	}
	
	//DmtException test cases

	public void testDmtException(){
		new org.osgi.test.cases.dmt.main.tbc.DmtException.DmtException(this).run();
	}
	
	public void testDmtExceptionPrintStackTrace(){
		new PrintStackTrace(this).run();
	}
	
	public void testDmtExceptionConstants(){
		new DmtExceptionConstants(this).run();
	}

	//DmtPrincipalPermission test case

	public void testDmtPrincipalPermission(){
		new DmtPrincipalPermission(this).run();
	}

	public void testDmtPrincipalPermissionEquals(){
		new org.osgi.test.cases.dmt.main.tbc.DmtPrincipalPermission.Equals(this).run();
	}

	public void testDmtPrincipalPermissionImplies(){
		new org.osgi.test.cases.dmt.main.tbc.DmtPrincipalPermission.Implies(this).run();
	}

	public void testDmtPrincipalPermissionHashCode(){
		new org.osgi.test.cases.dmt.main.tbc.DmtPrincipalPermission.HashCode(this).run();
	}

	//DmtPermission test cases
	public void testDmtPermission() {
		new org.osgi.test.cases.dmt.main.tbc.DmtPermission.DmtPermission(this).run();
	}
	
	public void testDmtPermissionConstants() {
		new org.osgi.test.cases.dmt.main.tbc.DmtPermission.DmtPermissionConstants(this).run();
	}
	
	public void testDmtPermissionEquals() {
		new org.osgi.test.cases.dmt.main.tbc.DmtPermission.Equals(this).run();
	}
	
	public void testDmtPermissionHashCode() {
		new org.osgi.test.cases.dmt.main.tbc.DmtPermission.HashCode(this).run();
	}	
	
	public void unprepare() {
		uninstallHandler();
	}


	public DmtAdmin getDmtAdmin() {
		if (dmtAdmin != null)
			return dmtAdmin;
		else
			throw new NullPointerException("DmtAdmin is null");
	}

	/**
	 * It deletes all the nodes created during the execution of the test. It
	 * receives a String array containing all the node URIs.
	 */
	public void cleanUp(DmtSession session,String[] nodeUri) {
		if (session != null && session.getState() == DmtSession.STATE_OPEN) {
			if (nodeUri == null) {
				closeSession(session);
			} else {
				for (int i = 0; i < nodeUri.length; i++) {
					try {
						session.deleteNode(nodeUri[i]);
					} catch (Throwable e) {
						//log("#Exception at cleanUp: "+e.getClass().getName() + " [Message: " +e.getMessage() +"]");
					}
				}
				closeSession(session);
			}
		}
	}
	
	public void cleanUp(DmtSession session, DmtSession remoteSession, String nodeUri) {
		closeSession(session);
		closeSession(remoteSession);
		setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtTestControl.ALL_NODES,DmtTestControl.ALL_ACTIONS));
		if (nodeUri != null)
			cleanAcl(nodeUri);
	}
	
	private void installHandler() {
		testDmtHandlerActivator = new DmtHandlerActivator(this);
		try {
			testDmtHandlerActivator.start(this.getContext());
		} catch (Exception e) {
			log("#Fail when starting the Handler");
		}		
	}
	
	public void uninstallHandler() {
		if (testDmtHandlerActivator != null) {
			try {
				testDmtHandlerActivator.stop(this.getContext());
			} catch (Exception e) {
				log("#Fail when stopping the Handler");
			}
		}
	}

	public void cleanAcl(String nodeUri) {
		DmtSession session = null;
		try {
			session = getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
			session.setNodeAcl(nodeUri,null);
		} catch (Exception e) {
			log("#Exception cleaning the acl from "+ nodeUri +" : "+e.getClass().getName() + "Message: [" +e.getMessage() +"]");
		} finally {
			closeSession(session);
		}
		
	}
	public void closeSession(DmtSession session) {
		if (null != session) {
			if (session.getState() == DmtSession.STATE_OPEN) {
				try {
					session.close();
				} catch (DmtException e) {
					log("#Exception closing the session: "+e.getClass().getName() + "Message: [" +e.getMessage() +"]");
				}
			}
		}
	}	

}
