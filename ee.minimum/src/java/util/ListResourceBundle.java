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
public abstract class ListResourceBundle extends java.util.ResourceBundle {
	public ListResourceBundle() { }
	protected abstract java.lang.Object[][] getContents();
	public java.util.Enumeration getKeys() { return null; }
	public final java.lang.Object handleGetObject(java.lang.String var0) { return null; }
}

