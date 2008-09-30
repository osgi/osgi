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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
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

import org.eclipse.core.runtime.adaptor.LocationManager;
import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;
import org.eclipse.osgi.framework.internal.core.FrameworkSecurityManager;
import org.eclipse.osgi.framework.internal.core.OSGi;
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
	public static final String  ORG_OSGI_IMPL_BUNDLE_JAXP_JAR = "file:../../org.osgi.impl.bundle.jaxp/org.osgi.impl.bundle.jaxp.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_DMT_JAR	= "file:../../org.osgi.impl.service.dmt/org.osgi.impl.service.dmt.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_LOG_JAR	= "file:../../org.osgi.impl.service.log/org.osgi.impl.service.log.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_CM_JAR	= "file:../../org.osgi.impl.service.cm/org.osgi.impl.service.cm.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_EVENT_JAR	= "file:../../org.osgi.impl.service.event/org.osgi.impl.service.event.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_COMPONENT_JAR = "file:../../org.osgi.impl.service.component/org.osgi.impl.service.component.jar";
	public static final String	INTEGRATIONTESTS_BUNDLE1_JAR = "file:../../org.osgi.impl.service.policy.unittest/integrationtests.bundle1.jar";
	public static final String	INTEGRATIONTESTS_BUNDLE2_JAR = "file:../../org.osgi.impl.service.policy.unittest/integrationtests.bundle2.jar";
	public static final String  CONSOLE_JAR = "file:../../osgi.test/org.osgi.tools.console.jar";

	public FrameworkSecurityManager	secMan;
	public FrameworkAdaptor adaptor;
	public BundleContext	systemBundleContext;
	public Bundle	osgiAPIsBundle;
	public Bundle	jaxp;
	public Bundle	eventBundle;
	public Bundle	configManagerBundle;
	public Bundle	logBundle;
	public Bundle	dmtBundle;
	public Bundle	serviceComponent;
	public Bundle	integrationTestBundle1;
	public Bundle	integrationTestBundle2;
	public Bundle	console;
	public OSGi	framework;
	public PermissionAdmin	permissionAdmin;
	public ConditionalPermissionAdmin	conditionalPermissionAdmin;
	public Method	bundle1DoAction;
	public Method	bundle2DoAction;

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

	public void tearDown() throws Exception {
		stopFramework();
	}

	public void setBundleAsAdministrator(String location) throws Exception {
		permissionAdmin.setPermissions(location,new PermissionInfo[] {
				new PermissionInfo(AllPermission.class.getName(),"*","*")});
		
	}

	/**
	 * Starts the OSGi R4 RI framework.
	 * @param fresh If true, then clean up any previous persistent data found in storage.
	 * 			At the beginning of a unit test, you MUST set this true, otherwise data
	 * 			from the previous runs may affect test behaviour. If you need to test shutdown/startup
	 * 			consistency in a module, you may call {@link IntegratedTest#stopFramework()}, and
	 * 			then <code>startFramework(false)</code>.
	 * @throws Exception
	 */
	public void startFramework(boolean fresh) throws Exception {
		System.setSecurityManager(null);
		Policy.setPolicy(new VeryGenerousPolicy());
		cleanAllFactories();
		
		// replace policy file ${user.home}/.java.policy with our own
		Security.setProperty("policy.url.2","file:policy");
		
		if (fresh) {
			// we clear out everything
			URI u = new URI(System.getProperty(LocationManager.PROP_INSTALL_AREA));
			File f = new File(u);
			assertTrue(f.exists());
			delete(new File(f,"configuration"));
		}
		secMan = new FrameworkSecurityManager();
		System.setSecurityManager(secMan);
		adaptor = new BaseAdaptor(null);
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
			
			
			setBundleAsAdministrator(ORG_OSGI_IMPL_BUNDLE_JAXP_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_EVENT_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_CM_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_LOG_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_DMT_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_COMPONENT_JAR);
			setBundleAsAdministrator(CONSOLE_JAR);
		} 

		jaxp = systemBundleContext.installBundle(ORG_OSGI_IMPL_BUNDLE_JAXP_JAR);
		eventBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_EVENT_JAR);
		configManagerBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_CM_JAR);
		logBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_LOG_JAR);
		dmtBundle = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_DMT_JAR);
		serviceComponent = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_COMPONENT_JAR);
		integrationTestBundle1 = systemBundleContext.installBundle(INTEGRATIONTESTS_BUNDLE1_JAR);
		integrationTestBundle2 = systemBundleContext.installBundle(INTEGRATIONTESTS_BUNDLE2_JAR);
		console = systemBundleContext.installBundle(CONSOLE_JAR);

		jaxp.start();
		eventBundle.start();
		configManagerBundle.start();
		logBundle.start();
		dmtBundle.start();
		serviceComponent.start();
		console.start();

		Class cl = integrationTestBundle1.loadClass("org.osgi.impl.service.policy.integrationtests.bundle1.Test");
		bundle1DoAction = cl.getDeclaredMethod("doAction",new Class[]{PrivilegedExceptionAction.class});

		cl = integrationTestBundle2.loadClass("org.osgi.impl.service.policy.integrationtests.bundle2.Test");
		bundle2DoAction = cl.getDeclaredMethod("doAction",new Class[]{PrivilegedExceptionAction.class});

	}

	/**
	 * deletes a dir recursively
	 * @param f
	 */
	private static void delete(File f) {
		File[] subfiles = f.listFiles();
		if (subfiles!=null) {
			for(int i=0;i<subfiles.length;i++) delete(subfiles[i]);
		}
		f.delete();
	}

	public void stopFramework() throws Exception {
		if (serviceComponent!=null) serviceComponent.stop();
		if (framework!=null && framework.isActive()) framework.shutdown();
		framework = null;
		System.setSecurityManager(null);
		Policy.setPolicy(null);
		secMan = null;
		adaptor = null;
		systemBundleContext = null;
		osgiAPIsBundle = null;
		eventBundle = null;
		configManagerBundle = null;
		logBundle = null;
		dmtBundle = null;
		integrationTestBundle1 = null;
		permissionAdmin = null;
		conditionalPermissionAdmin = null;
		bundle1DoAction = null;

		cleanAllFactories();
	}
	
	/**
	 * There are some factories that can only be set once. Since in the unit tests,
	 * we constantly start and stop the framework, and the framework sets these factories,
	 * we need to clean up.
	 */
	public void cleanAllFactories() throws Exception {
		Field urlFactory = URL.class.getDeclaredField("factory");
		urlFactory.setAccessible(true);
		urlFactory.set(null,null);
		
		Field urlConnectionFactory = URLConnection.class.getDeclaredField("factory");
		urlConnectionFactory.setAccessible(true);
		urlConnectionFactory.set(null,null);
		
	}

}
