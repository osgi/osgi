/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.security.interfaces;
public abstract interface RSAPrivateKey extends java.security.PrivateKey, java.security.interfaces.RSAKey {
	public abstract java.math.BigInteger getPrivateExponent();
}

