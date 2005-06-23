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

package java.text;
public abstract class Format implements java.io.Serializable, java.lang.Cloneable {
	public Format() { }
	public java.lang.Object clone() { return null; }
	public final java.lang.String format(java.lang.Object var0) { return null; }
	public abstract java.lang.StringBuffer format(java.lang.Object var0, java.lang.StringBuffer var1, java.text.FieldPosition var2);
	public java.lang.Object parseObject(java.lang.String var0) throws java.text.ParseException { return null; }
	public abstract java.lang.Object parseObject(java.lang.String var0, java.text.ParsePosition var1);
}

