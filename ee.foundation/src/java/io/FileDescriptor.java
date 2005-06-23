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
public final class FileDescriptor {
	public FileDescriptor() { }
	public void sync() throws java.io.SyncFailedException { }
	public boolean valid() { return false; }
	public final static java.io.FileDescriptor in; static { in = null; }
	public final static java.io.FileDescriptor out; static { out = null; }
	public final static java.io.FileDescriptor err; static { err = null; }
}

