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

package javax.xml.transform.sax;
public class SAXSource implements javax.xml.transform.Source {
	public SAXSource() { }
	public SAXSource(org.xml.sax.InputSource var0) { }
	public SAXSource(org.xml.sax.XMLReader var0, org.xml.sax.InputSource var1) { }
	public org.xml.sax.InputSource getInputSource() { return null; }
	public java.lang.String getSystemId() { return null; }
	public org.xml.sax.XMLReader getXMLReader() { return null; }
	public void setInputSource(org.xml.sax.InputSource var0) { }
	public void setSystemId(java.lang.String var0) { }
	public void setXMLReader(org.xml.sax.XMLReader var0) { }
	public static org.xml.sax.InputSource sourceToInputSource(javax.xml.transform.Source var0) { return null; }
	public final static java.lang.String FEATURE = "http://javax.xml.transform.sax.SAXSource/feature";
}

