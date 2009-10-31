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

package java.util.jar;
public class Manifest implements java.lang.Cloneable {
	public Manifest() { } 
	public Manifest(java.io.InputStream var0) throws java.io.IOException { } 
	public Manifest(java.util.jar.Manifest var0) { } 
	public void clear() { }
	public java.lang.Object clone() { return null; }
	public java.util.jar.Attributes getAttributes(java.lang.String var0) { return null; }
	public java.util.Map<java.lang.String,java.util.jar.Attributes> getEntries() { return null; }
	public java.util.jar.Attributes getMainAttributes() { return null; }
	public void read(java.io.InputStream var0) throws java.io.IOException { }
	public void write(java.io.OutputStream var0) throws java.io.IOException { }
}

