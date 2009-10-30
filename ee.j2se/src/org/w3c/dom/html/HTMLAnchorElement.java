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
public interface HTMLAnchorElement extends org.w3c.dom.html.HTMLElement {
	void blur();
	void focus();
	java.lang.String getAccessKey();
	java.lang.String getCharset();
	java.lang.String getCoords();
	java.lang.String getHref();
	java.lang.String getHreflang();
	java.lang.String getName();
	java.lang.String getRel();
	java.lang.String getRev();
	java.lang.String getShape();
	int getTabIndex();
	java.lang.String getTarget();
	java.lang.String getType();
	void setAccessKey(java.lang.String var0);
	void setCharset(java.lang.String var0);
	void setCoords(java.lang.String var0);
	void setHref(java.lang.String var0);
	void setHreflang(java.lang.String var0);
	void setName(java.lang.String var0);
	void setRel(java.lang.String var0);
	void setRev(java.lang.String var0);
	void setShape(java.lang.String var0);
	void setTabIndex(int var0);
	void setTarget(java.lang.String var0);
	void setType(java.lang.String var0);
}

