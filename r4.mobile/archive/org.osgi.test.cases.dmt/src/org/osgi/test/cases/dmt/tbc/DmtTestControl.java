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
 * 21/JAN/2005  Andre Assad
 * CR 1         Implement MEG TCK
 * ===========  ==============================================================
 * Feb 14, 2005  Alexandre Santos
 * 1             Updates after formal inspection (BTC_MEG_TCK_CODE-INSPR-002)
 * ===========  ==============================================================
 * Mar 04, 2005  Alexandre Santos
 * 23            Updates due to changes in the DmtAcl API
 * ===========  ==============================================================
 */

package org.osgi.test.cases.dmt.tbc;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.dmt.*;
import org.osgi.service.dmt.DmtException;
import org.osgi.test.cases.dmt.tbc.DmtAcl.*;
import org.osgi.test.cases.dmt.tbc.DmtAdmin.GetSession;
import org.osgi.test.cases.dmt.tbc.DmtData.*;
import org.osgi.test.cases.dmt.tbc.DmtData.DmtData;
import org.osgi.test.cases.dmt.tbc.DmtDataType.DmtDataTypeConstants;
import org.osgi.test.cases.dmt.tbc.DmtException.*;
import org.osgi.test.cases.dmt.tbc.DmtPrincipalPermission.DmtPrincipalPermission;
import org.osgi.test.cases.dmt.tbc.DmtSession.*;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * @author Andre Assad
 *
 * Controls the execution of the test case. By default a session is created on
 * the root (".") node, if a diferent session is required for a test class, it
 * must be set on the the test class itself.
 */
public class DmtTestControl extends DefaultTestBundleControl {
	public static final String OSGi_ROOT = "./OSGi";

	public static final String OSGi_LOG = OSGi_ROOT + "/log";

	public static final String OSGi_APPS = OSGi_ROOT + "/apps";

	public static final String OSGi_CFG = OSGi_ROOT + "/cfg";

	public static final String OSGi_MON = OSGi_ROOT + "/mon";

	public static final String OSGi_APP_INST = OSGi_ROOT + "app_instances";

	public static final String SOURCE = "./OSGi/log/source";

	public static final String SUBTREE = "./OSGi/log/source/tst";

	public static final String SOURCE_LEAF = "./OSGi/log/source/maxsize";

	public static final String DESTINY = "./OSGi/log/destiny";

	public static final String DESTINY_LEAF = "./OSGi/log/destiny/maxsize";

	public static final String INVALID_NODE = "./OSGi/log/invalid";

	public static final String ACLSTR = "Add=*&Delete=*&Replace=*&Get=*";

	public static final String ACL_CESAR = "Add=www.cesar.org.br&Delete=www.cesar.org.br&Get=*";

	public static final String TITLE = "Ninja";

	public static final String INVALID_URI = "./OSGi/log/#@~z";

	public static final String INVALID_LEAFNAME = "%&ã#&$#";

	public static final String LONG_NAME = "sdsdqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfghjklzxcvbnqwertyuiopasdfgzcvddddddsd";

	public static final String URI_LONG = "./OSGi/log/" + LONG_NAME;

	public static final String XMLSTR = "<?xml version=\"1.0\"?><data><name>data name</name><value>data value</value></data>";

	public static final String PRINCIPAL = "www.cesar.org.br";
	
	public static final String PRINCIPAL_2 = "www.cin.ufpe.br";

	public static final String MIMETYPE = "text/html";
	
	public static final int INVALID_LOCKMODE = 3;
	
	public static final String MESSAGE = "Default message";

	private DmtSession session;

	private DmtAdmin factory;

	/**
	 * <remove>Prepare for each run. It is important that a test run is properly
	 * initialized and that each case can run standalone. To save a lot of time
	 * in debugging, clean up all possible persistent remains before the test is
	 * run. Clean up is better don in the prepare because debugging sessions can
	 * easily cause the unprepare never to be called. </remove>
	 * 
	 * @throws InvalidSyntaxException
	 */
	public void prepare() {
		log("#before each run");

		factory = (DmtAdmin) getContext().getService(
				getContext().getServiceReference(DmtAdmin.class.getName()));

	}

