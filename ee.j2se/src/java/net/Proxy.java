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
public class Proxy {
	public enum Type {
		DIRECT,
		HTTP,
		SOCKS;
	}
	public final static java.net.Proxy NO_PROXY; static { NO_PROXY = null; }
	public Proxy(java.net.Proxy.Type var0, java.net.SocketAddress var1) { } 
	public java.net.SocketAddress address() { return null; }
	public final boolean equals(java.lang.Object var0) { return false; }
	public final int hashCode() { return 0; }
	public java.net.Proxy.Type type() { return null; }
}

