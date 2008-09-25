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

package java.net;
public class InetAddress implements java.io.Serializable {
	public byte[] getAddress() { return null; }
	public static java.net.InetAddress[] getAllByName(java.lang.String var0) throws java.net.UnknownHostException { return null; }
	public static java.net.InetAddress getByAddress(java.lang.String var0, byte[] var1) throws java.net.UnknownHostException { return null; }
	public static java.net.InetAddress getByAddress(byte[] var0) throws java.net.UnknownHostException { return null; }
	public static java.net.InetAddress getByName(java.lang.String var0) throws java.net.UnknownHostException { return null; }
	public java.lang.String getCanonicalHostName() { return null; }
	public java.lang.String getHostAddress() { return null; }
	public java.lang.String getHostName() { return null; }
	public static java.net.InetAddress getLocalHost() throws java.net.UnknownHostException { return null; }
	public int hashCode() { return 0; }
	public boolean isAnyLocalAddress() { return false; }
	public boolean isLinkLocalAddress() { return false; }
	public boolean isLoopbackAddress() { return false; }
	public boolean isMCGlobal() { return false; }
	public boolean isMCLinkLocal() { return false; }
	public boolean isMCNodeLocal() { return false; }
	public boolean isMCOrgLocal() { return false; }
	public boolean isMCSiteLocal() { return false; }
	public boolean isMulticastAddress() { return false; }
	public boolean isSiteLocalAddress() { return false; }
	InetAddress() { } /* generated constructor to prevent compiler adding default public constructor */
}

