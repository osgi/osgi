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

package java.security.interfaces;
public abstract interface RSAPrivateCrtKey extends java.security.interfaces.RSAPrivateKey {
	public abstract java.math.BigInteger getPublicExponent();
	public abstract java.math.BigInteger getPrimeP();
	public abstract java.math.BigInteger getPrimeQ();
	public abstract java.math.BigInteger getPrimeExponentP();
	public abstract java.math.BigInteger getPrimeExponentQ();
	public abstract java.math.BigInteger getCrtCoefficient();
}

