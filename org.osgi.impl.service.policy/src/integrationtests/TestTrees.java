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

public class TestTrees extends TestCase {

	
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
	public void testAllStartsUp() throws Exception {
		Policy.setPolicy(new VeryGenerousPolicy());
		SecurityManager secMan = new FrameworkSecurityManager();
		System.setSecurityManager(secMan);
		DefaultAdaptor adaptor = new DefaultAdaptor(new String[] { "reset" });
		OSGi osgi = new OSGi(adaptor);
		osgi.launch();
		BundleContext bc = osgi.getBundleContext();
		Bundle osgiBundle = bc.installBundle("file:../../osgi.released/osgi.jar");
		osgiBundle.start();
		Bundle event = bc.installBundle("file:../org.osgi.impl.service.event.jar");
		event.start();
		Bundle eventMapperBundle = bc.installBundle("file:../org.osgi.impl.service.event.mapper.jar");
		eventMapperBundle.start();
		Bundle configManagerBunde = bc.installBundle("file:../org.osgi.impl.service.cm.jar");
		configManagerBunde.start();
		Bundle logBundle = bc.installBundle("file:../org.osgi.impl.service.log.jar");
		logBundle.start();
		Bundle dmtBundle = bc.installBundle("file:../org.osgi.impl.service.dmt.jar");
		dmtBundle.start();
		Bundle policyBundle = bc.installBundle("file:../org.osgi.impl.service.policy.jar");
		policyBundle.start();

		// check if all three policy trees are registered
		ServiceReference[] sr;
		sr = bc.getServiceReferences(DmtDataPlugin.class.getName(),"(dataRootURIs="+PermissionAdminPlugin.dataRootURI+")");
		assertNotNull(sr[0]);
		sr = bc.getServiceReferences(DmtDataPlugin.class.getName(),"(dataRootURIs="+ConditionalPermissionAdminPlugin.dataRootURI+")");
		assertNotNull(sr[0]);
		sr = bc.getServiceReferences(DmtDataPlugin.class.getName(),"(dataRootURIs="+DmtPrincipalPlugin.dataRootURI+")");
		assertNotNull(sr[0]);
	}
}
