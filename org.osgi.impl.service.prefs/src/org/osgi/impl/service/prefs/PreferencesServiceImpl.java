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
package org.osgi.impl.service.prefs;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

/**
 * @author $Id$
 */
public class PreferencesServiceImpl implements PreferencesService {
	private static final int	INIT_HASHTABLE_SIZE		= 3;
	private File				prefsRootDir;
	private File				usersRootDir;
	private AbstractPreferences			systemPreferences		= null;
	private Hashtable<String,Preferences>	userPreferencesTable	= null;

	PreferencesServiceImpl(BundleContext bundleContext, long bundleId) {
		prefsRootDir = bundleContext.getDataFile(Long.toString(bundleId));
		checkDirectory(prefsRootDir);
		usersRootDir = new File(prefsRootDir, "users");
		checkDirectory(usersRootDir);
		userPreferencesTable = new Hashtable<>(INIT_HASHTABLE_SIZE);
	}

	private void checkDirectory(final File dir) {
		//j2security
		Boolean success = AccessController
				.doPrivileged(new PrivilegedAction<Boolean>() {
					@Override
					public Boolean run() {
						if (!dir.exists() && !dir.mkdir()) {
							return Boolean.FALSE;
						}
						else {
							return Boolean.TRUE;
						}
					}
				});
		if (!(success.booleanValue())) {
			throw new RuntimeException("could not make directory " + dir);
		}
		//endblock
		//!j2security
		//	if (!dir.exists() && !dir.mkdir()) {
		//	    throw new RuntimeException("could not make directory " + dir);
		//	}
		//endblock
	}

	@Override
	public synchronized Preferences getSystemPreferences() {
		if ((systemPreferences == null) || (systemPreferences.isRemoved())) {
			File file = new File(prefsRootDir, "system.prefs");
			File tmpFile = new File(prefsRootDir, "system.tmp");
			systemPreferences = new SimpleRootPref(file, tmpFile);
		}
		return systemPreferences;
	}

	@Override
	public synchronized Preferences getUserPreferences(String user) {
		AbstractPreferences userPreferences = (AbstractPreferences) userPreferencesTable
				.get(user);
		if ((userPreferences == null) || (userPreferences.isRemoved())) {
			File file = new File(usersRootDir, user + ".prefs");
			File tmpFile = new File(usersRootDir, user + ".tmp");
			userPreferences = new SimpleRootPref(file, tmpFile);
			userPreferencesTable.put(user, userPreferences);
		}
		return userPreferences;
	}
	
	@Override
	public synchronized String[] getUsers() {
		Enumeration<String> enumeration = userPreferencesTable.keys();
		String[] result = new String[userPreferencesTable.size()];
		for (int i = 0; enumeration.hasMoreElements(); i++) {
			result[i] = enumeration.nextElement();
		}
		return result;
	}

	/*
	 * flush all preference trees
	 */
	synchronized void flushAll() {
		try {
			if (systemPreferences != null) {
				systemPreferences.flush();
			}
			Enumeration<Preferences> elements = userPreferencesTable.elements();
			while (elements.hasMoreElements()) {
				Preferences userRoot = elements.nextElement();
				userRoot.flush();
			}
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
