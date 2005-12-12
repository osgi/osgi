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
 * Aug 25, 2005  Luiz Felipe Guimaraes
 * 173           [MEGTCK][DMT] Changes on interface names and plugins
 * ============  ==============================================================
 */

package org.osgi.test.cases.dmt.main.tbc;
import java.security.MessageDigest;
import java.util.PropertyPermission;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.Acl;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.event.TopicPermission;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.cases.dmt.main.tbc.Acl.AclConstants;
import org.osgi.test.cases.dmt.main.tbc.Acl.AclConstraints;
import org.osgi.test.cases.dmt.main.tbc.Acl.AddPermission;
import org.osgi.test.cases.dmt.main.tbc.Acl.DeletePermission;
import org.osgi.test.cases.dmt.main.tbc.Acl.Hashcode;
import org.osgi.test.cases.dmt.main.tbc.Acl.IsPermitted;
import org.osgi.test.cases.dmt.main.tbc.Acl.SetPermission;
import org.osgi.test.cases.dmt.main.tbc.Activators.EventHandlerActivator;
import org.osgi.test.cases.dmt.main.tbc.Activators.RemoteAlertSenderActivator;
import org.osgi.test.cases.dmt.main.tbc.AlertItem.AlertItem;
import org.osgi.test.cases.dmt.main.tbc.AlertItem.ToString;
import org.osgi.test.cases.dmt.main.tbc.AlertPermission.AlertPermission;
import org.osgi.test.cases.dmt.main.tbc.DmtData.DmtData;
import org.osgi.test.cases.dmt.main.tbc.DmtData.DmtDataConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtData.Equals;
import org.osgi.test.cases.dmt.main.tbc.DmtData.TestDmtDataExceptions;
import org.osgi.test.cases.dmt.main.tbc.DmtException.DmtExceptionConstants;
import org.osgi.test.cases.dmt.main.tbc.DmtException.PrintStackTrace;
import org.osgi.test.cases.dmt.main.tbc.DmtPrincipalPermission.DmtPrincipalPermission;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPlugin;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ExecPlugin.TestExecPluginActivator;
import org.osgi.test.cases.dmt.main.tbc.Plugin.NonAtomic.TestNonAtomicPluginActivator;
import org.osgi.test.cases.dmt.main.tbc.Plugin.ReadOnly.TestReadOnlyPluginActivator;
import org.osgi.test.cases.util.DefaultTestBundleControl;

public class DmtTestControl extends DefaultTestBundleControl {
	
	private TestExecPluginActivator testExecPluginActivator;
	
    private TestNonAtomicPluginActivator testNonAtomicPluginActivator;
    
	private TestReadOnlyPluginActivator testReadOnlyPluginActivator;
	
	private RemoteAlertSenderActivator remoteAlertSenderActivator;
	
	private DmtAdmin dmtAdmin;
	
	private EventHandlerActivator testDmtHandlerActivator; 
	
	public static String LOCATION = "";

	private PermissionAdmin permissionAdmin;
	
	private TB1Service tb1Service;
	
	private TestInterface[] testClasses;
    
    private PermissionWorker permissionWorker;

	//URIs too long, to be used simulating DmtException.URI_TOO_LONG
	public final static String[] URIS_TOO_LONG;
	
    static {
	    
	    if (DmtConstants.MAXIMUM_NODE_LENGTH>0) {
	        if (DmtConstants.MAXIMUM_NODE_SEGMENTS>0) {
	            URIS_TOO_LONG = new String[] { getSegmentTooLong(TestExecPluginActivator.ROOT),getExcedingSegmentsUri(TestExecPluginActivator.ROOT) };
	        } else {
	            URIS_TOO_LONG = new String[] { getSegmentTooLong(TestExecPluginActivator.ROOT) };
	        }
	    } else if (DmtConstants.MAXIMUM_NODE_SEGMENTS>0) { 
	        URIS_TOO_LONG = new String[] { getExcedingSegmentsUri(TestExecPluginActivator.ROOT)};
	    }
	    
	            
    };
	
    //Invalid URIs, to be used simulating DmtException.INVALID_URI
    public final static Object[] INVALID_URIS = new Object[]{ 
        null, 
        TestExecPluginActivator.INTERIOR_NODE + "/", 
        TestExecPluginActivator.INTERIOR_NODE + "\\", 
        TestExecPluginActivator.ROOT + "/./" + TestExecPluginActivator.INTERIOR_NODE_NAME, 
        TestExecPluginActivator.INTERIOR_NODE + "/../" + TestExecPluginActivator.INTERIOR_NODE_NAME};
    

