/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security.interfaces;
public abstract interface DSAKeyPairGenerator {
	public abstract void initialize(java.security.interfaces.DSAParams var0, java.security.SecureRandom var1) throws java.security.InvalidParameterException;
	public abstract void initialize(int var0, boolean var1, java.security.SecureRandom var2) throws java.security.InvalidParameterException;
}

