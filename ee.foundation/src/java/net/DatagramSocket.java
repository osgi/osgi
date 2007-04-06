/*
 * $Id$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2006). All Rights Reserved.
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
public class DatagramSocket {
	public DatagramSocket() throws java.net.SocketException { }
	public DatagramSocket(int var0) throws java.net.SocketException { }
	public DatagramSocket(int var0, java.net.InetAddress var1) throws java.net.SocketException { }
	protected DatagramSocket(java.net.DatagramSocketImpl var0) { }
	public DatagramSocket(java.net.SocketAddress var0) throws java.net.SocketException { }
	public void bind(java.net.SocketAddress var0) throws java.net.SocketException { }
	public void close() { }
	public void connect(java.net.InetAddress var0, int var1) { }
	public void connect(java.net.SocketAddress var0) throws java.net.SocketException { }
	public void disconnect() { }
	public boolean getBroadcast() throws java.net.SocketException { return false; }
	public java.net.InetAddress getInetAddress() { return null; }
	public java.net.InetAddress getLocalAddress() { return null; }
	public int getLocalPort() { return 0; }
	public java.net.SocketAddress getLocalSocketAddress() { return null; }
	public int getPort() { return 0; }
	public int getReceiveBufferSize() throws java.net.SocketException { return 0; }
	public java.net.SocketAddress getRemoteSocketAddress() { return null; }
	public boolean getReuseAddress() throws java.net.SocketException { return false; }
	public int getSendBufferSize() throws java.net.SocketException { return 0; }
	public int getSoTimeout() throws java.net.SocketException { return 0; }
	public int getTrafficClass() throws java.net.SocketException { return 0; }
	public boolean isBound() { return false; }
	public boolean isClosed() { return false; }
	public boolean isConnected() { return false; }
	public void receive(java.net.DatagramPacket var0) throws java.io.IOException { }
	public void send(java.net.DatagramPacket var0) throws java.io.IOException { }
	public void setBroadcast(boolean var0) throws java.net.SocketException { }
	public static void setDatagramSocketImplFactory(java.net.DatagramSocketImplFactory var0) throws java.io.IOException { }
	public void setReceiveBufferSize(int var0) throws java.net.SocketException { }
	public void setReuseAddress(boolean var0) throws java.net.SocketException { }
	public void setSendBufferSize(int var0) throws java.net.SocketException { }
	public void setSoTimeout(int var0) throws java.net.SocketException { }
	public void setTrafficClass(int var0) throws java.net.SocketException { }
}

