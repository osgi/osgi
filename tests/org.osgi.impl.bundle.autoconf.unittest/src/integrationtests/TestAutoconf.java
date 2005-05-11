/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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
 */

package integrationtests;

import java.util.Dictionary;
import java.util.Hashtable;

import integrationtests.api.ITest;
import junit.framework.Test;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.bundle.autoconf.Autoconf;
import org.osgi.impl.service.policy.integrationtests.IntegratedTest;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

public class TestAutoconf extends IntegratedTest implements Test {
	public static final String	ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR	= "file:../../org.osgi.impl.service.deploymentadmin/org.osgi.impl.service.deploymentadmin.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_METATYPE_JAR	= "file:../../org.osgi.impl.service.metatype/org.osgi.impl.service.metatype.jar";
	public static final String  ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR = "file:../../org.osgi.impl.bundle.autoconf/org.osgi.impl.bundle.autoconf.jar";
	public static final String	INTEGRATIONTESTS_MANAGEDSERVICE1_JAR = "file:../../org.osgi.impl.bundle.autoconf.unittest/integrationtests.managedservice1.jar";

	public Bundle	deploymentAdmin;
	public Bundle	autoconf;
	public Bundle	metatype;
	public MetaTypeService	metaTypeService;
	
	public void startFramework(boolean fresh) throws Exception {
		super.startFramework(fresh);
		if (fresh) {
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_METATYPE_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR);
		}
		deploymentAdmin = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR);
		metatype = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_METATYPE_JAR);
		autoconf = systemBundleContext.installBundle(ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR);
		deploymentAdmin.start();
		metatype.start();
		autoconf.start();

		ServiceReference sr = systemBundleContext.getServiceReference(MetaTypeService.class.getName());
		metaTypeService = (MetaTypeService) systemBundleContext.getService(sr);
		
		// component service bundle has some race condition issues
		synchronized(this) { this.wait(100); }
	}

	public void stopFramework() throws Exception {
		deploymentAdmin = null;
		autoconf = null;
		super.stopFramework();
	}

	public void testRegistered() throws Exception {
		startFramework(true);
		ServiceReference[] sr = systemBundleContext.getServiceReferences(ResourceProcessor.class.getName(),"(processor=AutoconfProcessor)");
		assertEquals(1,sr.length);
		Object sp = systemBundleContext.getService(sr[0]);
		assertEquals(Autoconf.class.getName(),sp.getClass().getName()); // different classloaders!
	}
	
	public void testManagedServiceWorks() throws Exception {
		startFramework(true);
		setBundleAsAdministrator(INTEGRATIONTESTS_MANAGEDSERVICE1_JAR);
		Bundle managedService1 = systemBundleContext.installBundle(INTEGRATIONTESTS_MANAGEDSERVICE1_JAR);
		managedService1.start();

		ServiceReference sr = systemBundleContext.getServiceReference(ITest.class.getName());
		ITest iTest = (ITest) systemBundleContext.getService(sr);

		sr = systemBundleContext.getServiceReference(ConfigurationAdmin.class.getName());
		ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) systemBundleContext.getService(sr);
		Configuration conf = configurationAdmin.getConfiguration("integrationtests.managedservice1.pid");
		conf.setBundleLocation(INTEGRATIONTESTS_MANAGEDSERVICE1_JAR);
		Hashtable props = new Hashtable();
		props.put("increment",new Integer(2));
		conf.update(props);

		int i = iTest.succ(3);
		assertEquals(5,i);
	}
}
