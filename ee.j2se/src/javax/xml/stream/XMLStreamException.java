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

package javax.xml.stream;
public class XMLStreamException extends java.lang.Exception {
	protected javax.xml.stream.Location location;
	protected java.lang.Throwable nested;
	public XMLStreamException() { } 
	public XMLStreamException(java.lang.String var0) { } 
	public XMLStreamException(java.lang.String var0, java.lang.Throwable var1) { } 
	public XMLStreamException(java.lang.String var0, javax.xml.stream.Location var1) { } 
	public XMLStreamException(java.lang.String var0, javax.xml.stream.Location var1, java.lang.Throwable var2) { } 
	public XMLStreamException(java.lang.Throwable var0) { } 
	public javax.xml.stream.Location getLocation() { return null; }
	public java.lang.Throwable getNestedException() { return null; }
}

