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
public final class HttpCookie implements java.lang.Cloneable {
	public HttpCookie(java.lang.String var0, java.lang.String var1) { } 
	public java.lang.Object clone() { return null; }
	public static boolean domainMatches(java.lang.String var0, java.lang.String var1) { return false; }
	public java.lang.String getComment() { return null; }
	public java.lang.String getCommentURL() { return null; }
	public boolean getDiscard() { return false; }
	public java.lang.String getDomain() { return null; }
	public long getMaxAge() { return 0l; }
	public java.lang.String getName() { return null; }
	public java.lang.String getPath() { return null; }
	public java.lang.String getPortlist() { return null; }
	public boolean getSecure() { return false; }
	public java.lang.String getValue() { return null; }
	public int getVersion() { return 0; }
	public boolean hasExpired() { return false; }
	public static java.util.List<java.net.HttpCookie> parse(java.lang.String var0) { return null; }
	public void setComment(java.lang.String var0) { }
	public void setCommentURL(java.lang.String var0) { }
	public void setDiscard(boolean var0) { }
	public void setDomain(java.lang.String var0) { }
	public void setMaxAge(long var0) { }
	public void setPath(java.lang.String var0) { }
	public void setPortlist(java.lang.String var0) { }
	public void setSecure(boolean var0) { }
	public void setValue(java.lang.String var0) { }
	public void setVersion(int var0) { }
}

