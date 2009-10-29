/*
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

package java.net;
public abstract class DatagramSocketImpl implements java.net.SocketOptions {
	public DatagramSocketImpl() { }
	protected abstract void bind(int var0, java.net.InetAddress var1) throws java.net.SocketException;
	protected abstract void close();
	protected void connect(java.net.InetAddress var0, int var1) throws java.net.SocketException { }
	protected abstract void create() throws java.net.SocketException;
	protected void disconnect() { }
	protected java.io.FileDescriptor getFileDescriptor() { return null; }
	protected int getLocalPort() { return 0; }
	/** @deprecated */ protected abstract byte getTTL() throws java.io.IOException;
	protected abstract int getTimeToLive() throws java.io.IOException;
	protected abstract void join(java.net.InetAddress var0) throws java.io.IOException;
	protected abstract void joinGroup(java.net.SocketAddress var0, java.net.NetworkInterface var1) throws java.io.IOException;
	protected abstract void leave(java.net.InetAddress var0) throws java.io.IOException;
	protected abstract void leaveGroup(java.net.SocketAddress var0, java.net.NetworkInterface var1) throws java.io.IOException;
	protected abstract int peek(java.net.InetAddress var0) throws java.io.IOException;
	protected abstract int peekData(java.net.DatagramPacket var0) throws java.io.IOException;
	protected abstract void receive(java.net.DatagramPacket var0) throws java.io.IOException;
	protected abstract void send(java.net.DatagramPacket var0) throws java.io.IOException;
	/** @deprecated */ protected abstract void setTTL(byte var0) throws java.io.IOException;
	protected abstract void setTimeToLive(int var0) throws java.io.IOException;
	protected java.io.FileDescriptor fd;
	protected int localPort;
}

