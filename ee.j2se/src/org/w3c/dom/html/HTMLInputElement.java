/*
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
public abstract interface HTMLInputElement extends org.w3c.dom.html.HTMLElement {
	public abstract void blur();
	public abstract void click();
	public abstract void focus();
	public abstract java.lang.String getAccept();
	public abstract java.lang.String getAccessKey();
	public abstract java.lang.String getAlign();
	public abstract java.lang.String getAlt();
	public abstract boolean getChecked();
	public abstract boolean getDefaultChecked();
	public abstract java.lang.String getDefaultValue();
	public abstract boolean getDisabled();
	public abstract org.w3c.dom.html.HTMLFormElement getForm();
	public abstract int getMaxLength();
	public abstract java.lang.String getName();
	public abstract boolean getReadOnly();
	public abstract java.lang.String getSize();
	public abstract java.lang.String getSrc();
	public abstract int getTabIndex();
	public abstract java.lang.String getType();
	public abstract java.lang.String getUseMap();
	public abstract java.lang.String getValue();
	public abstract void select();
	public abstract void setAccept(java.lang.String var0);
	public abstract void setAccessKey(java.lang.String var0);
	public abstract void setAlign(java.lang.String var0);
	public abstract void setAlt(java.lang.String var0);
	public abstract void setChecked(boolean var0);
	public abstract void setDefaultChecked(boolean var0);
	public abstract void setDefaultValue(java.lang.String var0);
	public abstract void setDisabled(boolean var0);
	public abstract void setMaxLength(int var0);
	public abstract void setName(java.lang.String var0);
	public abstract void setReadOnly(boolean var0);
	public abstract void setSize(java.lang.String var0);
	public abstract void setSrc(java.lang.String var0);
	public abstract void setTabIndex(int var0);
	public abstract void setUseMap(java.lang.String var0);
	public abstract void setValue(java.lang.String var0);
}

