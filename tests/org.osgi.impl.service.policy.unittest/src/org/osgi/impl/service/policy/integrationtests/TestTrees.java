/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.policy.integrationtests;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.policy.condpermadmin.ConditionalPermissionAdminPlugin;
import org.osgi.impl.service.policy.dmtprincipal.DmtPrincipalPlugin;
import org.osgi.impl.service.policy.permadmin.PermissionAdminPlugin;
import org.osgi.service.condpermadmin.BundleSignerCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtPermission;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionInfo;

public class TestTrees extends IntegratedTest {
	public static final String	ORG_OSGI_IMPL_SERVICE_POLICY_JAR	= "file:../../org.osgi.impl.service.policy/org.osgi.impl.service.policy.jar";
	public static final String PRINCIPAL1 = "principal1";
	public static final String PRINCIPAL1_HASH = "zDcCo9K+A67rtQI3TQEDg6_LEIw";
	public DmtAdmin	dmtAdmin;
	public Bundle	policyBundle;
	
	public void startFramework(boolean fresh) throws Exception {
		super.startFramework(fresh);
		if (fresh) {
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_POLICY_JAR);
		}
		policyBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_POLICY_JAR);
		policyBundle.start();
		ServiceReference sr = systemBundleContext.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin)systemBundleContext.getService(sr);
		
		// deferred services are added asynch, give component service some time. 
		synchronized (this) { this.wait(100); }
	}

	public void stopFramework() throws Exception {
		policyBundle = null;
		dmtAdmin = null;
		super.stopFramework();
	}
	
	
	public void testAllStartsUp() throws Exception {
		startFramework(true);

		// check if all three policy trees are registered
		ServiceReference[] sr;
		sr = systemBundleContext.getServiceReferences(DmtDataPlugin.class.getName(),"(dataRootURIs="+PermissionAdminPlugin.dataRootURI+")");
		assertNotNull(sr[0]);
		sr = systemBundleContext.getServiceReferences(DmtDataPlugin.class.getName(),"(dataRootURIs="+ConditionalPermissionAdminPlugin.dataRootURI+")");
		assertNotNull(sr[0]);
		sr = systemBundleContext.getServiceReferences(DmtDataPlugin.class.getName(),"(dataRootURIs="+DmtPrincipalPlugin.dataRootURI+")");
		assertNotNull(sr[0]);
		stopFramework();
	}
	
	public void testDmtPrincipals() throws Exception {
		startFramework(true);

		// "principal1" gets the right to read the principal tree
		DmtSession session = dmtAdmin.getSession(DmtPrincipalPlugin.dataRootURI,DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode("1");
		session.setNodeValue("1/Principal",new DmtData(PRINCIPAL1));
		session.setNodeValue("1/PermissionInfo",new DmtData("(org.osgi.service.dmt.DmtPermission \""+DmtPrincipalPlugin.dataRootURI+"\" \"Get\")"));
		session.close();

		//stopFramework();
		// TODO: dmt admin doesn't save the principal permissions to backstorage yet
		//startFramework(false);

		// check if it is there
		session = dmtAdmin.getSession(DmtPrincipalPlugin.dataRootURI);
		DmtData value = session.getNodeValue(PRINCIPAL1_HASH+"/Principal");
		assertEquals(PRINCIPAL1,value.getString());
		session.close();
				
		// try to read from the principal tree as "principal1"
		session = dmtAdmin.getSession(PRINCIPAL1,DmtPrincipalPlugin.dataRootURI,DmtSession.LOCK_TYPE_ATOMIC);
		value = session.getNodeValue(PRINCIPAL1_HASH+"/Principal");
		assertEquals(PRINCIPAL1,value.getString());
		session.close();

		// try to read from the principal tree as "principal2", which does not have any rights
		// TODO: as I understand it, this should fail. check with dmt impl
		session = dmtAdmin.getSession("principal2",DmtPrincipalPlugin.dataRootURI,DmtSession.LOCK_TYPE_ATOMIC);
		value = session.getNodeValue(PRINCIPAL1_HASH+"/Principal");
		assertEquals(PRINCIPAL1,value.getString());
		session.close();
	
	}
	
	public void testAccessControl() throws Exception {
		startFramework(true);
		final DmtSession session = dmtAdmin.getSession(PermissionAdminPlugin.dataRootURI);
		
		// this is a call with allpermission
		session.getChildNodeNames(PermissionAdminPlugin.dataRootURI);
//		session.getChildNodeNames(""); TODO

		// this is a call with read permission
		Permissions permissions = new Permissions();
		permissions.add(new DmtPermission(PermissionAdminPlugin.dataRootURI,"Get"));
		AccessController.doPrivileged(new PrivilegedExceptionAction() {
			public Object run() throws DmtException {
				session.getChildNodeNames(PermissionAdminPlugin.dataRootURI);
				return null;
			}
		
		},
		new AccessControlContext(new ProtectionDomain[] {new ProtectionDomain(null,permissions)}));

		// and here, no permissions
		permissions = new Permissions();
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws DmtException {
					session.getChildNodeNames(PermissionAdminPlugin.dataRootURI);
					return null;
				}
			
			},
			new AccessControlContext(new ProtectionDomain[] {new ProtectionDomain(null,permissions)}));
			fail();
		} catch (AccessControlException e) {}
		
	}
	
	public void testPermissionAdmin() throws Exception {
		startFramework(true);
		
		DmtSession session = dmtAdmin.getSession(PermissionAdminPlugin.dataRootURI,DmtSession.LOCK_TYPE_ATOMIC);
		String value = session.getNodeValue("Default/PermissionInfo").getString();
		// the default permission is already set at startup, let's check if it is there
		assertEquals(new PermissionInfo(PackagePermission.class.getName(),"*","IMPORT").getEncoded()+"\n",value);

		session.createInteriorNode("1");
		session.setNodeValue("1/Location",new DmtData("http://location1"));
		PermissionInfo pi = new PermissionInfo(AdminPermission.class.getName(),"*","*");
		session.setNodeValue("1/PermissionInfo",new DmtData(pi.getEncoded()));
		session.close();
		
		PermissionInfo[] permissions = permissionAdmin.getPermissions("http://location1");
		assertEquals(1,permissions.length);
		assertEquals(permissions[0],pi);
	}

	public void testConditionalPermissionAdminBasic() throws Exception {
		startFramework(true);
		ConditionInfo cond1 =  new ConditionInfo(BundleSignerCondition.class.getName(),new String[] {"*"});
		PermissionInfo perm1 = new PermissionInfo(AdminPermission.class.getName(),"*","*");
		conditionalPermissionAdmin.addConditionalPermissionInfo(
				new ConditionInfo[] { cond1 },
				new PermissionInfo[] { perm1  }
				);
		String nameHash = "pOWUUF7ZBzn2HHOc26VuZdn3RWI";
		
		DmtSession session = dmtAdmin.getSession(ConditionalPermissionAdminPlugin.dataRootURI,DmtSession.LOCK_TYPE_ATOMIC);

		String names[] = session.getChildNodeNames(ConditionalPermissionAdminPlugin.dataRootURI);
		
		String conditionInfo = session.getNodeValue(nameHash+"/ConditionInfo").getString();
		assertEquals(cond1.getEncoded()+"\n",conditionInfo);
		String permissionInfo = session.getNodeValue(nameHash+"/PermissionInfo").getString();
		assertEquals(perm1.getEncoded()+"\n",permissionInfo);

		session.deleteNode(nameHash);
		session.close();
		
		// since we deleted it, conditions should be empty
		Enumeration cpis = conditionalPermissionAdmin.getConditionalPermissionInfos();
		assertFalse(cpis.hasMoreElements());
	}
	
	/**
	 * we set integration bundle 1 to have all permissions, via the dmt tree.
	 * Then try to do something in its name.
	 * TODO currently this test fails, since the framework doesn't seem to implement signer conditions
	 */
	public void testConditionalPermissionAdminThrough() throws Exception {
		ConditionInfo SIGNER_SARAH =  new ConditionInfo(BundleSignerCondition.class.getName(),new String[] {"CN=Sarah Bar, OU=Informatical Infrastructure Management, O=ConstructionOy, C=HU; CN=People, OU=Informatical Infrastructure Maintenance, O=ConstructionOy, L=Budapest, C=HU; CN=Root1, OU=FAKEDONTUSE, O=CASoft, L=Budapest, C=HU"});
		PermissionInfo ADMINPERMISSION = new PermissionInfo(AdminPermission.class.getName(),"*","*");
		
		startFramework(true);

		PrivilegedExceptionAction adminAction = (PrivilegedExceptionAction) new PrivilegedExceptionAction() {
			public Object run() throws Exception {
				System.getSecurityManager().checkPermission(new AdminPermission());
				return null;
			}
		};
		
		// since we haven't given permissions yet, this should fail
		try {
			bundle1DoAction.invoke(null,new Object[]{adminAction});
			fail();
		} catch (InvocationTargetException e) {
			if (!(e.getCause() instanceof AccessControlException)) throw e;
		}
		
		DmtSession session = dmtAdmin.getSession(ConditionalPermissionAdminPlugin.dataRootURI,DmtSession.LOCK_TYPE_ATOMIC);
		session.createInteriorNode("1");
		session.setNodeValue("1/ConditionInfo",new DmtData(SIGNER_SARAH.getEncoded()));
		session.setNodeValue("1/PermissionInfo",new DmtData(ADMINPERMISSION.getEncoded()));
		session.close();
		
		// now this should work;
		bundle1DoAction.invoke(null,new Object[]{adminAction});
	}
	
}
