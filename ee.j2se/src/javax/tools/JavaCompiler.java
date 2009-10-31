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
public interface JavaCompiler extends javax.tools.OptionChecker, javax.tools.Tool {
	public interface CompilationTask extends java.util.concurrent.Callable<java.lang.Boolean> {
		java.lang.Boolean call();
		void setLocale(java.util.Locale var0);
		void setProcessors(java.lang.Iterable<? extends javax.annotation.processing.Processor> var0);
	}
	javax.tools.StandardJavaFileManager getStandardFileManager(javax.tools.DiagnosticListener<? super javax.tools.JavaFileObject> var0, java.util.Locale var1, java.nio.charset.Charset var2);
	javax.tools.JavaCompiler.CompilationTask getTask(java.io.Writer var0, javax.tools.JavaFileManager var1, javax.tools.DiagnosticListener<? super javax.tools.JavaFileObject> var2, java.lang.Iterable<java.lang.String> var3, java.lang.Iterable<java.lang.String> var4, java.lang.Iterable<? extends javax.tools.JavaFileObject> var5);
}

