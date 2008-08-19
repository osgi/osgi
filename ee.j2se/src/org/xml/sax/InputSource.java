/*
 * $Date$
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

package org.xml.sax;
public class InputSource {
	public InputSource() { }
	public InputSource(java.io.InputStream var0) { }
	public InputSource(java.io.Reader var0) { }
	public InputSource(java.lang.String var0) { }
	public java.io.InputStream getByteStream() { return null; }
	public java.io.Reader getCharacterStream() { return null; }
	public java.lang.String getEncoding() { return null; }
	public java.lang.String getPublicId() { return null; }
	public java.lang.String getSystemId() { return null; }
	public void setByteStream(java.io.InputStream var0) { }
	public void setCharacterStream(java.io.Reader var0) { }
	public void setEncoding(java.lang.String var0) { }
	public void setPublicId(java.lang.String var0) { }
	public void setSystemId(java.lang.String var0) { }
}

