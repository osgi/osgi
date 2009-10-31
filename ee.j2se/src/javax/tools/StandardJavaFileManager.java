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
public interface StandardJavaFileManager extends javax.tools.JavaFileManager {
	java.lang.Iterable<? extends javax.tools.JavaFileObject> getJavaFileObjects(java.io.File... var0);
	java.lang.Iterable<? extends javax.tools.JavaFileObject> getJavaFileObjects(java.lang.String... var0);
	java.lang.Iterable<? extends javax.tools.JavaFileObject> getJavaFileObjectsFromFiles(java.lang.Iterable<? extends java.io.File> var0);
	java.lang.Iterable<? extends javax.tools.JavaFileObject> getJavaFileObjectsFromStrings(java.lang.Iterable<java.lang.String> var0);
	java.lang.Iterable<? extends java.io.File> getLocation(javax.tools.JavaFileManager.Location var0);
	void setLocation(javax.tools.JavaFileManager.Location var0, java.lang.Iterable<? extends java.io.File> var1) throws java.io.IOException;
}

