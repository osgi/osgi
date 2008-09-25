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
public abstract interface HTMLTextAreaElement extends org.w3c.dom.html.HTMLElement {
	public abstract void blur();
	public abstract void focus();
	public abstract java.lang.String getAccessKey();
	public abstract int getCols();
	public abstract java.lang.String getDefaultValue();
	public abstract boolean getDisabled();
	public abstract org.w3c.dom.html.HTMLFormElement getForm();
	public abstract java.lang.String getName();
	public abstract boolean getReadOnly();
	public abstract int getRows();
	public abstract int getTabIndex();
	public abstract java.lang.String getType();
	public abstract java.lang.String getValue();
	public abstract void select();
	public abstract void setAccessKey(java.lang.String var0);
	public abstract void setCols(int var0);
	public abstract void setDefaultValue(java.lang.String var0);
	public abstract void setDisabled(boolean var0);
	public abstract void setName(java.lang.String var0);
	public abstract void setReadOnly(boolean var0);
	public abstract void setRows(int var0);
	public abstract void setTabIndex(int var0);
	public abstract void setValue(java.lang.String var0);
}

