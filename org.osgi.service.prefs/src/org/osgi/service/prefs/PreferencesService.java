/*
 * @(#)PreferencesService.java  1.4 01/07/18
 * $Header$
 *
 
 * 
 * (C) Copyright 1996-2001 Sun Microsystems, Inc. 
 * 
 * This source code is licensed to OSGi as MEMBER LICENSED MATERIALS 
 * under the terms of Section 3.2 of the OSGi MEMBER AGREEMENT.
 * 
 */
package org.osgi.service.prefs;

/**
 * The Preferences Service.
 * 
 * <p>
 * Each bundle using this service has its own set of preference trees: one for
 * system preferences, and one for each user.
 * 
 * <p>
 * A <tt>PreferencesService</tt> object is specific to the bundle which
 * obtained it from the service registry. If a bundle wishes to allow another
 * bundle to access its preferences, it should pass its
 * <tt>PreferencesService</tt> object to that bundle.
 *  
 */
public interface PreferencesService {
	/**
	 * Returns the root system node for the calling bundle.
	 */
	public abstract Preferences getSystemPreferences();

	/**
	 * Returns the root node for the specified user and the calling bundle.
	 */
	public abstract Preferences getUserPreferences(String name);

	/**
	 * Returns the names of users for which node trees exist.
	 */
	public abstract String[] getUsers();
}
