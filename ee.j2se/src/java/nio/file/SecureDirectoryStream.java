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
public interface SecureDirectoryStream<T> extends java.nio.file.DirectoryStream<T> {
	void deleteDirectory(T var0) throws java.io.IOException;
	void deleteFile(T var0) throws java.io.IOException;
	<V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(java.lang.Class<V> var0);
	<V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(T var0, java.lang.Class<V> var1, java.nio.file.LinkOption... var2);
	void move(T var0, java.nio.file.SecureDirectoryStream<T> var1, T var2) throws java.io.IOException;
	java.nio.channels.SeekableByteChannel newByteChannel(T var0, java.util.Set<? extends java.nio.file.OpenOption> var1, java.nio.file.attribute.FileAttribute<?>... var2) throws java.io.IOException;
	java.nio.file.SecureDirectoryStream<T> newDirectoryStream(T var0, java.nio.file.LinkOption... var1) throws java.io.IOException;
}

