/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 */

package java.util.zip;
public class Inflater {
	public void end() { }
	protected void finalize() { }
	public boolean finished() { return false; }
	public int getAdler() { return 0; }
	public int getRemaining() { return 0; }
	public int getTotalIn() { return 0; }
	public int getTotalOut() { return 0; }
	public int inflate(byte[] var0) throws java.util.zip.DataFormatException { return 0; }
	public int inflate(byte[] var0, int var1, int var2) throws java.util.zip.DataFormatException { return 0; }
	public Inflater() { }
	public Inflater(boolean var0) { }
	public boolean needsDictionary() { return false; }
	public boolean needsInput() { return false; }
	public void reset() { }
	public void setDictionary(byte[] var0) { }
	public void setDictionary(byte[] var0, int var1, int var2) { }
	public void setInput(byte[] var0) { }
	public void setInput(byte[] var0, int var1, int var2) { }
}

