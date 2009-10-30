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

package org.w3c.dom.ranges;
public interface Range {
	public final static short END_TO_END = 2;
	public final static short END_TO_START = 3;
	public final static short START_TO_END = 1;
	public final static short START_TO_START = 0;
	org.w3c.dom.DocumentFragment cloneContents();
	org.w3c.dom.ranges.Range cloneRange();
	void collapse(boolean var0);
	short compareBoundaryPoints(short var0, org.w3c.dom.ranges.Range var1);
	void deleteContents();
	void detach();
	org.w3c.dom.DocumentFragment extractContents();
	boolean getCollapsed();
	org.w3c.dom.Node getCommonAncestorContainer();
	org.w3c.dom.Node getEndContainer();
	int getEndOffset();
	org.w3c.dom.Node getStartContainer();
	int getStartOffset();
	void insertNode(org.w3c.dom.Node var0);
	void selectNode(org.w3c.dom.Node var0);
	void selectNodeContents(org.w3c.dom.Node var0);
	void setEnd(org.w3c.dom.Node var0, int var1);
	void setEndAfter(org.w3c.dom.Node var0);
	void setEndBefore(org.w3c.dom.Node var0);
	void setStart(org.w3c.dom.Node var0, int var1);
	void setStartAfter(org.w3c.dom.Node var0);
	void setStartBefore(org.w3c.dom.Node var0);
	void surroundContents(org.w3c.dom.Node var0);
	java.lang.String toString();
}

