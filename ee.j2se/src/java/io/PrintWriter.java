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
public class PrintWriter extends java.io.Writer {
	protected java.io.Writer out;
	public PrintWriter(java.io.File var0) throws java.io.FileNotFoundException { } 
	public PrintWriter(java.io.File var0, java.lang.String var1) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException { } 
	public PrintWriter(java.io.OutputStream var0) { } 
	public PrintWriter(java.io.OutputStream var0, boolean var1) { } 
	public PrintWriter(java.io.Writer var0) { } 
	public PrintWriter(java.io.Writer var0, boolean var1) { } 
	public PrintWriter(java.lang.String var0) throws java.io.FileNotFoundException { } 
	public PrintWriter(java.lang.String var0, java.lang.String var1) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException { } 
	public java.io.PrintWriter append(char var0) { return null; }
	public java.io.PrintWriter append(java.lang.CharSequence var0) { return null; }
	public java.io.PrintWriter append(java.lang.CharSequence var0, int var1, int var2) { return null; }
	public boolean checkError() { return false; }
	public void close() { }
	public void flush() { }
	public java.io.PrintWriter format(java.lang.String var0, java.lang.Object... var1) { return null; }
	public java.io.PrintWriter format(java.util.Locale var0, java.lang.String var1, java.lang.Object... var2) { return null; }
	public void print(char var0) { }
	public void print(double var0) { }
	public void print(float var0) { }
	public void print(int var0) { }
	public void print(long var0) { }
	public void print(java.lang.Object var0) { }
	public void print(java.lang.String var0) { }
	public void print(boolean var0) { }
	public void print(char[] var0) { }
	public java.io.PrintWriter printf(java.lang.String var0, java.lang.Object... var1) { return null; }
	public java.io.PrintWriter printf(java.util.Locale var0, java.lang.String var1, java.lang.Object... var2) { return null; }
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
	public void write(java.lang.String var0) { }
	public void write(java.lang.String var0, int var1, int var2) { }
	public void write(char[] var0) { }
	public void write(char[] var0, int var1, int var2) { }
}

