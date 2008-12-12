/*
 * $Revision$
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
public class ServerSocket {
	public ServerSocket() throws java.io.IOException { }
	public ServerSocket(int var0) throws java.io.IOException { }
	public ServerSocket(int var0, int var1) throws java.io.IOException { }
	public ServerSocket(int var0, int var1, java.net.InetAddress var2) throws java.io.IOException { }
	public java.net.Socket accept() throws java.io.IOException { return null; }
	public void bind(java.net.SocketAddress var0) throws java.io.IOException { }
	public void bind(java.net.SocketAddress var0, int var1) throws java.io.IOException { }
	public void close() throws java.io.IOException { }
	public java.net.InetAddress getInetAddress() { return null; }
	public int getLocalPort() { return 0; }
	public java.net.SocketAddress getLocalSocketAddress() { return null; }
	public int getReceiveBufferSize() throws java.net.SocketException { return 0; }
	public boolean getReuseAddress() throws java.net.SocketException { return false; }
	public int getSoTimeout() throws java.io.IOException { return 0; }
	protected final void implAccept(java.net.Socket var0) throws java.io.IOException { }
	public boolean isBound() { return false; }
	public boolean isClosed() { return false; }
	public void setReceiveBufferSize(int var0) throws java.net.SocketException { }
	public void setReuseAddress(boolean var0) throws java.net.SocketException { }
	public void setSoTimeout(int var0) throws java.net.SocketException { }
	public static void setSocketFactory(java.net.SocketImplFactory var0) throws java.io.IOException { }
}

