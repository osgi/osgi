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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.util.Enumeration;
import junit.framework.TestCase;
import org.eclipse.osgi.framework.internal.core.FrameworkSecurityManager;
import org.eclipse.osgi.framework.internal.core.OSGi;
import org.eclipse.osgi.framework.internal.defaultadaptor.DefaultAdaptor;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.policy.condpermadmin.ConditionalPermissionAdminPlugin;
import org.osgi.impl.service.policy.dmtprincipal.DmtPrincipalPlugin;
import org.osgi.impl.service.policy.permadmin.PermissionAdminPlugin;
import org.osgi.service.condpermadmin.BundleSignerCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtPermission;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

public class TestTrees extends TestCase {
	public static final String	ORG_OSGI_IMPL_SERVICE_POLICY_JAR	= "file:../org.osgi.impl.service.policy.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_DMT_JAR	= "file:../../org.osgi.impl.service.dmt/org.osgi.impl.service.dmt.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_LOG_JAR	= "file:../../org.osgi.impl.service.log/org.osgi.impl.service.log.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_CM_JAR	= "file:../../org.osgi.impl.service.cm/org.osgi.impl.service.cm.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_EVENT_MAPPER_JAR	= "file:../../org.osgi.impl.service.event/org.osgi.impl.service.event.mapper.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_EVENT_JAR	= "file:../../org.osgi.impl.service.event/org.osgi.impl.service.event.jar";
	public static final String	INTEGRATIONTESTS_BUNDLE1_JAR = "file:../integrationtests.bundle1.jar";

	public static final String PRINCIPAL1 = "principal1";
	public static final String PRINCIPAL1_HASH = "zDcCo9K+A67rtQI3TQEDg6_LEIw";
	
	public FrameworkSecurityManager	secMan;
	public DefaultAdaptor adaptor;
	public BundleContext	systemBundleContext;
	public Bundle	osgiAPIsBundle;
	public Bundle	eventBundle;
	public Bundle	eventMapperBundle;
	public Bundle	configManagerBundle;
	public Bundle	logBundle;
	public Bundle	dmtBundle;
	public Bundle	policyBundle;
	public Bundle	integrationTestBundle;
	public OSGi	framework;
	public PermissionAdmin	permissionAdmin;
	public ConditionalPermissionAdmin	conditionalPermissionAdmin;
	public DmtAdmin	dmtAdmin;
	
	/**
	 * This policy implementation gives AllPermission to all code sources.
	 * Hopefully the framework will overide this for the bundle code sources.
	 */
	public static class VeryGenerousPolicy extends Policy {
		public void refresh() {}

		public PermissionCollection getPermissions(CodeSource codesource) {
			//System.out.println(codesource.getLocation());
			Permissions pc = new Permissions();
			pc.add(new AllPermission());
			return pc;
		}
		
	}

	public void setBundleAsAdministrator(String location) throws Exception {
		permissionAdmin.setPermissions(location,new PermissionInfo[] {
				new PermissionInfo(AllPermission.class.getName(),"*","*")});
		
	}
	
	public void startFramework(boolean fresh) throws Exception {
		Policy.setPolicy(new VeryGenerousPolicy());
		
		// replace policy file ${user.home}/.java.policy with our own
		Security.setProperty("policy.url.2","file:policy");
		
		secMan = new FrameworkSecurityManager();
		System.setSecurityManager(secMan);
		adaptor = new DefaultAdaptor(fresh?new String[] { "reset" }:null);
		framework = new OSGi(adaptor);
		framework.launch();
		systemBundleContext = framework.getBundleContext();
		
		ServiceReference sr = systemBundleContext.getServiceReference(PermissionAdmin.class.getName());
		permissionAdmin = (PermissionAdmin) systemBundleContext.getService(sr);
		
		sr = systemBundleContext.getServiceReference(ConditionalPermissionAdmin.class.getName());
		conditionalPermissionAdmin = (ConditionalPermissionAdmin) systemBundleContext.getService(sr);

		if (fresh) {
			// Warning! Don't do this on a real system!
			permissionAdmin.setDefaultPermissions(new PermissionInfo[] { 
					new PermissionInfo(PackagePermission.class.getName(),"*","IMPORT")
					});
			
			
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_EVENT_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_EVENT_MAPPER_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_CM_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_LOG_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_DMT_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_POLICY_JAR);
		} 

		eventBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_EVENT_JAR);
		eventMapperBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_EVENT_MAPPER_JAR);
		configManagerBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_CM_JAR);
		logBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_LOG_JAR);
		dmtBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_DMT_JAR);
		policyBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_POLICY_JAR);
		integrationTestBundle = systemBundleContext.installBundle(INTEGRATIONTESTS_BUNDLE1_JAR);

		eventBundle.start();
		eventMapperBundle.start();
		configManagerBundle.start();
		logBundle.start();
		dmtBundle.start();
		policyBundle.start();
		
		sr = systemBundleContext.getServiceReference(DmtAdmin.class.getName());
		dmtAdmin = (DmtAdmin)systemBundleContext.getService(sr);
		
	}

	public void stopFramework() throws Exception {
		if (framework!=null && framework.isActive()) framework.shutdown();
		framework = null;
		System.setSecurityManager(null);
		Policy.setPolicy(null);
		secMan = null;
		adaptor = null;
		systemBundleContext = null;
		osgiAPIsBundle = null;
		eventBundle = null;
		eventMapperBundle = null;
		configManagerBundle = null;
		logBundle = null;
		dmtBundle = null;
		policyBundle = null;
		integrationTestBundle = null;
		permissionAdmin = null;
		conditionalPermissionAdmin = null;
		dmtAdmin = null;

		// the framework needs to set these to its own implementation
		// And they can only be set once, so we need to
		// re-set them if we want to run a new framework instance.
		Field urlFactory = URL.class.getDeclaredField("factory");
		urlFactory.setAccessible(true);
		urlFactory.set(null,null);
		
		Field urlConnectionFactory = URLConnection.class.getDeclaredField("factory");
		urlConnectionFactory.setAccessible(true);
		urlConnectionFactory.set(null,null);
	}
	
	public void tearDown() throws Exception {
		stopFramework();
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

		Class cl = integrationTestBundle.loadClass("org.osgi.impl.service.policy.integrationtests.bundle1.Test");
		Method doAction = cl.getDeclaredMethod("doAction",new Class[]{PrivilegedExceptionAction.class});
		PrivilegedExceptionAction adminAction = (PrivilegedExceptionAction) new PrivilegedExceptionAction() {
			public Object run() throws Exception {
				System.getSecurityManager().checkPermission(new AdminPermission());
				return null;
			}
		};
		
		// since we haven't given permissions yet, this should fail
		try {
			doAction.invoke(null,new Object[]{adminAction});
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
		doAction.invoke(null,new Object[]{adminAction});
	}
	
}
