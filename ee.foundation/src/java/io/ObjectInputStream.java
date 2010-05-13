/*
 * $Id$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.io;
public class ObjectInputStream extends java.io.InputStream implements java.io.ObjectInput, java.io.ObjectStreamConstants {
	protected ObjectInputStream() throws java.io.IOException { }
	public ObjectInputStream(java.io.InputStream var0) throws java.io.IOException { }
	public void defaultReadObject() throws java.io.IOException, java.lang.ClassNotFoundException { }
	protected boolean enableResolveObject(boolean var0) { return false; }
	public int read() throws java.io.IOException { return 0; }
	public boolean readBoolean() throws java.io.IOException { return false; }
	public byte readByte() throws java.io.IOException { return 0; }
	public char readChar() throws java.io.IOException { return 0; }
	protected java.io.ObjectStreamClass readClassDescriptor() throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
	public double readDouble() throws java.io.IOException { return 0.0d; }
	public java.io.ObjectInputStream.GetField readFields() throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
	public float readFloat() throws java.io.IOException { return 0.0f; }
	public void readFully(byte[] var0) throws java.io.IOException { }
	public void readFully(byte[] var0, int var1, int var2) throws java.io.IOException { }
	public int readInt() throws java.io.IOException { return 0; }
	/** @deprecated */ public java.lang.String readLine() throws java.io.IOException { return null; }
	public long readLong() throws java.io.IOException { return 0l; }
	public final java.lang.Object readObject() throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
	protected java.lang.Object readObjectOverride() throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
	public short readShort() throws java.io.IOException { return 0; }
	protected void readStreamHeader() throws java.io.IOException { }
	public java.lang.String readUTF() throws java.io.IOException { return null; }
	public java.lang.Object readUnshared() throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
	public int readUnsignedByte() throws java.io.IOException { return 0; }
	public int readUnsignedShort() throws java.io.IOException { return 0; }
	public void registerValidation(java.io.ObjectInputValidation var0, int var1) throws java.io.InvalidObjectException, java.io.NotActiveException { }
	protected java.lang.Class resolveClass(java.io.ObjectStreamClass var0) throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
	protected java.lang.Object resolveObject(java.lang.Object var0) throws java.io.IOException { return null; }
	protected java.lang.Class resolveProxyClass(java.lang.String[] var0) throws java.io.IOException, java.lang.ClassNotFoundException { return null; }
	public int skipBytes(int var0) throws java.io.IOException { return 0; }
	public static abstract class GetField {
		public GetField() { }
		public abstract boolean defaulted(java.lang.String var0) throws java.io.IOException;
		public abstract byte get(java.lang.String var0, byte var1) throws java.io.IOException;
		public abstract char get(java.lang.String var0, char var1) throws java.io.IOException;
		public abstract double get(java.lang.String var0, double var1) throws java.io.IOException;
		public abstract float get(java.lang.String var0, float var1) throws java.io.IOException;
		public abstract int get(java.lang.String var0, int var1) throws java.io.IOException;
		public abstract long get(java.lang.String var0, long var1) throws java.io.IOException;
		public abstract java.lang.Object get(java.lang.String var0, java.lang.Object var1) throws java.io.IOException;
		public abstract short get(java.lang.String var0, short var1) throws java.io.IOException;
		public abstract boolean get(java.lang.String var0, boolean var1) throws java.io.IOException;
		public abstract java.io.ObjectStreamClass getObjectStreamClass();
	}
}

