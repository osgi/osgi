/*
 * $Date$
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
public abstract interface HTMLTableElement extends org.w3c.dom.html.HTMLElement {
	public abstract org.w3c.dom.html.HTMLElement createCaption();
	public abstract org.w3c.dom.html.HTMLElement createTFoot();
	public abstract org.w3c.dom.html.HTMLElement createTHead();
	public abstract void deleteCaption();
	public abstract void deleteRow(int var0);
	public abstract void deleteTFoot();
	public abstract void deleteTHead();
	public abstract java.lang.String getAlign();
	public abstract java.lang.String getBgColor();
	public abstract java.lang.String getBorder();
	public abstract org.w3c.dom.html.HTMLTableCaptionElement getCaption();
	public abstract java.lang.String getCellPadding();
	public abstract java.lang.String getCellSpacing();
	public abstract java.lang.String getFrame();
	public abstract org.w3c.dom.html.HTMLCollection getRows();
	public abstract java.lang.String getRules();
	public abstract java.lang.String getSummary();
	public abstract org.w3c.dom.html.HTMLCollection getTBodies();
	public abstract org.w3c.dom.html.HTMLTableSectionElement getTFoot();
	public abstract org.w3c.dom.html.HTMLTableSectionElement getTHead();
	public abstract java.lang.String getWidth();
	public abstract org.w3c.dom.html.HTMLElement insertRow(int var0);
	public abstract void setAlign(java.lang.String var0);
	public abstract void setBgColor(java.lang.String var0);
	public abstract void setBorder(java.lang.String var0);
	public abstract void setCaption(org.w3c.dom.html.HTMLTableCaptionElement var0);
	public abstract void setCellPadding(java.lang.String var0);
	public abstract void setCellSpacing(java.lang.String var0);
	public abstract void setFrame(java.lang.String var0);
	public abstract void setRules(java.lang.String var0);
	public abstract void setSummary(java.lang.String var0);
	public abstract void setTFoot(org.w3c.dom.html.HTMLTableSectionElement var0);
	public abstract void setTHead(org.w3c.dom.html.HTMLTableSectionElement var0);
	public abstract void setWidth(java.lang.String var0);
}

