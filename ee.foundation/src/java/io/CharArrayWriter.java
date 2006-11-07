/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2006). All Rights Reserved.
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
public class CharArrayWriter extends java.io.Writer {
	public CharArrayWriter() { }
	public CharArrayWriter(int var0) { }
	public void close() { }
	public void flush() { }
	public void reset() { }
	public int size() { return 0; }
	public char[] toCharArray() { return null; }
	public void write(int var0) { }
	public void write(java.lang.String var0, int var1, int var2) { }
	public void write(char[] var0, int var1, int var2) { }
	public void writeTo(java.io.Writer var0) throws java.io.IOException { }
	protected char[] buf;
	protected int count;
}

