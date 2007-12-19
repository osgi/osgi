/*
 * $Date$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2007). All Rights Reserved.
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
public class Socket {
	public Socket() { }
	public Socket(java.lang.String var0, int var1) throws java.io.IOException { }
	public Socket(java.lang.String var0, int var1, java.net.InetAddress var2, int var3) throws java.io.IOException { }
	public Socket(java.net.InetAddress var0, int var1) throws java.io.IOException { }
	public Socket(java.net.InetAddress var0, int var1, java.net.InetAddress var2, int var3) throws java.io.IOException { }
	protected Socket(java.net.SocketImpl var0) throws java.net.SocketException { }
	public void bind(java.net.SocketAddress var0) throws java.io.IOException { }
	public void close() throws java.io.IOException { }
	public void connect(java.net.SocketAddress var0) throws java.io.IOException { }
	public void connect(java.net.SocketAddress var0, int var1) throws java.io.IOException { }
	public java.net.InetAddress getInetAddress() { return null; }
	public java.io.InputStream getInputStream() throws java.io.IOException { return null; }
	public boolean getKeepAlive() throws java.net.SocketException { return false; }
	public java.net.InetAddress getLocalAddress() { return null; }
	public int getLocalPort() { return 0; }
	public java.net.SocketAddress getLocalSocketAddress() { return null; }
	public boolean getOOBInline() throws java.net.SocketException { return false; }
	public java.io.OutputStream getOutputStream() throws java.io.IOException { return null; }
	public int getPort() { return 0; }
	public int getReceiveBufferSize() throws java.net.SocketException { return 0; }
	public java.net.SocketAddress getRemoteSocketAddress() { return null; }
	public boolean getReuseAddress() throws java.net.SocketException { return false; }
	public int getSendBufferSize() throws java.net.SocketException { return 0; }
	public int getSoLinger() throws java.net.SocketException { return 0; }
	public int getSoTimeout() throws java.net.SocketException { return 0; }
	public boolean getTcpNoDelay() throws java.net.SocketException { return false; }
	public int getTrafficClass() throws java.net.SocketException { return 0; }
	public boolean isBound() { return false; }
	public boolean isClosed() { return false; }
	public boolean isConnected() { return false; }
	public boolean isInputShutdown() { return false; }
	public boolean isOutputShutdown() { return false; }
	public void sendUrgentData(int var0) throws java.io.IOException { }
	public void setKeepAlive(boolean var0) throws java.net.SocketException { }
	public void setOOBInline(boolean var0) throws java.net.SocketException { }
	public void setReceiveBufferSize(int var0) throws java.net.SocketException { }
	public void setReuseAddress(boolean var0) throws java.net.SocketException { }
	public void setSendBufferSize(int var0) throws java.net.SocketException { }
	public void setSoLinger(boolean var0, int var1) throws java.net.SocketException { }
	public void setSoTimeout(int var0) throws java.net.SocketException { }
	public static void setSocketImplFactory(java.net.SocketImplFactory var0) throws java.io.IOException { }
	public void setTcpNoDelay(boolean var0) throws java.net.SocketException { }
	public void setTrafficClass(int var0) throws java.net.SocketException { }
	public void shutdownInput() throws java.io.IOException { }
	public void shutdownOutput() throws java.io.IOException { }
}

