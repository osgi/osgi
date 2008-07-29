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

package org.w3c.dom.traversal;
public abstract interface TreeWalker {
	public abstract org.w3c.dom.Node firstChild();
	public abstract org.w3c.dom.Node getCurrentNode();
	public abstract boolean getExpandEntityReferences();
	public abstract org.w3c.dom.traversal.NodeFilter getFilter();
	public abstract org.w3c.dom.Node getRoot();
	public abstract int getWhatToShow();
	public abstract org.w3c.dom.Node lastChild();
	public abstract org.w3c.dom.Node nextNode();
	public abstract org.w3c.dom.Node nextSibling();
	public abstract org.w3c.dom.Node parentNode();
	public abstract org.w3c.dom.Node previousNode();
	public abstract org.w3c.dom.Node previousSibling();
	public abstract void setCurrentNode(org.w3c.dom.Node var0);
}

