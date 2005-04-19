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
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import junit.framework.TestCase;
import org.eclipse.osgi.framework.internal.core.FrameworkSecurityManager;
import org.eclipse.osgi.framework.internal.core.OSGi;
import org.eclipse.osgi.framework.internal.defaultadaptor.DefaultAdaptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public abstract class IntegratedTest extends TestCase {
	public static final String	ORG_OSGI_IMPL_SERVICE_POLICY_JAR	= "file:../../org.osgi.impl.service.policy/org.osgi.impl.service.policy.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_DMT_JAR	= "file:../../org.osgi.impl.service.dmt/org.osgi.impl.service.dmt.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_LOG_JAR	= "file:../../org.osgi.impl.service.log/org.osgi.impl.service.log.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_CM_JAR	= "file:../../org.osgi.impl.service.cm/org.osgi.impl.service.cm.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_EVENT_MAPPER_JAR	= "file:../../org.osgi.impl.service.event/org.osgi.impl.service.event.mapper.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_EVENT_JAR	= "file:../../org.osgi.impl.service.event/org.osgi.impl.service.event.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_POLICY_USERPROMPT_JAR = "file:../../org.osgi.impl.service.policy/org.osgi.impl.service.policy.userprompt.jar";
	public static final String	INTEGRATIONTESTS_BUNDLE1_JAR = "file:../integrationtests.bundle1.jar";
	public static final String	INTEGRATIONTESTS_MESSAGES_JAR = "file:../integrationtests.messages.jar";

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
	public Bundle	userPromptBundle;
	public Bundle	integrationTestBundle;
	public Bundle	integrationTestMessagesBundle;
	public OSGi	framework;
	public PermissionAdmin	permissionAdmin;
	public ConditionalPermissionAdmin	conditionalPermissionAdmin;
	public Method	bundle1DoAction;

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
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_POLICY_USERPROMPT_JAR);
			permissionAdmin.setPermissions(INTEGRATIONTESTS_MESSAGES_JAR,
					new PermissionInfo[]{
						new PermissionInfo(PackagePermission.class.getName(),"*","EXPORT")
					}
				);
			
		} 

		eventBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_EVENT_JAR);
		eventMapperBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_EVENT_MAPPER_JAR);
		configManagerBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_CM_JAR);
		logBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_LOG_JAR);
		dmtBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_DMT_JAR);
		policyBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_POLICY_JAR);
		userPromptBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_POLICY_USERPROMPT_JAR);
		integrationTestBundle = systemBundleContext.installBundle(INTEGRATIONTESTS_BUNDLE1_JAR);
		integrationTestMessagesBundle = systemBundleContext.installBundle(INTEGRATIONTESTS_MESSAGES_JAR);

		eventBundle.start();
		eventMapperBundle.start();
		configManagerBundle.start();
		logBundle.start();
		dmtBundle.start();
		policyBundle.start();
		userPromptBundle.start();
		integrationTestMessagesBundle.start();

		Class cl = integrationTestBundle.loadClass("org.osgi.impl.service.policy.integrationtests.bundle1.Test");
		bundle1DoAction = cl.getDeclaredMethod("doAction",new Class[]{PrivilegedExceptionAction.class});

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
		bundle1DoAction = null;

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

}
