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
public abstract class SelectorProvider {
	protected SelectorProvider() { }
	public abstract java.nio.channels.DatagramChannel openDatagramChannel() throws java.io.IOException;
	public abstract java.nio.channels.Pipe openPipe() throws java.io.IOException;
	public abstract java.nio.channels.spi.AbstractSelector openSelector() throws java.io.IOException;
	public abstract java.nio.channels.ServerSocketChannel openServerSocketChannel() throws java.io.IOException;
	public abstract java.nio.channels.SocketChannel openSocketChannel() throws java.io.IOException;
	public static java.nio.channels.spi.SelectorProvider provider() { return null; }
}

