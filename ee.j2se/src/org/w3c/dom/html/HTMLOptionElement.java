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
public interface HTMLOptionElement extends org.w3c.dom.html.HTMLElement {
	boolean getDefaultSelected();
	boolean getDisabled();
	org.w3c.dom.html.HTMLFormElement getForm();
	int getIndex();
	java.lang.String getLabel();
	boolean getSelected();
	java.lang.String getText();
	java.lang.String getValue();
	void setDefaultSelected(boolean var0);
	void setDisabled(boolean var0);
	void setLabel(java.lang.String var0);
	void setSelected(boolean var0);
	void setValue(java.lang.String var0);
}

