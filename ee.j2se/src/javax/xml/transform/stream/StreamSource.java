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

package javax.xml.transform.stream;
public class StreamSource implements javax.xml.transform.Source {
	public final static java.lang.String FEATURE = "http://javax.xml.transform.stream.StreamSource/feature";
	public StreamSource() { } 
	public StreamSource(java.io.File var0) { } 
	public StreamSource(java.io.InputStream var0) { } 
	public StreamSource(java.io.InputStream var0, java.lang.String var1) { } 
	public StreamSource(java.io.Reader var0) { } 
	public StreamSource(java.io.Reader var0, java.lang.String var1) { } 
	public StreamSource(java.lang.String var0) { } 
	public java.io.InputStream getInputStream() { return null; }
	public java.lang.String getPublicId() { return null; }
	public java.io.Reader getReader() { return null; }
	public java.lang.String getSystemId() { return null; }
	public void setInputStream(java.io.InputStream var0) { }
	public void setPublicId(java.lang.String var0) { }
	public void setReader(java.io.Reader var0) { }
	public void setSystemId(java.io.File var0) { }
	public void setSystemId(java.lang.String var0) { }
}

