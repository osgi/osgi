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
public interface HTMLTableSectionElement extends org.w3c.dom.html.HTMLElement {
	void deleteRow(int var0);
	java.lang.String getAlign();
	java.lang.String getCh();
	java.lang.String getChOff();
	org.w3c.dom.html.HTMLCollection getRows();
	java.lang.String getVAlign();
	org.w3c.dom.html.HTMLElement insertRow(int var0);
	void setAlign(java.lang.String var0);
	void setCh(java.lang.String var0);
	void setChOff(java.lang.String var0);
	void setVAlign(java.lang.String var0);
}

