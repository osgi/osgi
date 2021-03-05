/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.permissionadmin.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * Used to exit from the permissions calling context.
 * 
 * @author Petia Sotirova
 * @version 1.0
 */
public interface PermissionSignatureTBCService {

	// from Bundle class
	public Dictionary<String,String> callBundle_getHeaders(Bundle bundle);

	public Dictionary<String,String> callBundle_getHeaders(Bundle bundle,
			String localeString);

	public String callBundle_getLocation(Bundle bundle);

	public URL callBundle_getResource(Bundle bundle, String name);

	public Enumeration<URL> callBundle_getResources(Bundle bundle, String name)
			throws IOException;

	public URL callBundle_getEntry(Bundle bundle, String name);

	public Enumeration<String> callBundle_getEntryPaths(Bundle bundle,
			String path);

	public Class< ? > callBundle_loadClass(Bundle bundle, String name)
			throws ClassNotFoundException;

	public void callBundle_stop(Bundle bundle) throws BundleException;

	public void callBundle_uninstall(Bundle bundle) throws BundleException;

	public void callBundle_update(Bundle bundle) throws BundleException;

	public void callBundle_update(Bundle bundle, InputStream in)
			throws BundleException;

	public void callBundle_start(Bundle bundle) throws BundleException;

	// from BundleContext class
	public Bundle callBundleContext_installBundle(BundleContext context,
			String location) throws BundleException;

	public Bundle callBundleContext_installBundle(BundleContext context,
			String location, InputStream input) throws BundleException;

	public void callBundleContext_addBundleListener(BundleContext context);

	public void callBundleContext_removeBundleListener(BundleContext context);

	// from StartLevel service
	public void callStartLevel_setBundleStartLevel(Bundle bundle,
			Integer startlevel);

	public void callStartLevel_setStartLevel(Integer startlevel);

	public void callStartLevel_setInitialBundleStartLevel(Integer startlevel);

	// from PermisssionAdmin service
	public void callPermissionAdmin_setPermissions(String location,
			PermissionInfo[] permissions);

	public void callPermissionAdmin_setDefaultPermissions(
			PermissionInfo[] permissions);

	// from PackageAdmin service
	public void callPackageAdmin_refreshPackages(Bundle[] bundles);

	public boolean callPackageAdmin_resolveBundles(Bundle[] bundles);

	// from FrameworkWiring
	public void callFrameworkWiring_refreshBundles(Bundle... bundles);
	
	public boolean callFrameworkWiring_resolveBundles(Bundle... bundles);
	
	// from BundleStartLevel
	public void callBundleStartLevel_setStartLevel(Bundle bundle,
			Integer startlevel);

	// from FrameworkStartLevel
	public void callFrameworkStartLevel_setStartLevel(Integer startlevel);

	public void callFrameworkStartLevel_setInitialBundleStartLevel(
			Integer startlevel);
}
