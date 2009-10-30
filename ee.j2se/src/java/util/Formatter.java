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

package java.util;
public final class Formatter implements java.io.Closeable, java.io.Flushable {
	public enum BigDecimalLayoutForm {
		DECIMAL_FLOAT,
		SCIENTIFIC;
	}
	public Formatter() { } 
	public Formatter(java.io.File var0) throws java.io.FileNotFoundException { } 
	public Formatter(java.io.File var0, java.lang.String var1) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException { } 
	public Formatter(java.io.File var0, java.lang.String var1, java.util.Locale var2) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException { } 
	public Formatter(java.io.OutputStream var0) { } 
	public Formatter(java.io.OutputStream var0, java.lang.String var1) throws java.io.UnsupportedEncodingException { } 
	public Formatter(java.io.OutputStream var0, java.lang.String var1, java.util.Locale var2) throws java.io.UnsupportedEncodingException { } 
	public Formatter(java.io.PrintStream var0) { } 
	public Formatter(java.lang.Appendable var0) { } 
	public Formatter(java.lang.Appendable var0, java.util.Locale var1) { } 
	public Formatter(java.lang.String var0) throws java.io.FileNotFoundException { } 
	public Formatter(java.lang.String var0, java.lang.String var1) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException { } 
	public Formatter(java.lang.String var0, java.lang.String var1, java.util.Locale var2) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException { } 
	public Formatter(java.util.Locale var0) { } 
	public void close() { }
	public void flush() { }
	public java.util.Formatter format(java.lang.String var0, java.lang.Object[] var1) { return null; }
	public java.util.Formatter format(java.util.Locale var0, java.lang.String var1, java.lang.Object[] var2) { return null; }
	public java.io.IOException ioException() { return null; }
	public java.util.Locale locale() { return null; }
	public java.lang.Appendable out() { return null; }
}

