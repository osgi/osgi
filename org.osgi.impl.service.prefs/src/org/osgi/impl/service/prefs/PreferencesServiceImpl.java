/*
 * @(#)PreferencesServiceImpl.java	1.5 01/07/18
 * $Header$
 *
 
 * 
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * Copyright (c) IBM Corporation (2004)
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 * 
 */
package org.osgi.impl.service.prefs;

import java.io.File;
import java.security.*;
import java.util.*;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.*;

/**
 * @version $Revision$
 */
public class PreferencesServiceImpl implements PreferencesService {
	private static final int	INIT_HASHTABLE_SIZE		= 3;
	private File				prefsRootDir;
	private File				usersRootDir;
	private Preferences			systemPreferences		= null;
	private Hashtable			userPreferencesTable	= null;

	PreferencesServiceImpl(BundleContext bundleContext, long bundleId) {
		prefsRootDir = bundleContext.getDataFile(Long.toString(bundleId));
		checkDirectory(prefsRootDir);
		usersRootDir = new File(prefsRootDir, "users");
		checkDirectory(usersRootDir);
		userPreferencesTable = new Hashtable(INIT_HASHTABLE_SIZE);
	}

	private void checkDirectory(final File dir) {
		//j2security
		Boolean success = (Boolean) AccessController
				.doPrivileged(new PrivilegedAction() {
					public Object run() {
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

	public synchronized Preferences getSystemPreferences() {
		if (systemPreferences == null) {
			File file = new File(prefsRootDir, "system.prefs");
			File tmpFile = new File(prefsRootDir, "system.tmp");
			systemPreferences = new SimpleRootPref(file, tmpFile, this, null);
		}
		return systemPreferences;
	}

	public synchronized Preferences getUserPreferences(String user) {
		Preferences userPreferences = (Preferences) userPreferencesTable
				.get(user);
		if (userPreferences == null) {
			File file = new File(usersRootDir, user + ".prefs");
			File tmpFile = new File(usersRootDir, user + ".tmp");
			userPreferences = new SimpleRootPref(file, tmpFile, this, user);
			userPreferencesTable.put(user, userPreferences);
		}
		return userPreferences;
	}
	
	synchronized void removeRootNode(String user) {	// RFC 60
		if (user == null) {
			systemPreferences = null;
		}
		else {
			userPreferencesTable.remove(user);
		}
	}

	public synchronized String[] getUsers() {
		Enumeration enum = userPreferencesTable.keys();
		String[] result = new String[userPreferencesTable.size()];
		for (int i = 0; enum.hasMoreElements(); i++) {
			result[i] = (String) enum.nextElement();
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
			Enumeration elements = userPreferencesTable.elements();
			while (elements.hasMoreElements()) {
				Preferences userRoot = (Preferences) elements.nextElement();
				userRoot.flush();
			}
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
