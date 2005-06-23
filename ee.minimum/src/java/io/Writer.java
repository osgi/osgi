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

package java.io;
public abstract class Writer {
	protected Writer() { }
	protected Writer(java.lang.Object var0) { }
	public abstract void close() throws java.io.IOException;
	public abstract void flush() throws java.io.IOException;
	public void write(char[] var0) throws java.io.IOException { }
	public abstract void write(char[] var0, int var1, int var2) throws java.io.IOException;
	public void write(int var0) throws java.io.IOException { }
	public void write(java.lang.String var0) throws java.io.IOException { }
	public void write(java.lang.String var0, int var1, int var2) throws java.io.IOException { }
	protected java.lang.Object lock;
}

