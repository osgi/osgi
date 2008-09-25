/*
 * $Revision$
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
public abstract class AbstractSelector extends java.nio.channels.Selector {
	protected AbstractSelector(java.nio.channels.spi.SelectorProvider var0) { }
	protected final void begin() { }
	protected final java.util.Set cancelledKeys() { return null; }
	public final void close() throws java.io.IOException { }
	protected final void deregister(java.nio.channels.spi.AbstractSelectionKey var0) { }
	protected final void end() { }
	protected abstract void implCloseSelector() throws java.io.IOException;
	public final boolean isOpen() { return false; }
	public final java.nio.channels.spi.SelectorProvider provider() { return null; }
	protected abstract java.nio.channels.SelectionKey register(java.nio.channels.spi.AbstractSelectableChannel var0, int var1, java.lang.Object var2);
}

