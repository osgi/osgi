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
public final class Files {
	public static long copy(java.io.InputStream var0, java.nio.file.Path var1, java.nio.file.CopyOption... var2) throws java.io.IOException { return 0l; }
	public static long copy(java.nio.file.Path var0, java.io.OutputStream var1) throws java.io.IOException { return 0l; }
	public static java.nio.file.Path copy(java.nio.file.Path var0, java.nio.file.Path var1, java.nio.file.CopyOption... var2) throws java.io.IOException { return null; }
	public static java.nio.file.Path createDirectories(java.nio.file.Path var0, java.nio.file.attribute.FileAttribute<?>... var1) throws java.io.IOException { return null; }
	public static java.nio.file.Path createDirectory(java.nio.file.Path var0, java.nio.file.attribute.FileAttribute<?>... var1) throws java.io.IOException { return null; }
	public static java.nio.file.Path createFile(java.nio.file.Path var0, java.nio.file.attribute.FileAttribute<?>... var1) throws java.io.IOException { return null; }
	public static java.nio.file.Path createLink(java.nio.file.Path var0, java.nio.file.Path var1) throws java.io.IOException { return null; }
	public static java.nio.file.Path createSymbolicLink(java.nio.file.Path var0, java.nio.file.Path var1, java.nio.file.attribute.FileAttribute<?>... var2) throws java.io.IOException { return null; }
	public static java.nio.file.Path createTempDirectory(java.lang.String var0, java.nio.file.attribute.FileAttribute<?>... var1) throws java.io.IOException { return null; }
	public static java.nio.file.Path createTempDirectory(java.nio.file.Path var0, java.lang.String var1, java.nio.file.attribute.FileAttribute<?>... var2) throws java.io.IOException { return null; }
	public static java.nio.file.Path createTempFile(java.lang.String var0, java.lang.String var1, java.nio.file.attribute.FileAttribute<?>... var2) throws java.io.IOException { return null; }
	public static java.nio.file.Path createTempFile(java.nio.file.Path var0, java.lang.String var1, java.lang.String var2, java.nio.file.attribute.FileAttribute<?>... var3) throws java.io.IOException { return null; }
	public static void delete(java.nio.file.Path var0) throws java.io.IOException { }
	public static boolean deleteIfExists(java.nio.file.Path var0) throws java.io.IOException { return false; }
	public static boolean exists(java.nio.file.Path var0, java.nio.file.LinkOption... var1) { return false; }
	public static java.lang.Object getAttribute(java.nio.file.Path var0, java.lang.String var1, java.nio.file.LinkOption... var2) throws java.io.IOException { return null; }
	public static <V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(java.nio.file.Path var0, java.lang.Class<V> var1, java.nio.file.LinkOption... var2) { return null; }
	public static java.nio.file.FileStore getFileStore(java.nio.file.Path var0) throws java.io.IOException { return null; }
	public static java.nio.file.attribute.FileTime getLastModifiedTime(java.nio.file.Path var0, java.nio.file.LinkOption... var1) throws java.io.IOException { return null; }
	public static java.nio.file.attribute.UserPrincipal getOwner(java.nio.file.Path var0, java.nio.file.LinkOption... var1) throws java.io.IOException { return null; }
	public static java.util.Set<java.nio.file.attribute.PosixFilePermission> getPosixFilePermissions(java.nio.file.Path var0, java.nio.file.LinkOption... var1) throws java.io.IOException { return null; }
	public static boolean isDirectory(java.nio.file.Path var0, java.nio.file.LinkOption... var1) { return false; }
	public static boolean isExecutable(java.nio.file.Path var0) { return false; }
	public static boolean isHidden(java.nio.file.Path var0) throws java.io.IOException { return false; }
	public static boolean isReadable(java.nio.file.Path var0) { return false; }
	public static boolean isRegularFile(java.nio.file.Path var0, java.nio.file.LinkOption... var1) { return false; }
	public static boolean isSameFile(java.nio.file.Path var0, java.nio.file.Path var1) throws java.io.IOException { return false; }
	public static boolean isSymbolicLink(java.nio.file.Path var0) { return false; }
	public static boolean isWritable(java.nio.file.Path var0) { return false; }
	public static java.nio.file.Path move(java.nio.file.Path var0, java.nio.file.Path var1, java.nio.file.CopyOption... var2) throws java.io.IOException { return null; }
	public static java.io.BufferedReader newBufferedReader(java.nio.file.Path var0, java.nio.charset.Charset var1) throws java.io.IOException { return null; }
	public static java.io.BufferedWriter newBufferedWriter(java.nio.file.Path var0, java.nio.charset.Charset var1, java.nio.file.OpenOption... var2) throws java.io.IOException { return null; }
	public static java.nio.channels.SeekableByteChannel newByteChannel(java.nio.file.Path var0, java.util.Set<? extends java.nio.file.OpenOption> var1, java.nio.file.attribute.FileAttribute<?>... var2) throws java.io.IOException { return null; }
	public static java.nio.channels.SeekableByteChannel newByteChannel(java.nio.file.Path var0, java.nio.file.OpenOption... var1) throws java.io.IOException { return null; }
	public static java.nio.file.DirectoryStream<java.nio.file.Path> newDirectoryStream(java.nio.file.Path var0) throws java.io.IOException { return null; }
	public static java.nio.file.DirectoryStream<java.nio.file.Path> newDirectoryStream(java.nio.file.Path var0, java.lang.String var1) throws java.io.IOException { return null; }
	public static java.nio.file.DirectoryStream<java.nio.file.Path> newDirectoryStream(java.nio.file.Path var0, java.nio.file.DirectoryStream.Filter<? super java.nio.file.Path> var1) throws java.io.IOException { return null; }
	public static java.io.InputStream newInputStream(java.nio.file.Path var0, java.nio.file.OpenOption... var1) throws java.io.IOException { return null; }
	public static java.io.OutputStream newOutputStream(java.nio.file.Path var0, java.nio.file.OpenOption... var1) throws java.io.IOException { return null; }
	public static boolean notExists(java.nio.file.Path var0, java.nio.file.LinkOption... var1) { return false; }
	public static java.lang.String probeContentType(java.nio.file.Path var0) throws java.io.IOException { return null; }
	public static byte[] readAllBytes(java.nio.file.Path var0) throws java.io.IOException { return null; }
	public static java.util.List<java.lang.String> readAllLines(java.nio.file.Path var0, java.nio.charset.Charset var1) throws java.io.IOException { return null; }
	public static <A extends java.nio.file.attribute.BasicFileAttributes> A readAttributes(java.nio.file.Path var0, java.lang.Class<A> var1, java.nio.file.LinkOption... var2) throws java.io.IOException { return null; }
	public static java.util.Map<java.lang.String,java.lang.Object> readAttributes(java.nio.file.Path var0, java.lang.String var1, java.nio.file.LinkOption... var2) throws java.io.IOException { return null; }
	public static java.nio.file.Path readSymbolicLink(java.nio.file.Path var0) throws java.io.IOException { return null; }
	public static java.nio.file.Path setAttribute(java.nio.file.Path var0, java.lang.String var1, java.lang.Object var2, java.nio.file.LinkOption... var3) throws java.io.IOException { return null; }
	public static java.nio.file.Path setLastModifiedTime(java.nio.file.Path var0, java.nio.file.attribute.FileTime var1) throws java.io.IOException { return null; }
	public static java.nio.file.Path setOwner(java.nio.file.Path var0, java.nio.file.attribute.UserPrincipal var1) throws java.io.IOException { return null; }
	public static java.nio.file.Path setPosixFilePermissions(java.nio.file.Path var0, java.util.Set<java.nio.file.attribute.PosixFilePermission> var1) throws java.io.IOException { return null; }
	public static long size(java.nio.file.Path var0) throws java.io.IOException { return 0l; }
	public static java.nio.file.Path walkFileTree(java.nio.file.Path var0, java.nio.file.FileVisitor<? super java.nio.file.Path> var1) throws java.io.IOException { return null; }
	public static java.nio.file.Path walkFileTree(java.nio.file.Path var0, java.util.Set<java.nio.file.FileVisitOption> var1, int var2, java.nio.file.FileVisitor<? super java.nio.file.Path> var3) throws java.io.IOException { return null; }
	public static java.nio.file.Path write(java.nio.file.Path var0, java.lang.Iterable<? extends java.lang.CharSequence> var1, java.nio.charset.Charset var2, java.nio.file.OpenOption... var3) throws java.io.IOException { return null; }
	public static java.nio.file.Path write(java.nio.file.Path var0, byte[] var1, java.nio.file.OpenOption... var2) throws java.io.IOException { return null; }
	private Files() { } /* generated constructor to prevent compiler adding default public constructor */
}