	public void prepare() {
		ServiceReference dmtAdminReference = getContext().getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin) getContext().getService(dmtAdminReference);

		permissionAdmin = (PermissionAdmin) getContext().getService(
				getContext().getServiceReference(
						PermissionAdmin.class.getName()));	
		installBundle();
		installPlugins();
		installHandler();
		installRemoteAlertSender();
        permissionWorker = new PermissionWorker(this);
        permissionWorker.start(); 
		
	}
	public void installRemoteAlertSender() {
		try {
			remoteAlertSenderActivator = new RemoteAlertSenderActivator();
			remoteAlertSenderActivator.start(getContext());
		} catch (Exception e) {
			log("#TestControl: Failed starting the remote alert sender");
		}
	}	
	
	
	private void installBundle() {
		try {
			installBundle("tb1.jar");
		} catch (Exception e) {
			log("#TestControl: Failed installing tb1 bundle");
		}
		ServiceReference tb1SvrReference = getContext().getServiceReference(TB1Service.class.getName());
		LOCATION = tb1SvrReference.getBundle().getLocation();		
		tb1Service = (TB1Service) getContext().getService(tb1SvrReference);
		testClasses = tb1Service.getTestClasses(this);
				
	}
	private void installPlugins() {
		try {
			testExecPluginActivator = new TestExecPluginActivator(this);
			testExecPluginActivator.start(getContext());
            
            testNonAtomicPluginActivator = new TestNonAtomicPluginActivator(this);
            testNonAtomicPluginActivator.start(getContext());    
            
			testReadOnlyPluginActivator = new TestReadOnlyPluginActivator(this);
			testReadOnlyPluginActivator.start(getContext());	
		} catch (Exception e) {
			log("#TestControl: Failed starting plugins");
		}
	}
	public void setPermissions(PermissionInfo[] permissions) {
		PermissionInfo[] defaults = new PermissionInfo[] {
			new PermissionInfo(TopicPermission.class.getName(), "org/osgi/service/dmt/*", TopicPermission.PUBLISH),
			new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
			new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
			new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
			new PermissionInfo(PropertyPermission.class.getName(), "*","read"),
		};
        PermissionInfo[] perm;
		if (permissions.length != defaults.length) {
    		int size = permissions.length + defaults.length;
            perm = new PermissionInfo[size];
    		System.arraycopy(defaults, 0, perm, 0, defaults.length);
    		System.arraycopy(permissions, 0, perm, defaults.length, permissions.length);
        } else {
            perm = defaults;
        }


        synchronized (permissionWorker) {
            permissionWorker.setLocation(LOCATION);
            permissionWorker.setPermissions(perm);
            permissionWorker.notifyAll();
            try {
                permissionWorker.wait(DmtConstants.WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
	}
	public void setPermissions(PermissionInfo permission) {
        
        PermissionInfo[] perm = new PermissionInfo[] {
            new PermissionInfo(TopicPermission.class.getName(), "org/osgi/service/dmt/*", TopicPermission.PUBLISH),
            new PermissionInfo(PackagePermission.class.getName(), "*", "EXPORT, IMPORT"),
            new PermissionInfo(ServicePermission.class.getName(), "*", "GET"),
            new PermissionInfo(AdminPermission.class.getName(), "*", "*"),
            new PermissionInfo(PropertyPermission.class.getName(), "*","read"),
            permission
        };

        synchronized (permissionWorker) {
            permissionWorker.setLocation(LOCATION);
            permissionWorker.setPermissions(perm);
            permissionWorker.notifyAll();
            try {
                permissionWorker.wait(DmtConstants.WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


	}
	
	public PermissionAdmin getPermissionAdmin() {
		return permissionAdmin;
	}
	public String getHash(String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA");
		byte[] b = md.digest(str.getBytes("UTF-8"));
		StringBuffer temp = new StringBuffer(new String(Base64Encoder.encode(b),"UTF-8"));
		int index=0;
		while (temp.length()>index) {
		    if (temp.charAt(index) == '=') {
		        temp.deleteCharAt(index);
		    } else if (temp.charAt(index) == '/') {
		        temp.setCharAt(index,'_');
		    }
		    index++;
		}
		return temp.toString();
	}
	
	public void testEvents() {
		testClasses[34].run();
	}

	//DmtSession test cases
	
	//Tests common DmtSession exceptions
	public void testDmtSessionExceptions() {
		testClasses[32].run();
	}
	
	public void testDmtSessionConstants() {
		testClasses[21].run();
	}
	
	public void testDmtSessionClose() {
		testClasses[0].run();
		
	}
	public void testDmtSessionCommit() {
		testClasses[1].run();
		
	}	
	public void testDmtSessionCopy() {
		testClasses[2].run();
		
	}

	public void testDmtSessionCreateInteriorNode() {
		testClasses[3].run();
	}

	public void testDmtSessionCreateLeafNode() {
		testClasses[4].run();
	}

	public void testDmtSessionDeleteNode() {
		testClasses[5].run();
	}

	public void testDmtSessionExecute() {
		testClasses[17].run();
	}

	public void testDmtSessionGetChildNodeNames() {
		testClasses[6].run();
	}

	public void testDmtSessionGetLockType() {
		testClasses[22].run();
	}

	public void testDmtSessionGetEffectiveNodeAcl() {
	   testClasses[18].run();
	}
	
	public void testDmtSessionGetMetaNode() {
		testClasses[7].run();
		
	}

	public void testDmtSessionGetSetNodeAcl() {
	    testClasses[19].run();
	}

	public void testDmtSessionGetNodeSize() {
		testClasses[8].run();
	}

	public void testDmtSessionGetNodeTimestamp() {
	    testClasses[9].run();
		
	}

	public void testDmtSessionGetNodeVersion() {
		testClasses[10].run();
	}

	public void testDmtSessionGetPrincipal() {
		testClasses[23].run();
	}

	public void testDmtSessionGetRootUri() {
		testClasses[24].run();
	}

	public void testDmtSessionGetSessionId() {
		testClasses[25].run();
	}

	public void testDmtSessionGetSetNodeTitle() {
	    testClasses[11].run();

	}

	public void testDmtSessionGetSetNodeType() {
		testClasses[12].run();
	}

	public void testDmtSessionGetSetNodeValue() {
		testClasses[13].run();
	}

	public void testDmtSessionGetState() {
		testClasses[26].run();
	}
	
	public void testDmtSessionIsLeafNode() {
		testClasses[16].run();
	}

	public void testDmtSessionIsNodeUri() {
		testClasses[27].run();
	}

	public void testDmtSessionRenameNode() {
		testClasses[14].run();
	}

	public void testDmtSessionRollback() {
		testClasses[15].run();
	}
	
	public void testDmtSessionSetDefaultNodeValue() {
		testClasses[30].run();
	}
	
	public void testDmtSessionMangle() {
		testClasses[33].run();
	}
	
	//Acl test cases
	public void testAcl() {
		new org.osgi.test.cases.dmt.main.tbc.Acl.Acl(this).run();
	}

	public void testAclAddPermission() {
		new AddPermission(this).run();
	}

	public void testAclDeletePermission() {
		new DeletePermission(this).run();
	}

	public void testAclConstants() {
		new AclConstants(this).run();
	}

	public void testAclIsPermitted() {
		new IsPermitted(this).run();
	}

	public void testAclSetPermission() {
		new SetPermission(this).run();
	}
	
	public void testAclToString() {
		new org.osgi.test.cases.dmt.main.tbc.Acl.ToString(this).run();
	}
	
	public void testAclEquals() {
		new org.osgi.test.cases.dmt.main.tbc.Acl.Equals(this).run();
	}

	public void testAclHashcode() {
		new Hashcode(this).run();
	}
	
	
	public void testAclConstraints() {
		new AclConstraints(this).run();
	}	
	
	//DmtAdmin Test cases
	public void testDmtAdminGetSession() {
		testClasses[20].run();
	}
	
	public void testDmtAdminSendAlert() {
		testClasses[28].run();
	}

	public void testDmtAdminMangle() {
		testClasses[29].run();
	}

	public void testDmtAdminDmtAdressingUri() {
		testClasses[31].run();
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
	public void testDmtDataExceptions() {
		new TestDmtDataExceptions(this).run();
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
	
	//AlertItem
	public void testAlertItem() {
		new AlertItem(this).run();
	}
	
	public void testAlertItemToString() {
		new ToString(this).run();
	}
	
	//AlertPermission
	public void testAlertPermission() {
		new AlertPermission(this).run();
	}
	
	public void testAlertPermissionEquals() {
		new org.osgi.test.cases.dmt.main.tbc.AlertPermission.Equals(this).run();
	}	

	public void testAlertPermissionHashCode() {
		new org.osgi.test.cases.dmt.main.tbc.AlertPermission.HashCode(this).run();
	}	

	public void testAlertPermissionImplies() {
		new org.osgi.test.cases.dmt.main.tbc.AlertPermission.Implies(this).run();
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


	public void cleanUp(DmtSession session, String nodeUri) {
		closeSession(session);
		//setPermissions(new PermissionInfo(DmtPermission.class.getName(), DmtConstants.ALL_NODES,DmtConstants.ALL_ACTIONS));
		if (nodeUri != null)
			cleanAcl(nodeUri);
	}
	
	private void installHandler() {
		testDmtHandlerActivator = new EventHandlerActivator(this);
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
			TestExecPlugin.setAllUriIsExistent(true);
			if (session.isNodeUri(nodeUri)) {
			    session.setNodeAcl(nodeUri,null);
			}
			TestExecPlugin.setAllUriIsExistent(false);
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
	public String mangleUri(String[] nodeUri) {
	    String nodeName="";
	    if (null != nodeUri) {
		    StringBuffer nodeNameBuffer= new StringBuffer();
			if (nodeUri.length>0) {
				for (int i=0;i<nodeUri.length;i++) {
					nodeNameBuffer = nodeNameBuffer.append(getDmtAdmin().mangle(nodeUri[i]) + "/");
				}
				nodeName = nodeNameBuffer.substring(0,nodeNameBuffer.length()-1);
			}
	    }		
		return nodeName;
	}


    /**
	 * Appends a nodeUri with exceeding segments
	 * @param nodeUri The URI base
	 * @return The URI base appended with the an exceeding number of segments
	 */
	public static String getExcedingSegmentsUri(String nodeUri) {
	    //Gets the number of segments
		int rootPluginSegments = uriTotalSegments(TestExecPluginActivator.ROOT);
		//The segments to be appended are equal to the maximum number of segments plus one.
		int totalSegments = DmtConstants.MAXIMUM_NODE_SEGMENTS - rootPluginSegments + 1;
		//Appends an the specified number of segments
		return appendSegments(nodeUri,totalSegments);
	}
	/**
	 * Appends a nodeUri with a number of segments. 
	 * @param nodeUri The URI base
	 * @param numberOfSegments The number of segments to be appended.
	 * @return The URI base appended with the number of segments specified
	 */
	
	public static String appendSegments(String nodeUri,int numberOfSegments) {
	    StringBuffer uriTooLong =  new StringBuffer(nodeUri);
	    for (int i=0; i<numberOfSegments;i++) {
	        uriTooLong.append("/a");
		}
		return uriTooLong.toString(); 
	}
	
	/**
	 * This method returns the total number of segments, assuming that '/' are not in node names 
	 * @param nodeUri The URI to be checked
	 * @return The total number of segments of that URI.
	 */
	public static int uriTotalSegments(String nodeUri) {
		int totalSegments =0;
		int currentIndex=-1;
		boolean finish = false;
		while (!finish) {
		    currentIndex =nodeUri.indexOf("/",currentIndex+1);
		    if (currentIndex!=-1) {
		        totalSegments++;
		    } else {
		        finish=true;
		    }
		}
		return totalSegments;
	}
	/**
	 * Appends a base URI with a segment that exceeds the limit defined by the
	 * implementation
	 * @param nodeUri The URI base
	 * @return If nodeUri is not null it returns the URI base appended with a segment that exceeds the limit else
	 * it returns a segment too long.
	 */
	public static String getSegmentTooLong(String nodeUri) {
		int nodeLength = DmtConstants.MAXIMUM_NODE_LENGTH + 1;
		StringBuffer nodeName = new StringBuffer(nodeLength);
		for (int i=0;i<nodeLength;i++) {
			nodeName.append("a");
		}
		if (null!=nodeUri) { 
			return nodeUri + "/" + nodeName.toString(); 
		} else {
		    return nodeName.toString();
		}
		    
	}
    
    public void openSessionAndSetNodeAcl(String nodeUri,String principal,int permissions) {
        DmtSession session = null;
        try {
            session = getDmtAdmin().getSession(".",DmtSession.LOCK_TYPE_EXCLUSIVE);
            session.setNodeAcl(
                nodeUri,new Acl(new String[]{ principal }, new int[]{ permissions }));
            
        } catch (Exception e) {
            fail("Unexpected Exception: " + e.getClass().getName()
                + " [Message: " + e.getMessage() + "]");
        } finally {
            closeSession(session);
        }
    
    }
    
}
