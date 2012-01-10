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

package java.nio.file.spi;
public abstract class FileSystemProvider {
	protected FileSystemProvider() { } 
	public abstract void checkAccess(java.nio.file.Path var0, java.nio.file.AccessMode... var1) throws java.io.IOException;
	public abstract void copy(java.nio.file.Path var0, java.nio.file.Path var1, java.nio.file.CopyOption... var2) throws java.io.IOException;
	public abstract void createDirectory(java.nio.file.Path var0, java.nio.file.attribute.FileAttribute<?>... var1) throws java.io.IOException;
	public void createLink(java.nio.file.Path var0, java.nio.file.Path var1) throws java.io.IOException { }
	public void createSymbolicLink(java.nio.file.Path var0, java.nio.file.Path var1, java.nio.file.attribute.FileAttribute<?>... var2) throws java.io.IOException { }
	public abstract void delete(java.nio.file.Path var0) throws java.io.IOException;
	public boolean deleteIfExists(java.nio.file.Path var0) throws java.io.IOException { return false; }
	public abstract <V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(java.nio.file.Path var0, java.lang.Class<V> var1, java.nio.file.LinkOption... var2);
	public abstract java.nio.file.FileStore getFileStore(java.nio.file.Path var0) throws java.io.IOException;
	public abstract java.nio.file.FileSystem getFileSystem(java.net.URI var0);
	public abstract java.nio.file.Path getPath(java.net.URI var0);
	public abstract java.lang.String getScheme();
	public static java.util.List<java.nio.file.spi.FileSystemProvider> installedProviders() { return null; }
	public abstract boolean isHidden(java.nio.file.Path var0) throws java.io.IOException;
	public abstract boolean isSameFile(java.nio.file.Path var0, java.nio.file.Path var1) throws java.io.IOException;
	public abstract void move(java.nio.file.Path var0, java.nio.file.Path var1, java.nio.file.CopyOption... var2) throws java.io.IOException;
	public java.nio.channels.AsynchronousFileChannel newAsynchronousFileChannel(java.nio.file.Path var0, java.util.Set<? extends java.nio.file.OpenOption> var1, java.util.concurrent.ExecutorService var2, java.nio.file.attribute.FileAttribute<?>... var3) throws java.io.IOException { return null; }
	public abstract java.nio.channels.SeekableByteChannel newByteChannel(java.nio.file.Path var0, java.util.Set<? extends java.nio.file.OpenOption> var1, java.nio.file.attribute.FileAttribute<?>... var2) throws java.io.IOException;
	public abstract java.nio.file.DirectoryStream<java.nio.file.Path> newDirectoryStream(java.nio.file.Path var0, java.nio.file.DirectoryStream.Filter<? super java.nio.file.Path> var1) throws java.io.IOException;
	public java.nio.channels.FileChannel newFileChannel(java.nio.file.Path var0, java.util.Set<? extends java.nio.file.OpenOption> var1, java.nio.file.attribute.FileAttribute<?>... var2) throws java.io.IOException { return null; }
	public abstract java.nio.file.FileSystem newFileSystem(java.net.URI var0, java.util.Map<java.lang.String,?> var1) throws java.io.IOException;
	public java.nio.file.FileSystem newFileSystem(java.nio.file.Path var0, java.util.Map<java.lang.String,?> var1) throws java.io.IOException { return null; }
	public java.io.InputStream newInputStream(java.nio.file.Path var0, java.nio.file.OpenOption... var1) throws java.io.IOException { return null; }
	public java.io.OutputStream newOutputStream(java.nio.file.Path var0, java.nio.file.OpenOption... var1) throws java.io.IOException { return null; }
	public abstract <A extends java.nio.file.attribute.BasicFileAttributes> A readAttributes(java.nio.file.Path var0, java.lang.Class<A> var1, java.nio.file.LinkOption... var2) throws java.io.IOException;
	public abstract java.util.Map<java.lang.String,java.lang.Object> readAttributes(java.nio.file.Path var0, java.lang.String var1, java.nio.file.LinkOption... var2) throws java.io.IOException;
	public java.nio.file.Path readSymbolicLink(java.nio.file.Path var0) throws java.io.IOException { return null; }
	public abstract void setAttribute(java.nio.file.Path var0, java.lang.String var1, java.lang.Object var2, java.nio.file.LinkOption... var3) throws java.io.IOException;
}

