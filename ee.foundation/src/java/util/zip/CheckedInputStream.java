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

package java.util.zip;
public class CheckedInputStream extends java.io.FilterInputStream {
	public CheckedInputStream(java.io.InputStream var0, java.util.zip.Checksum var1) { super((java.io.InputStream) null); }
	public int read() throws java.io.IOException { return 0; }
	public int read(byte[] var0, int var1, int var2) throws java.io.IOException { return 0; }
	public java.util.zip.Checksum getChecksum() { return null; }
	public long skip(long var0) throws java.io.IOException { return 0l; }
}

