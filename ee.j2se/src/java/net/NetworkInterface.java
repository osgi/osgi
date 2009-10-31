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
public final class NetworkInterface {
	public static java.net.NetworkInterface getByInetAddress(java.net.InetAddress var0) throws java.net.SocketException { return null; }
	public static java.net.NetworkInterface getByName(java.lang.String var0) throws java.net.SocketException { return null; }
	public java.lang.String getDisplayName() { return null; }
	public byte[] getHardwareAddress() throws java.net.SocketException { return null; }
	public java.util.Enumeration<java.net.InetAddress> getInetAddresses() { return null; }
	public java.util.List<java.net.InterfaceAddress> getInterfaceAddresses() { return null; }
	public int getMTU() throws java.net.SocketException { return 0; }
	public java.lang.String getName() { return null; }
	public static java.util.Enumeration<java.net.NetworkInterface> getNetworkInterfaces() throws java.net.SocketException { return null; }
	public java.net.NetworkInterface getParent() { return null; }
	public java.util.Enumeration<java.net.NetworkInterface> getSubInterfaces() { return null; }
	public boolean isLoopback() throws java.net.SocketException { return false; }
	public boolean isPointToPoint() throws java.net.SocketException { return false; }
	public boolean isUp() throws java.net.SocketException { return false; }
	public boolean isVirtual() { return false; }
	public boolean supportsMulticast() throws java.net.SocketException { return false; }
	private NetworkInterface() { } /* generated constructor to prevent compiler adding default public constructor */
}

