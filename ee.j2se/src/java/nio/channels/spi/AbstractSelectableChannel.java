/*
 * $Date$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package java.nio.channels.spi;
public abstract class AbstractSelectableChannel extends java.nio.channels.SelectableChannel {
	protected AbstractSelectableChannel(java.nio.channels.spi.SelectorProvider var0) { }
	public final java.lang.Object blockingLock() { return null; }
	public final java.nio.channels.SelectableChannel configureBlocking(boolean var0) throws java.io.IOException { return null; }
	protected final void implCloseChannel() throws java.io.IOException { }
	protected abstract void implCloseSelectableChannel() throws java.io.IOException;
	protected abstract void implConfigureBlocking(boolean var0) throws java.io.IOException;
	public final boolean isBlocking() { return false; }
	public final boolean isRegistered() { return false; }
	public final java.nio.channels.SelectionKey keyFor(java.nio.channels.Selector var0) { return null; }
	public final java.nio.channels.spi.SelectorProvider provider() { return null; }
	public final java.nio.channels.SelectionKey register(java.nio.channels.Selector var0, int var1, java.lang.Object var2) throws java.nio.channels.ClosedChannelException { return null; }
}

