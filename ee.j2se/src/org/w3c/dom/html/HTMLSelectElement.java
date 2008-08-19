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
public abstract interface HTMLSelectElement extends org.w3c.dom.html.HTMLElement {
	public abstract void add(org.w3c.dom.html.HTMLElement var0, org.w3c.dom.html.HTMLElement var1);
	public abstract void blur();
	public abstract void focus();
	public abstract boolean getDisabled();
	public abstract org.w3c.dom.html.HTMLFormElement getForm();
	public abstract int getLength();
	public abstract boolean getMultiple();
	public abstract java.lang.String getName();
	public abstract org.w3c.dom.html.HTMLCollection getOptions();
	public abstract int getSelectedIndex();
	public abstract int getSize();
	public abstract int getTabIndex();
	public abstract java.lang.String getType();
	public abstract java.lang.String getValue();
	public abstract void remove(int var0);
	public abstract void setDisabled(boolean var0);
	public abstract void setMultiple(boolean var0);
	public abstract void setName(java.lang.String var0);
	public abstract void setSelectedIndex(int var0);
	public abstract void setSize(int var0);
	public abstract void setTabIndex(int var0);
	public abstract void setValue(java.lang.String var0);
}

