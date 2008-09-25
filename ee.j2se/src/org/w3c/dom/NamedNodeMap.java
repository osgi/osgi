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

package org.w3c.dom;
public abstract interface NamedNodeMap {
	public abstract int getLength();
	public abstract org.w3c.dom.Node getNamedItem(java.lang.String var0);
	public abstract org.w3c.dom.Node getNamedItemNS(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.Node item(int var0);
	public abstract org.w3c.dom.Node removeNamedItem(java.lang.String var0);
	public abstract org.w3c.dom.Node removeNamedItemNS(java.lang.String var0, java.lang.String var1);
	public abstract org.w3c.dom.Node setNamedItem(org.w3c.dom.Node var0);
	public abstract org.w3c.dom.Node setNamedItemNS(org.w3c.dom.Node var0);
}

