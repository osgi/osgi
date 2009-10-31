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

package javax.xml.bind.annotation;
public class W3CDomHandler implements javax.xml.bind.annotation.DomHandler<org.w3c.dom.Element,javax.xml.transform.dom.DOMResult> {
	public W3CDomHandler() { } 
	public W3CDomHandler(javax.xml.parsers.DocumentBuilder var0) { } 
	public javax.xml.transform.dom.DOMResult createUnmarshaller(javax.xml.bind.ValidationEventHandler var0) { return null; }
	public javax.xml.parsers.DocumentBuilder getBuilder() { return null; }
	public org.w3c.dom.Element getElement(javax.xml.transform.dom.DOMResult var0) { return null; }
	public javax.xml.transform.Source marshal(org.w3c.dom.Element var0, javax.xml.bind.ValidationEventHandler var1) { return null; }
	public void setBuilder(javax.xml.parsers.DocumentBuilder var0) { }
}

