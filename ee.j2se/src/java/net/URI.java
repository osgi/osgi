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
public final class URI implements java.io.Serializable, java.lang.Comparable {
	public URI(java.lang.String var0) throws java.net.URISyntaxException { }
	public URI(java.lang.String var0, java.lang.String var1, java.lang.String var2) throws java.net.URISyntaxException { }
	public URI(java.lang.String var0, java.lang.String var1, java.lang.String var2, int var3, java.lang.String var4, java.lang.String var5, java.lang.String var6) throws java.net.URISyntaxException { }
	public URI(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) throws java.net.URISyntaxException { }
	public URI(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3, java.lang.String var4) throws java.net.URISyntaxException { }
	public int compareTo(java.lang.Object var0) { return 0; }
	public static java.net.URI create(java.lang.String var0) { return null; }
	public java.lang.String getAuthority() { return null; }
	public java.lang.String getFragment() { return null; }
	public java.lang.String getHost() { return null; }
	public java.lang.String getPath() { return null; }
	public int getPort() { return 0; }
	public java.lang.String getQuery() { return null; }
	public java.lang.String getRawAuthority() { return null; }
	public java.lang.String getRawFragment() { return null; }
	public java.lang.String getRawPath() { return null; }
	public java.lang.String getRawQuery() { return null; }
	public java.lang.String getRawSchemeSpecificPart() { return null; }
	public java.lang.String getRawUserInfo() { return null; }
	public java.lang.String getScheme() { return null; }
	public java.lang.String getSchemeSpecificPart() { return null; }
	public java.lang.String getUserInfo() { return null; }
	public int hashCode() { return 0; }
	public boolean isAbsolute() { return false; }
	public boolean isOpaque() { return false; }
	public java.net.URI normalize() { return null; }
	public java.net.URI parseServerAuthority() throws java.net.URISyntaxException { return null; }
	public java.net.URI relativize(java.net.URI var0) { return null; }
	public java.net.URI resolve(java.lang.String var0) { return null; }
	public java.net.URI resolve(java.net.URI var0) { return null; }
	public java.lang.String toASCIIString() { return null; }
	public java.net.URL toURL() throws java.net.MalformedURLException { return null; }
}

