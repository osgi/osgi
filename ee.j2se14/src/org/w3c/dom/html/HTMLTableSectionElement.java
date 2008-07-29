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
public abstract interface HTMLTableSectionElement extends org.w3c.dom.html.HTMLElement {
	public abstract void deleteRow(int var0);
	public abstract java.lang.String getAlign();
	public abstract java.lang.String getCh();
	public abstract java.lang.String getChOff();
	public abstract org.w3c.dom.html.HTMLCollection getRows();
	public abstract java.lang.String getVAlign();
	public abstract org.w3c.dom.html.HTMLElement insertRow(int var0);
	public abstract void setAlign(java.lang.String var0);
	public abstract void setCh(java.lang.String var0);
	public abstract void setChOff(java.lang.String var0);
	public abstract void setVAlign(java.lang.String var0);
}

