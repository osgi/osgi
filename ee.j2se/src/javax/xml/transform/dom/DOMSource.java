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

package javax.xml.transform.dom;
public class DOMSource implements javax.xml.transform.Source {
	public final static java.lang.String FEATURE = "http://javax.xml.transform.dom.DOMSource/feature";
	public DOMSource() { } 
	public DOMSource(org.w3c.dom.Node var0) { } 
	public DOMSource(org.w3c.dom.Node var0, java.lang.String var1) { } 
	public org.w3c.dom.Node getNode() { return null; }
	public java.lang.String getSystemId() { return null; }
	public void setNode(org.w3c.dom.Node var0) { }
	public void setSystemId(java.lang.String var0) { }
}

