/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.lang;
public class Throwable implements java.io.Serializable {
	public Throwable() { } 
	public Throwable(java.lang.String var0) { } 
	public Throwable(java.lang.String var0, java.lang.Throwable var1) { } 
	protected Throwable(java.lang.String var0, java.lang.Throwable var1, boolean var2, boolean var3) { } 
	public Throwable(java.lang.Throwable var0) { } 
	public final void addSuppressed(java.lang.Throwable var0) { }
	public java.lang.Throwable fillInStackTrace() { return null; }
	public java.lang.Throwable getCause() { return null; }
	public java.lang.String getLocalizedMessage() { return null; }
	public java.lang.String getMessage() { return null; }
	public java.lang.StackTraceElement[] getStackTrace() { return null; }
	public final java.lang.Throwable[] getSuppressed() { return null; }
	public java.lang.Throwable initCause(java.lang.Throwable var0) { return null; }
	public void printStackTrace() { }
	public void printStackTrace(java.io.PrintStream var0) { }
	public void printStackTrace(java.io.PrintWriter var0) { }
	public void setStackTrace(java.lang.StackTraceElement[] var0) { }
}

