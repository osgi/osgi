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

package org.w3c.dom.html;
public abstract interface HTMLDocument extends org.w3c.dom.Document {
	public abstract void close();
	public abstract org.w3c.dom.html.HTMLCollection getAnchors();
	public abstract org.w3c.dom.html.HTMLCollection getApplets();
	public abstract org.w3c.dom.html.HTMLElement getBody();
	public abstract java.lang.String getCookie();
	public abstract java.lang.String getDomain();
	public abstract org.w3c.dom.NodeList getElementsByName(java.lang.String var0);
	public abstract org.w3c.dom.html.HTMLCollection getForms();
	public abstract org.w3c.dom.html.HTMLCollection getImages();
	public abstract org.w3c.dom.html.HTMLCollection getLinks();
	public abstract java.lang.String getReferrer();
	public abstract java.lang.String getTitle();
	public abstract java.lang.String getURL();
	public abstract void open();
	public abstract void setBody(org.w3c.dom.html.HTMLElement var0);
	public abstract void setCookie(java.lang.String var0);
	public abstract void setTitle(java.lang.String var0);
	public abstract void write(java.lang.String var0);
	public abstract void writeln(java.lang.String var0);
}

