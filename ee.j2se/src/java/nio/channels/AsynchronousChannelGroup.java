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
public abstract class AsynchronousChannelGroup {
	protected AsynchronousChannelGroup(java.nio.channels.spi.AsynchronousChannelProvider var0) { } 
	public abstract boolean awaitTermination(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException;
	public abstract boolean isShutdown();
	public abstract boolean isTerminated();
	public final java.nio.channels.spi.AsynchronousChannelProvider provider() { return null; }
	public abstract void shutdown();
	public abstract void shutdownNow() throws java.io.IOException;
	public static java.nio.channels.AsynchronousChannelGroup withCachedThreadPool(java.util.concurrent.ExecutorService var0, int var1) throws java.io.IOException { return null; }
	public static java.nio.channels.AsynchronousChannelGroup withFixedThreadPool(int var0, java.util.concurrent.ThreadFactory var1) throws java.io.IOException { return null; }
	public static java.nio.channels.AsynchronousChannelGroup withThreadPool(java.util.concurrent.ExecutorService var0) throws java.io.IOException { return null; }
}

