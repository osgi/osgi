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
package integrationtests;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import junit.framework.TestCase;
import org.eclipse.osgi.framework.internal.core.FrameworkSecurityManager;
import org.eclipse.osgi.framework.internal.core.OSGi;
import org.eclipse.osgi.framework.internal.defaultadaptor.DefaultAdaptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.policy.condpermadmin.ConditionalPermissionAdminPlugin;
import org.osgi.impl.service.policy.dmtprincipal.DmtPrincipalPlugin;
import org.osgi.impl.service.policy.permadmin.PermissionAdminPlugin;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

public class TestTrees extends TestCase {


	
	public FrameworkSecurityManager	secMan;
	public DefaultAdaptor adaptor;
	private BundleContext	systemBundleContext;
	private Bundle	osgiAPIsBundle;
	private Bundle	eventBundle;
	private Bundle	eventMapperBundle;
	private Bundle	configManagerBundle;
	private Bundle	logBundle;
	private Bundle	dmtBundle;
	private Bundle	policyBundle;
	private OSGi	framework;
	
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

	public void startFramework() throws Exception {
		Policy.setPolicy(new VeryGenerousPolicy());
		secMan = new FrameworkSecurityManager();
		System.setSecurityManager(secMan);
		adaptor = new DefaultAdaptor(new String[] { "reset" });
		framework = new OSGi(adaptor);
		framework.launch();
		systemBundleContext = framework.getBundleContext();
		
		ServiceReference permissionAdminRef = systemBundleContext.getServiceReference(PermissionAdmin.class.getName());
		PermissionAdmin permissionAdmin = (PermissionAdmin) systemBundleContext.getService(permissionAdminRef);
		//permissionAdmin.setDefaultPermissions(new PermissionInfo[] { });
		
		eventBundle = systemBundleContext.installBundle("file:../org.osgi.impl.service.event.jar");
		eventBundle.start();
		eventMapperBundle = systemBundleContext.installBundle("file:../org.osgi.impl.service.event.mapper.jar");
		eventMapperBundle.start();
		configManagerBundle = systemBundleContext.installBundle("file:../org.osgi.impl.service.cm.jar");
		configManagerBundle.start();
		logBundle = systemBundleContext.installBundle("file:../org.osgi.impl.service.log.jar");
		logBundle.start();
		dmtBundle = systemBundleContext.installBundle("file:../org.osgi.impl.service.dmt.jar");
		dmtBundle.start();
		policyBundle = systemBundleContext.installBundle("file:../org.osgi.impl.service.policy.jar");
		policyBundle.start();
	}

	public void stopFramework() throws Exception {
		framework.shutdown();
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
		framework = null;
	}
	
	public void testAllStartsUp() throws Exception {
		startFramework();

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
}
