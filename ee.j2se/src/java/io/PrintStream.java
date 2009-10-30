/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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
public class PrintStream extends java.io.FilterOutputStream implements java.io.Closeable, java.lang.Appendable {
	public PrintStream(java.io.File var0) throws java.io.FileNotFoundException  { super((java.io.OutputStream) null); } 
	public PrintStream(java.io.File var0, java.lang.String var1) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException  { super((java.io.OutputStream) null); } 
	public PrintStream(java.io.OutputStream var0)  { super((java.io.OutputStream) null); } 
	public PrintStream(java.io.OutputStream var0, boolean var1)  { super((java.io.OutputStream) null); } 
	public PrintStream(java.io.OutputStream var0, boolean var1, java.lang.String var2) throws java.io.UnsupportedEncodingException  { super((java.io.OutputStream) null); } 
	public PrintStream(java.lang.String var0) throws java.io.FileNotFoundException  { super((java.io.OutputStream) null); } 
	public PrintStream(java.lang.String var0, java.lang.String var1) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException  { super((java.io.OutputStream) null); } 
	public java.io.PrintStream append(char var0) { return null; }
	public java.io.PrintStream append(java.lang.CharSequence var0) { return null; }
	public java.io.PrintStream append(java.lang.CharSequence var0, int var1, int var2) { return null; }
	public boolean checkError() { return false; }
	public void close() { }
	public void flush() { }
	public java.io.PrintStream format(java.lang.String var0, java.lang.Object... var1) { return null; }
	public java.io.PrintStream format(java.util.Locale var0, java.lang.String var1, java.lang.Object... var2) { return null; }
	public void print(char var0) { }
	public void print(double var0) { }
	public void print(float var0) { }
	public void print(int var0) { }
	public void print(long var0) { }
	public void print(java.lang.Object var0) { }
	public void print(java.lang.String var0) { }
	public void print(boolean var0) { }
	public void print(char[] var0) { }
	public java.io.PrintStream printf(java.lang.String var0, java.lang.Object... var1) { return null; }
	public java.io.PrintStream printf(java.util.Locale var0, java.lang.String var1, java.lang.Object... var2) { return null; }
	public void println() { }
	public void println(char var0) { }
	public void println(double var0) { }
	public void println(float var0) { }
	public void println(int var0) { }
	public void println(long var0) { }
	public void println(java.lang.Object var0) { }
	public void println(java.lang.String var0) { }
	public void println(boolean var0) { }
	public void println(char[] var0) { }
	protected void setError() { }
	public void write(int var0) { }
	public void write(byte[] var0, int var1, int var2) { }
}

