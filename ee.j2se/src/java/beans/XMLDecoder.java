/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.beans;
public class XMLDecoder implements java.lang.AutoCloseable {
	public XMLDecoder(java.io.InputStream var0) { } 
	public XMLDecoder(java.io.InputStream var0, java.lang.Object var1) { } 
	public XMLDecoder(java.io.InputStream var0, java.lang.Object var1, java.beans.ExceptionListener var2) { } 
	public XMLDecoder(java.io.InputStream var0, java.lang.Object var1, java.beans.ExceptionListener var2, java.lang.ClassLoader var3) { } 
	public XMLDecoder(org.xml.sax.InputSource var0) { } 
	public void close() { }
	public static org.xml.sax.helpers.DefaultHandler createHandler(java.lang.Object var0, java.beans.ExceptionListener var1, java.lang.ClassLoader var2) { return null; }
	public java.beans.ExceptionListener getExceptionListener() { return null; }
	public java.lang.Object getOwner() { return null; }
	public java.lang.Object readObject() { return null; }
	public void setExceptionListener(java.beans.ExceptionListener var0) { }
	public void setOwner(java.lang.Object var0) { }
}
