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

package java.security;
public abstract class AlgorithmParametersSpi {
	public AlgorithmParametersSpi() { }
	protected abstract byte[] engineGetEncoded() throws java.io.IOException;
	protected abstract byte[] engineGetEncoded(java.lang.String var0) throws java.io.IOException;
	protected abstract java.security.spec.AlgorithmParameterSpec engineGetParameterSpec(java.lang.Class var0) throws java.security.spec.InvalidParameterSpecException;
	protected abstract void engineInit(byte[] var0) throws java.io.IOException;
	protected abstract void engineInit(byte[] var0, java.lang.String var1) throws java.io.IOException;
	protected abstract void engineInit(java.security.spec.AlgorithmParameterSpec var0) throws java.security.spec.InvalidParameterSpecException;
	protected abstract java.lang.String engineToString();
}

