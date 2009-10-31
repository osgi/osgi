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

package javax.xml.transform.stax;
public class StAXResult implements javax.xml.transform.Result {
	public final static java.lang.String FEATURE = "http://javax.xml.transform.stax.StAXResult/feature";
	public StAXResult(javax.xml.stream.XMLEventWriter var0) { } 
	public StAXResult(javax.xml.stream.XMLStreamWriter var0) { } 
	public java.lang.String getSystemId() { return null; }
	public javax.xml.stream.XMLEventWriter getXMLEventWriter() { return null; }
	public javax.xml.stream.XMLStreamWriter getXMLStreamWriter() { return null; }
	public void setSystemId(java.lang.String var0) { }
}

