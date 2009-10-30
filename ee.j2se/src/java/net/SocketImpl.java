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

package java.net;
public abstract class SocketImpl implements java.net.SocketOptions {
	protected java.net.InetAddress address;
	protected java.io.FileDescriptor fd;
	protected int localport;
	protected int port;
	public SocketImpl() { } 
	protected abstract void accept(java.net.SocketImpl var0) throws java.io.IOException;
	protected abstract int available() throws java.io.IOException;
	protected abstract void bind(java.net.InetAddress var0, int var1) throws java.io.IOException;
	protected abstract void close() throws java.io.IOException;
	protected abstract void connect(java.lang.String var0, int var1) throws java.io.IOException;
	protected abstract void connect(java.net.InetAddress var0, int var1) throws java.io.IOException;
	protected abstract void connect(java.net.SocketAddress var0, int var1) throws java.io.IOException;
	protected abstract void create(boolean var0) throws java.io.IOException;
	protected java.io.FileDescriptor getFileDescriptor() { return null; }
	protected java.net.InetAddress getInetAddress() { return null; }
	protected abstract java.io.InputStream getInputStream() throws java.io.IOException;
	protected int getLocalPort() { return 0; }
	protected abstract java.io.OutputStream getOutputStream() throws java.io.IOException;
	protected int getPort() { return 0; }
	protected abstract void listen(int var0) throws java.io.IOException;
	protected abstract void sendUrgentData(int var0) throws java.io.IOException;
	protected void setPerformancePreferences(int var0, int var1, int var2) { }
	protected void shutdownInput() throws java.io.IOException { }
	protected void shutdownOutput() throws java.io.IOException { }
	protected boolean supportsUrgentData() { return false; }
}

