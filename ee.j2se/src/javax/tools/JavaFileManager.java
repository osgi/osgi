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

package javax.tools;
public interface JavaFileManager extends java.io.Closeable, java.io.Flushable, javax.tools.OptionChecker {
	public interface Location {
		java.lang.String getName();
		boolean isOutputLocation();
	}
	java.lang.ClassLoader getClassLoader(javax.tools.JavaFileManager.Location var0);
	javax.tools.FileObject getFileForInput(javax.tools.JavaFileManager.Location var0, java.lang.String var1, java.lang.String var2) throws java.io.IOException;
	javax.tools.FileObject getFileForOutput(javax.tools.JavaFileManager.Location var0, java.lang.String var1, java.lang.String var2, javax.tools.FileObject var3) throws java.io.IOException;
	javax.tools.JavaFileObject getJavaFileForInput(javax.tools.JavaFileManager.Location var0, java.lang.String var1, javax.tools.JavaFileObject.Kind var2) throws java.io.IOException;
	javax.tools.JavaFileObject getJavaFileForOutput(javax.tools.JavaFileManager.Location var0, java.lang.String var1, javax.tools.JavaFileObject.Kind var2, javax.tools.FileObject var3) throws java.io.IOException;
	boolean handleOption(java.lang.String var0, java.util.Iterator<java.lang.String> var1);
	boolean hasLocation(javax.tools.JavaFileManager.Location var0);
	java.lang.String inferBinaryName(javax.tools.JavaFileManager.Location var0, javax.tools.JavaFileObject var1);
	boolean isSameFile(javax.tools.FileObject var0, javax.tools.FileObject var1);
	java.lang.Iterable<javax.tools.JavaFileObject> list(javax.tools.JavaFileManager.Location var0, java.lang.String var1, java.util.Set<javax.tools.JavaFileObject.Kind> var2, boolean var3) throws java.io.IOException;
}

