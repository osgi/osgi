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

package java.lang.instrument;
public interface Instrumentation {
	void addTransformer(java.lang.instrument.ClassFileTransformer var0);
	void addTransformer(java.lang.instrument.ClassFileTransformer var0, boolean var1);
	void appendToBootstrapClassLoaderSearch(java.util.jar.JarFile var0);
	void appendToSystemClassLoaderSearch(java.util.jar.JarFile var0);
	java.lang.Class[] getAllLoadedClasses();
	java.lang.Class[] getInitiatedClasses(java.lang.ClassLoader var0);
	long getObjectSize(java.lang.Object var0);
	boolean isModifiableClass(java.lang.Class<?> var0);
	boolean isNativeMethodPrefixSupported();
	boolean isRedefineClassesSupported();
	boolean isRetransformClassesSupported();
	void redefineClasses(java.lang.instrument.ClassDefinition... var0) throws java.lang.ClassNotFoundException, java.lang.instrument.UnmodifiableClassException;
	boolean removeTransformer(java.lang.instrument.ClassFileTransformer var0);
	void retransformClasses(java.lang.Class<?>... var0) throws java.lang.instrument.UnmodifiableClassException;
	void setNativeMethodPrefix(java.lang.instrument.ClassFileTransformer var0, java.lang.String var1);
}

