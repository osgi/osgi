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
public abstract class SelectionKey {
	public final static int OP_ACCEPT = 16;
	public final static int OP_CONNECT = 8;
	public final static int OP_READ = 1;
	public final static int OP_WRITE = 4;
	protected SelectionKey() { } 
	public final java.lang.Object attach(java.lang.Object var0) { return null; }
	public final java.lang.Object attachment() { return null; }
	public abstract void cancel();
	public abstract java.nio.channels.SelectableChannel channel();
	public abstract int interestOps();
	public abstract java.nio.channels.SelectionKey interestOps(int var0);
	public final boolean isAcceptable() { return false; }
	public final boolean isConnectable() { return false; }
	public final boolean isReadable() { return false; }
	public abstract boolean isValid();
	public final boolean isWritable() { return false; }
	public abstract int readyOps();
	public abstract java.nio.channels.Selector selector();
}

