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

package java.nio.channels;
public abstract class AsynchronousFileChannel implements java.nio.channels.AsynchronousChannel {
	protected AsynchronousFileChannel() { } 
	public abstract void force(boolean var0) throws java.io.IOException;
	public final java.util.concurrent.Future<java.nio.channels.FileLock> lock() { return null; }
	public abstract java.util.concurrent.Future<java.nio.channels.FileLock> lock(long var0, long var1, boolean var2);
	public abstract <A> void lock(long var0, long var1, boolean var2, A var3, java.nio.channels.CompletionHandler<java.nio.channels.FileLock,? super A> var4);
	public final <A> void lock(A var0, java.nio.channels.CompletionHandler<java.nio.channels.FileLock,? super A> var1) { }
	public static java.nio.channels.AsynchronousFileChannel open(java.nio.file.Path var0, java.util.Set<? extends java.nio.file.OpenOption> var1, java.util.concurrent.ExecutorService var2, java.nio.file.attribute.FileAttribute<?>... var3) throws java.io.IOException { return null; }
	public static java.nio.channels.AsynchronousFileChannel open(java.nio.file.Path var0, java.nio.file.OpenOption... var1) throws java.io.IOException { return null; }
	public abstract java.util.concurrent.Future<java.lang.Integer> read(java.nio.ByteBuffer var0, long var1);
	public abstract <A> void read(java.nio.ByteBuffer var0, long var1, A var2, java.nio.channels.CompletionHandler<java.lang.Integer,? super A> var3);
	public abstract long size() throws java.io.IOException;
	public abstract java.nio.channels.AsynchronousFileChannel truncate(long var0) throws java.io.IOException;
	public final java.nio.channels.FileLock tryLock() throws java.io.IOException { return null; }
	public abstract java.nio.channels.FileLock tryLock(long var0, long var1, boolean var2) throws java.io.IOException;
	public abstract java.util.concurrent.Future<java.lang.Integer> write(java.nio.ByteBuffer var0, long var1);
	public abstract <A> void write(java.nio.ByteBuffer var0, long var1, A var2, java.nio.channels.CompletionHandler<java.lang.Integer,? super A> var3);
}

