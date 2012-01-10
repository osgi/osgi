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
public abstract class AsynchronousSocketChannel implements java.nio.channels.AsynchronousByteChannel, java.nio.channels.NetworkChannel {
	protected AsynchronousSocketChannel(java.nio.channels.spi.AsynchronousChannelProvider var0) { } 
	public abstract java.nio.channels.AsynchronousSocketChannel bind(java.net.SocketAddress var0) throws java.io.IOException;
	public abstract java.util.concurrent.Future<java.lang.Void> connect(java.net.SocketAddress var0);
	public abstract <A> void connect(java.net.SocketAddress var0, A var1, java.nio.channels.CompletionHandler<java.lang.Void,? super A> var2);
	public abstract java.net.SocketAddress getRemoteAddress() throws java.io.IOException;
	public static java.nio.channels.AsynchronousSocketChannel open() throws java.io.IOException { return null; }
	public static java.nio.channels.AsynchronousSocketChannel open(java.nio.channels.AsynchronousChannelGroup var0) throws java.io.IOException { return null; }
	public final java.nio.channels.spi.AsynchronousChannelProvider provider() { return null; }
	public abstract <A> void read(java.nio.ByteBuffer var0, long var1, java.util.concurrent.TimeUnit var2, A var3, java.nio.channels.CompletionHandler<java.lang.Integer,? super A> var4);
	public final <A> void read(java.nio.ByteBuffer var0, A var1, java.nio.channels.CompletionHandler<java.lang.Integer,? super A> var2) { }
	public abstract <A> void read(java.nio.ByteBuffer[] var0, int var1, int var2, long var3, java.util.concurrent.TimeUnit var4, A var5, java.nio.channels.CompletionHandler<java.lang.Long,? super A> var6);
	public abstract <T> java.nio.channels.AsynchronousSocketChannel setOption(java.net.SocketOption<T> var0, T var1) throws java.io.IOException;
	public abstract java.nio.channels.AsynchronousSocketChannel shutdownInput() throws java.io.IOException;
	public abstract java.nio.channels.AsynchronousSocketChannel shutdownOutput() throws java.io.IOException;
	public abstract <A> void write(java.nio.ByteBuffer var0, long var1, java.util.concurrent.TimeUnit var2, A var3, java.nio.channels.CompletionHandler<java.lang.Integer,? super A> var4);
	public final <A> void write(java.nio.ByteBuffer var0, A var1, java.nio.channels.CompletionHandler<java.lang.Integer,? super A> var2) { }
	public abstract <A> void write(java.nio.ByteBuffer[] var0, int var1, int var2, long var3, java.util.concurrent.TimeUnit var4, A var5, java.nio.channels.CompletionHandler<java.lang.Long,? super A> var6);
}

