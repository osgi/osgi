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
public abstract interface Certificate {
	public abstract void decode(java.io.InputStream var0) throws java.security.KeyException, java.io.IOException;
	public abstract void encode(java.io.OutputStream var0) throws java.security.KeyException, java.io.IOException;
	public abstract java.lang.String getFormat();
	public abstract java.security.Principal getGuarantor();
	public abstract java.security.Principal getPrincipal();
	public abstract java.security.PublicKey getPublicKey();
	public abstract java.lang.String toString(boolean var0);
}

