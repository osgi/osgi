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

package java.lang;
public class Runtime {
	public void addShutdownHook(java.lang.Thread var0) { }
	public int availableProcessors() { return 0; }
	public java.lang.Process exec(java.lang.String var0) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String var0, java.lang.String[] var1) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String var0, java.lang.String[] var1, java.io.File var2) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String[] var0) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String[] var0, java.lang.String[] var1) throws java.io.IOException { return null; }
	public java.lang.Process exec(java.lang.String[] var0, java.lang.String[] var1, java.io.File var2) throws java.io.IOException { return null; }
	public void exit(int var0) { }
	public long freeMemory() { return 0l; }
	public void gc() { }
	/** @deprecated */
	@java.lang.Deprecated
	public java.io.InputStream getLocalizedInputStream(java.io.InputStream var0) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public java.io.OutputStream getLocalizedOutputStream(java.io.OutputStream var0) { return null; }
	public static java.lang.Runtime getRuntime() { return null; }
	public void halt(int var0) { }
	public void load(java.lang.String var0) { }
	public void loadLibrary(java.lang.String var0) { }
	public long maxMemory() { return 0l; }
	public boolean removeShutdownHook(java.lang.Thread var0) { return false; }
	public void runFinalization() { }
	/** @deprecated */
	@java.lang.Deprecated
	public static void runFinalizersOnExit(boolean var0) { }
	public long totalMemory() { return 0l; }
	public void traceInstructions(boolean var0) { }
	public void traceMethodCalls(boolean var0) { }
	private Runtime() { } /* generated constructor to prevent compiler adding default public constructor */
}

