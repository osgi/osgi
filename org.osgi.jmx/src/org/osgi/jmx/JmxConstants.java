/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.jmx;

/** 
 * 
 */
public class JmxConstants {
	private JmxConstants() {
		// empty
	}

	/**
	 * The domain name of the core OSGi MBeans
	 */
	public static final String CORE = "osgi.core";

	/**
	 * The domain name of the selected OSGi compendium MBeans
	 */
	public static final String COMPENDIUM = "osgi.compendium";

	/**
	 * The fully qualified object name of the <link>BundleStateMBean</link>
	 */
	public static final String BUNDLE_STATE = CORE + ":type=bundleState";

	/**
	 * The fully qualified object name of the
	 * <link>ConfigAdminManagerMBean</link>
	 */
	public static final String CM_SERVICE = COMPENDIUM + ":service=cm";

	/**
	 * The fully qualified object name of the <link>FrameworkMBean</link>
	 */
	public static final String FRAMEWORK = CORE + ":type=framework";

	/**
	 * The fully qualified object name of the
	 * <link>PermissionManagerMBean</link>
	 */
	public static final String PA_SERVICE = COMPENDIUM
			+ ":service=permissionadmin";

	/**
	 * The fully qualified object name of the <link>PackageStateMBean</link>
	 */
	public static final String PACKAGE_STATE = CORE + ":type=packageState";

	/**
	 * The fully qualified object name of the <link>ProvisioningMBean</link>
	 */
	public static final String PS_SERVICE = COMPENDIUM + ":service=manager";

	/**
	 * The fully qualified object name of the <link>ServiceStateMBean</link>
	 */
	public static final String SERVICE_STATE = CORE + ":type=serviceState";

	/**
	 * The fully qualified object name of the <link>UserManagerMBean</link>
	 */
	public static final String UA_SERVICE = COMPENDIUM + ":service=userAdmin";

}
