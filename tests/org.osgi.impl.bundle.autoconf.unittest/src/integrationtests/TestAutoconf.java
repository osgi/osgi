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

import org.osgi.framework.Bundle;
import org.osgi.impl.service.policy.integrationtests.IntegratedTest;

public class TestAutoconf extends IntegratedTest {
	public static final String	ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR	= "file:../../org.osgi.impl.service.deploymentadmin/org.osgi.impl.service.deploymentadmin.jar";
	public static final String	ORG_OSGI_IMPL_SERVICE_METATYPE_JAR	= "file:../../org.osgi.impl.service.metatype/org.osgi.impl.service.metatype.jar";
	public static final String  ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR = "file:../../org.osgi.impl.bundle.autoconf/org.osgi.impl.bundle.autoconf.jar";
	public static final String  ORG_OSGI_IMPL_BUNDLE_JAXP_JAR = "file:../../org.osgi.impl.bundle.jaxp/org.osgi.impl.bundle.jaxp.jar";
	public Bundle	deploymentAdmin;
	public Bundle	autoconf;
	public Bundle	jaxp;
	public Bundle	metatype;
	
	public void test1() throws Exception {
		startFramework(true);
	}

	public void startFramework(boolean fresh) throws Exception {
		super.startFramework(fresh);
		if (fresh) {
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_SERVICE_METATYPE_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_BUNDLE_JAXP_JAR);
			setBundleAsAdministrator(ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR);
		}
		deploymentAdmin = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_DEPLOYMENTADMIN_JAR);
		jaxp = systemBundleContext.installBundle(ORG_OSGI_IMPL_BUNDLE_JAXP_JAR);
		metatype = systemBundleContext.installBundle(ORG_OSGI_IMPL_SERVICE_METATYPE_JAR);
		autoconf = systemBundleContext.installBundle(ORG_OSGI_IMPL_BUNDLE_AUTOCONF_JAR);
		deploymentAdmin.start();
		jaxp.start();
		metatype.start();
		autoconf.start();
	}

	public void stopFramework() throws Exception {
		deploymentAdmin = null;
		autoconf = null;
		super.stopFramework();
	}

	
}
