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

package org.w3c.dom.xpath;
public interface XPathResult {
	public final static short ANY_TYPE = 0;
	public final static short ANY_UNORDERED_NODE_TYPE = 8;
	public final static short BOOLEAN_TYPE = 3;
	public final static short FIRST_ORDERED_NODE_TYPE = 9;
	public final static short NUMBER_TYPE = 1;
	public final static short ORDERED_NODE_ITERATOR_TYPE = 5;
	public final static short ORDERED_NODE_SNAPSHOT_TYPE = 7;
	public final static short STRING_TYPE = 2;
	public final static short UNORDERED_NODE_ITERATOR_TYPE = 4;
	public final static short UNORDERED_NODE_SNAPSHOT_TYPE = 6;
	boolean getBooleanValue();
	boolean getInvalidIteratorState();
	double getNumberValue();
	short getResultType();
	org.w3c.dom.Node getSingleNodeValue();
	int getSnapshotLength();
	java.lang.String getStringValue();
	org.w3c.dom.Node iterateNext();
	org.w3c.dom.Node snapshotItem(int var0);
}

