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

package java.nio.file;
public interface Path extends java.lang.Comparable<java.nio.file.Path>, java.lang.Iterable<java.nio.file.Path>, java.nio.file.Watchable {
	int compareTo(java.nio.file.Path var0);
	boolean endsWith(java.lang.String var0);
	boolean endsWith(java.nio.file.Path var0);
	boolean equals(java.lang.Object var0);
	java.nio.file.Path getFileName();
	java.nio.file.FileSystem getFileSystem();
	java.nio.file.Path getName(int var0);
	int getNameCount();
	java.nio.file.Path getParent();
	java.nio.file.Path getRoot();
	int hashCode();
	boolean isAbsolute();
	java.util.Iterator<java.nio.file.Path> iterator();
	java.nio.file.Path normalize();
	java.nio.file.Path relativize(java.nio.file.Path var0);
	java.nio.file.Path resolve(java.lang.String var0);
	java.nio.file.Path resolve(java.nio.file.Path var0);
	java.nio.file.Path resolveSibling(java.lang.String var0);
	java.nio.file.Path resolveSibling(java.nio.file.Path var0);
	boolean startsWith(java.lang.String var0);
	boolean startsWith(java.nio.file.Path var0);
	java.nio.file.Path subpath(int var0, int var1);
	java.nio.file.Path toAbsolutePath();
	java.io.File toFile();
	java.nio.file.Path toRealPath(java.nio.file.LinkOption... var0) throws java.io.IOException;
	java.lang.String toString();
	java.net.URI toUri();
}

