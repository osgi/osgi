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

package org.w3c.dom.traversal;
public interface TreeWalker {
	org.w3c.dom.Node firstChild();
	org.w3c.dom.Node getCurrentNode();
	boolean getExpandEntityReferences();
	org.w3c.dom.traversal.NodeFilter getFilter();
	org.w3c.dom.Node getRoot();
	int getWhatToShow();
	org.w3c.dom.Node lastChild();
	org.w3c.dom.Node nextNode();
	org.w3c.dom.Node nextSibling();
	org.w3c.dom.Node parentNode();
	org.w3c.dom.Node previousNode();
	org.w3c.dom.Node previousSibling();
	void setCurrentNode(org.w3c.dom.Node var0);
}

