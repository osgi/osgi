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
public interface HTMLTableElement extends org.w3c.dom.html.HTMLElement {
	org.w3c.dom.html.HTMLElement createCaption();
	org.w3c.dom.html.HTMLElement createTFoot();
	org.w3c.dom.html.HTMLElement createTHead();
	void deleteCaption();
	void deleteRow(int var0);
	void deleteTFoot();
	void deleteTHead();
	java.lang.String getAlign();
	java.lang.String getBgColor();
	java.lang.String getBorder();
	org.w3c.dom.html.HTMLTableCaptionElement getCaption();
	java.lang.String getCellPadding();
	java.lang.String getCellSpacing();
	java.lang.String getFrame();
	org.w3c.dom.html.HTMLCollection getRows();
	java.lang.String getRules();
	java.lang.String getSummary();
	org.w3c.dom.html.HTMLCollection getTBodies();
	org.w3c.dom.html.HTMLTableSectionElement getTFoot();
	org.w3c.dom.html.HTMLTableSectionElement getTHead();
	java.lang.String getWidth();
	org.w3c.dom.html.HTMLElement insertRow(int var0);
	void setAlign(java.lang.String var0);
	void setBgColor(java.lang.String var0);
	void setBorder(java.lang.String var0);
	void setCaption(org.w3c.dom.html.HTMLTableCaptionElement var0);
	void setCellPadding(java.lang.String var0);
	void setCellSpacing(java.lang.String var0);
	void setFrame(java.lang.String var0);
	void setRules(java.lang.String var0);
	void setSummary(java.lang.String var0);
	void setTFoot(org.w3c.dom.html.HTMLTableSectionElement var0);
	void setTHead(org.w3c.dom.html.HTMLTableSectionElement var0);
	void setWidth(java.lang.String var0);
}

