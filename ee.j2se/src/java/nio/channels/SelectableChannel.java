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

package java.nio.channels;
public abstract class SelectableChannel extends java.nio.channels.spi.AbstractInterruptibleChannel implements java.nio.channels.Channel {
	protected SelectableChannel() { } 
	public abstract java.lang.Object blockingLock();
	public abstract java.nio.channels.SelectableChannel configureBlocking(boolean var0) throws java.io.IOException;
	public abstract boolean isBlocking();
	public abstract boolean isRegistered();
	public abstract java.nio.channels.SelectionKey keyFor(java.nio.channels.Selector var0);
	public abstract java.nio.channels.spi.SelectorProvider provider();
	public final java.nio.channels.SelectionKey register(java.nio.channels.Selector var0, int var1) throws java.nio.channels.ClosedChannelException { return null; }
	public abstract java.nio.channels.SelectionKey register(java.nio.channels.Selector var0, int var1, java.lang.Object var2) throws java.nio.channels.ClosedChannelException;
	public abstract int validOps();
}

