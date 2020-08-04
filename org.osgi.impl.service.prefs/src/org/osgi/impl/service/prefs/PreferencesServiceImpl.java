/*
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * Copyright (c) IBM Corporation (2004)
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 */
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
