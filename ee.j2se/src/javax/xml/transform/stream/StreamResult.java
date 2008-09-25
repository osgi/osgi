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

package javax.xml.transform.stream;
public class StreamResult implements javax.xml.transform.Result {
	public StreamResult() { }
	public StreamResult(java.io.File var0) { }
	public StreamResult(java.io.OutputStream var0) { }
	public StreamResult(java.io.Writer var0) { }
	public StreamResult(java.lang.String var0) { }
	public java.io.OutputStream getOutputStream() { return null; }
	public java.lang.String getSystemId() { return null; }
	public java.io.Writer getWriter() { return null; }
	public void setOutputStream(java.io.OutputStream var0) { }
	public void setSystemId(java.io.File var0) { }
	public void setSystemId(java.lang.String var0) { }
	public void setWriter(java.io.Writer var0) { }
	public final static java.lang.String FEATURE = "http://javax.xml.transform.stream.StreamResult/feature";
}