	/**
	 * <remove>Prepare for each method. It is important that each method can be
	 * executed independently of each other method. Do not keep state between
	 * methods, if possible. This method can be used to clean up any possible
	 * remaining state. </remove>
	 * 
	 */
	public void setState() {
		log("#before each method");
		// resets the session
		getSession(".", DmtSession.LOCK_TYPE_ATOMIC);
	}

	public void testConstants() {
		new Constants(this).run();
	}

	public void testCopy() {
		new Copy(this).run();
	}

	public void testCreateInteriorNode() {
		new CreateInteriorNode(this).run();
	}

	public void testCreateLeafNode() {
		new CreateLeafNode(this).run();
	}

	public void testDeleteNode() {
		new DeleteNode(this).run();
	}

	public void testExecute() {
		new Execute(this).run();
	}

	public void testGetChildNodeNames() {
		new GetChildNodeNames(this).run();
	}

	public void testGetLockType() {
		new GetLockType(this).run();
	}

	public void testGetMetaNode() {
		new GetMetaNode(this).run();
	}

	public void testGetSetNodeAcl() {
		new GetSetNodeAcl(this).run();
	}

	public void testGetNodeSize() {
		new GetNodeSize(this).run();
	}

	public void testGetNodeTimestamp() {
		new GetNodeTimestamp(this).run();
	}

	public void testGetNodeVersion() {
		new GetNodeVersion(this).run();
	}

	public void testGetPrincipal() {
		new GetPrincipal(this).run();
	}

	public void testGetRootUri() {
		new GetRootUri(this).run();
	}

	public void testGetSessionId() {
		new GetSessionId(this).run();
	}

	public void testGetSetNodeTitle() {
		new GetSetNodeTitle(this).run();
	}

	public void testGetSetNodeType() {
		new GetSetNodeType(this).run();
	}

	public void testGetSetNodeValue() {
		new GetSetNodeValue(this).run();
	}

	public void testIsLeafNode() {
		new IsLeafNode(this).run();
	}

	public void testIsNodeUri() {
		new IsNodeUri(this).run();
	}

	public void testRenameNode() {
		new RenameNode(this).run();
	}

	public void testRollback() {
		new Rollback(this).run();
	}
	
	//DmtAcl test cases
	public void testDmtAcl() {
		new org.osgi.test.cases.dmt.tbc.DmtAcl.DmtAcl(this).run();
	}

	public void testAddPermission() {
		new AddPermission(this).run();
	}

	public void testDeletePermission() {
		new DeletePermission(this).run();
	}

	public void testDmtAclConstants() {
		new DmtAclConstants(this).run();
	}

	public void testIsPermitted() {
		new IsPermitted(this).run();
	}

	public void testSetPermission() {
		new SetPermission(this).run();
	}
	
	public void testToString() {
		new org.osgi.test.cases.dmt.tbc.DmtAcl.ToString(this).run();
	}
	
	//DmtAdmin Test case
	public void testGetSession() {
		new GetSession(this).run();
	}
	
	public void testDmtDataConstant() {
		new DmtDataConstants(this).run();
	}

	/**
	 * This method runs all the test methods for asseting the
	 * <code>DmtData</code> constructors.
	 * @throws DmtException 
	 */
	public void testDmtData() throws DmtException {
		new DmtData(this).run();
	}

	/**
	 * This method runs all the test methods for asseting the
	 * <code>DmtData</code> equals() method
	 */
	public void testEquals() {
		new Equals(this).run();
	}

	public void testHashCode() {
		new HashCode(this).run();
	}

	public void testDmtDataToString() {
		new org.osgi.test.cases.dmt.tbc.DmtData.ToString(this).run();
	}
	
	//DmtDataType test cases
	/**
	 * This method runs all the test methods to assert the constants values
	 * 
	 */
	public void testDmtDataTypeConstants() {
		new DmtDataTypeConstants(this).run();
	}

	//DmtException test cases
	/**
	 * Runs DmtException constructors tests.  
	 */
	public void testDmtException(){
		new org.osgi.test.cases.dmt.tbc.DmtException.DmtException(this).run();
	}
	
	/**
	 * Runs getCause tests.  
	 */
	public void testGetCause(){
		new GetCause(this).run();
	}
	
	/**
	 * Runs getCauses tests.  
	 */
	public void testGetCauses(){
		new GetCauses(this).run();
	}
	
