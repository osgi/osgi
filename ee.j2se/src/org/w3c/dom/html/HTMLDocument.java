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

package org.w3c.dom.html;
public interface HTMLDocument extends org.w3c.dom.Document {
	void close();
	org.w3c.dom.html.HTMLCollection getAnchors();
	org.w3c.dom.html.HTMLCollection getApplets();
	org.w3c.dom.html.HTMLElement getBody();
	java.lang.String getCookie();
	java.lang.String getDomain();
	org.w3c.dom.NodeList getElementsByName(java.lang.String var0);
	org.w3c.dom.html.HTMLCollection getForms();
	org.w3c.dom.html.HTMLCollection getImages();
	org.w3c.dom.html.HTMLCollection getLinks();
	java.lang.String getReferrer();
	java.lang.String getTitle();
	java.lang.String getURL();
	void open();
	void setBody(org.w3c.dom.html.HTMLElement var0);
	void setCookie(java.lang.String var0);
	void setTitle(java.lang.String var0);
	void write(java.lang.String var0);
	void writeln(java.lang.String var0);
}

