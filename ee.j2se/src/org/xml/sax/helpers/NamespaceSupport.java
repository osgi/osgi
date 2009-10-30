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

package org.xml.sax.helpers;
public class NamespaceSupport {
	public final static java.lang.String NSDECL = "http://www.w3.org/xmlns/2000/";
	public final static java.lang.String XMLNS = "http://www.w3.org/XML/1998/namespace";
	public NamespaceSupport() { } 
	public boolean declarePrefix(java.lang.String var0, java.lang.String var1) { return false; }
	public java.util.Enumeration getDeclaredPrefixes() { return null; }
	public java.lang.String getPrefix(java.lang.String var0) { return null; }
	public java.util.Enumeration getPrefixes() { return null; }
	public java.util.Enumeration getPrefixes(java.lang.String var0) { return null; }
	public java.lang.String getURI(java.lang.String var0) { return null; }
	public boolean isNamespaceDeclUris() { return false; }
	public void popContext() { }
	public java.lang.String[] processName(java.lang.String var0, java.lang.String[] var1, boolean var2) { return null; }
	public void pushContext() { }
	public void reset() { }
	public void setNamespaceDeclUris(boolean var0) { }
}

