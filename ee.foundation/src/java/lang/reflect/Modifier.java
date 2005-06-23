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

package java.lang.reflect;
public class Modifier {
	public Modifier() { }
	public static boolean isAbstract(int var0) { return false; }
	public static boolean isFinal(int var0) { return false; }
	public static boolean isInterface(int var0) { return false; }
	public static boolean isNative(int var0) { return false; }
	public static boolean isPrivate(int var0) { return false; }
	public static boolean isProtected(int var0) { return false; }
	public static boolean isPublic(int var0) { return false; }
	public static boolean isStatic(int var0) { return false; }
	public static boolean isStrict(int var0) { return false; }
	public static boolean isSynchronized(int var0) { return false; }
	public static boolean isTransient(int var0) { return false; }
	public static boolean isVolatile(int var0) { return false; }
	public static java.lang.String toString(int var0) { return null; }
	public final static int PUBLIC = 1;
	public final static int PRIVATE = 2;
	public final static int PROTECTED = 4;
	public final static int STATIC = 8;
	public final static int FINAL = 16;
	public final static int SYNCHRONIZED = 32;
	public final static int VOLATILE = 64;
	public final static int TRANSIENT = 128;
	public final static int NATIVE = 256;
	public final static int INTERFACE = 512;
	public final static int ABSTRACT = 1024;
	public final static int STRICT = 2048;
}

