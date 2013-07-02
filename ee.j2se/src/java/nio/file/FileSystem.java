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
public abstract class FileSystem implements java.io.Closeable {
	protected FileSystem() { } 
	public abstract java.lang.Iterable<java.nio.file.FileStore> getFileStores();
	public abstract java.nio.file.Path getPath(java.lang.String var0, java.lang.String... var1);
	public abstract java.nio.file.PathMatcher getPathMatcher(java.lang.String var0);
	public abstract java.lang.Iterable<java.nio.file.Path> getRootDirectories();
	public abstract java.lang.String getSeparator();
	public abstract java.nio.file.attribute.UserPrincipalLookupService getUserPrincipalLookupService();
	public abstract boolean isOpen();
	public abstract boolean isReadOnly();
	public abstract java.nio.file.WatchService newWatchService() throws java.io.IOException;
	public abstract java.nio.file.spi.FileSystemProvider provider();
	public abstract java.util.Set<java.lang.String> supportedFileAttributeViews();
}

