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
public class File implements java.io.Serializable, java.lang.Comparable {
	public File(java.io.File var0, java.lang.String var1) { }
	public File(java.lang.String var0) { }
	public File(java.lang.String var0, java.lang.String var1) { }
	public static java.io.File[] listRoots() { return null; }
	public boolean canRead() { return false; }
	public boolean canWrite() { return false; }
	public int compareTo(java.lang.Object var0) { return 0; }
	public int compareTo(java.io.File var0) { return 0; }
	public boolean delete() { return false; }
	public void deleteOnExit() { }
	public boolean equals(java.lang.Object var0) { return false; }
	public boolean exists() { return false; }
	public java.lang.String getAbsolutePath() { return null; }
	public java.io.File getAbsoluteFile() { return null; }
	public java.lang.String getCanonicalPath() throws java.io.IOException { return null; }
	public java.io.File getCanonicalFile() throws java.io.IOException { return null; }
	public java.lang.String getName() { return null; }
	public java.lang.String getParent() { return null; }
	public java.io.File getParentFile() { return null; }
	public java.lang.String getPath() { return null; }
	public int hashCode() { return 0; }
	public boolean isAbsolute() { return false; }
	public boolean isDirectory() { return false; }
	public boolean isFile() { return false; }
	public boolean isHidden() { return false; }
	public long lastModified() { return 0l; }
	public boolean setLastModified(long var0) { return false; }
	public boolean setReadOnly() { return false; }
	public long length() { return 0l; }
	public java.lang.String[] list() { return null; }
	public java.io.File[] listFiles() { return null; }
	public java.io.File[] listFiles(java.io.FilenameFilter var0) { return null; }
	public java.io.File[] listFiles(java.io.FileFilter var0) { return null; }
	public java.lang.String[] list(java.io.FilenameFilter var0) { return null; }
	public boolean mkdir() { return false; }
	public boolean mkdirs() { return false; }
	public boolean createNewFile() throws java.io.IOException { return false; }
	public static java.io.File createTempFile(java.lang.String var0, java.lang.String var1) throws java.io.IOException { return null; }
	public static java.io.File createTempFile(java.lang.String var0, java.lang.String var1, java.io.File var2) throws java.io.IOException { return null; }
	public boolean renameTo(java.io.File var0) { return false; }
	public java.lang.String toString() { return null; }
	public java.net.URL toURL() throws java.net.MalformedURLException { return null; }
	public final static char separatorChar; static { separatorChar = 0; }
	public final static java.lang.String separator; static { separator = null; }
	public final static char pathSeparatorChar; static { pathSeparatorChar = 0; }
	public final static java.lang.String pathSeparator; static { pathSeparator = null; }
}