	/**
	 * Runs getCode tests.  
	 */
	public void testGetCode(){
		new GetCode(this).run();
	}
	
	/**
	 * Runs getMessage tests.  
	 */
	public void testGetMessage(){
		new GetMessage(this).run();
	}
	
	/**
	 * Runs getURI tests.  
	 */
	public void testGetURI(){
		new GetURI(this).run();
	}
	
	/**
	 * Runs printStackTrace tests.  
	 */
	public void testPrintStackTrace(){
		new PrintStackTrace(this).run();
	}
	
	public void testDmtExceptionConstants(){
		new DmtExceptionConstants(this).run();
	}

	//DmtPrincipalPermission test case
	/**
	 * Runs DmtPrincipalPermission constructors tests.  
	 */
	public void testDmtPrincipalPermission(){
		new DmtPrincipalPermission(this).run();
	}

	
	

	/**
	 * Clean up after each method. Notice that during debugging many times the
	 * unsetState is never reached.
	 */
	public void unsetState() {
		log("#after each method");
	}

	/**
	 * Clean up after a run. Notice that during debugging many times the
	 * unprepare is never reached.
	 */
	public void unprepare() {
		log("#after each run");
		if (session != null) {
			try {
				session.close();
			} catch (DmtException e) {
				fail("Session could not be closed");
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return Returns the factory.
	 */
	public DmtAdmin getFactory() {
		if (factory != null)
			return factory;
		else
			throw new NullPointerException("DmtAdmin factory is null");
	}

	public DmtSession getSession(String nodeURI) {
		if (nodeURI != null) {
			try {
				if (session != null) {
					session.commit();
					session.close();
				}
				session = getFactory().getSession(nodeURI);
			} catch (DmtException e) {
				log("TestControl: Fail to get the session");
			} catch (IllegalStateException e) {
				try {
					session = getFactory().getSession(nodeURI);
				} catch (DmtException e1) {
					log("TestControl: Fail to get the session");
				}
				return session;
			} catch (Exception e) {
				log("TestControl: Unexpected Exception "+e.getClass().getName());
			}
			return session;
		} else {
			throw new IllegalArgumentException("node URI is null or invalid.");
		}
	}

	public DmtSession getSession(String nodeURI, int lockMode) {
		if (nodeURI != null) {
			try {
				if (session != null) {
					session.commit();
					session.close();
				}
				session = getFactory().getSession(nodeURI, lockMode);
			} catch (DmtException e) {
				log("TestControl: Fail to get the session");
			} catch (IllegalStateException e) {
				try {
					session = getFactory().getSession(nodeURI, lockMode);
				} catch (DmtException e1) {
					log("TestControl: Fail to get the session");
				}
				return session;
			} catch (Exception e) {
				log("TestControl: Unexpected Exception "+e.getClass().getName());
			}
			return session;
		} else {
			throw new IllegalArgumentException("node URI is null or invalid.");
		}
	}

	public DmtSession getSession(String principal, String nodeURI, int lockMode) {
		if (nodeURI != null) {
			try {
				if (session != null) {
					session.commit();
					session.close();
				}
				session = getFactory().getSession(principal, nodeURI, lockMode);
			} catch (DmtException e) {
				log("TestControl: Fail to get the session");
			} catch (IllegalStateException e) {
				try {
					session = getFactory().getSession(principal, nodeURI, lockMode);
				} catch (DmtException e1) {
					log("TestControl: Fail to get the session");
				}
				return session;
			} catch (Exception e) {
				log("TestControl: Unexpected Exception "+e.getClass().getName());
			}
			return session;
		} else {
			throw new IllegalArgumentException("Node URI is null or invalid.");
		}
	}

	public DmtSession getSession() {
		if (session != null) {
			return session;
		} else {
			throw new NullPointerException("Current session is null");
		}
	}

	/**
	 * It deletes all the nodes created during the execution of the test. It
	 * receives a String array containing all the node URIs.
	 * 
	 * @param nodeUri
	 * @return Returns true or false
	 */
	public boolean cleanUp(String[] nodeUri) {
		if (nodeUri == null)
			return false;

		for (int i = 0; i < nodeUri.length; i++) {
			try {
				getSession().deleteNode(nodeUri[i]);
			} catch (DmtException e) {
                continue;
			} catch (Throwable e) {
				return false;
			}
		}
		return true;
	}

}
