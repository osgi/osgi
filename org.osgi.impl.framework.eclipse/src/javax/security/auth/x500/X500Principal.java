/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 */
package javax.security.auth.x500;

import java.io.InputStream;
import java.security.Principal;

public final class X500Principal implements Principal, java.io.Serializable {
	public static final String	RFC1779		= "RFC1779";
	public static final String	RFC2253		= "RFC2253";
	public static final String	CANONICAL	= "CANONICAL";

	public X500Principal(String var0) {
	}

	public X500Principal(byte[] var0) {
	}

	public X500Principal(InputStream var0) {
	}

	public String getName() {
		return null;
	}

	public String getName(String var0) {
		return null;
	}

	public byte[] getEncoded() {
		return null;
	}

	public String toString() {
		return null;
	}

	public boolean equals(Object var0) {
		return false;
	}

	public int hashCode() {
		return 0;
	}

}
