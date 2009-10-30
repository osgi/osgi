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
public abstract class Selector {
	protected Selector() { } 
	public abstract void close() throws java.io.IOException;
	public abstract boolean isOpen();
	public abstract java.util.Set<java.nio.channels.SelectionKey> keys();
	public static java.nio.channels.Selector open() throws java.io.IOException { return null; }
	public abstract java.nio.channels.spi.SelectorProvider provider();
	public abstract int select() throws java.io.IOException;
	public abstract int select(long var0) throws java.io.IOException;
	public abstract int selectNow() throws java.io.IOException;
	public abstract java.util.Set<java.nio.channels.SelectionKey> selectedKeys();
	public abstract java.nio.channels.Selector wakeup();
}

