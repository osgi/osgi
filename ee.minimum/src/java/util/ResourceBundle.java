/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package java.util;
public abstract class ResourceBundle {
	public ResourceBundle() { }
	public final static java.util.ResourceBundle getBundle(java.lang.String var0) throws java.util.MissingResourceException { return null; }
	public final static java.util.ResourceBundle getBundle(java.lang.String var0, java.util.Locale var1) { return null; }
	public static java.util.ResourceBundle getBundle(java.lang.String var0, java.util.Locale var1, java.lang.ClassLoader var2) throws java.util.MissingResourceException { return null; }
	public abstract java.util.Enumeration getKeys();
	public java.util.Locale getLocale() { return null; }
	public final java.lang.Object getObject(java.lang.String var0) throws java.util.MissingResourceException { return null; }
	public final java.lang.String getString(java.lang.String var0) throws java.util.MissingResourceException { return null; }
	public final java.lang.String[] getStringArray(java.lang.String var0) throws java.util.MissingResourceException { return null; }
	protected abstract java.lang.Object handleGetObject(java.lang.String var0) throws java.util.MissingResourceException;
	protected void setParent(java.util.ResourceBundle var0) { }
	protected java.util.ResourceBundle parent;
}

