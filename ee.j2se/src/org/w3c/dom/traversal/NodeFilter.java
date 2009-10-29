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

package org.w3c.dom.traversal;
public abstract interface NodeFilter {
	public abstract short acceptNode(org.w3c.dom.Node var0);
	public final static short FILTER_ACCEPT = 1;
	public final static short FILTER_REJECT = 2;
	public final static short FILTER_SKIP = 3;
	public final static int SHOW_ALL = -1;
	public final static int SHOW_ATTRIBUTE = 2;
	public final static int SHOW_CDATA_SECTION = 8;
	public final static int SHOW_COMMENT = 128;
	public final static int SHOW_DOCUMENT = 256;
	public final static int SHOW_DOCUMENT_FRAGMENT = 1024;
	public final static int SHOW_DOCUMENT_TYPE = 512;
	public final static int SHOW_ELEMENT = 1;
	public final static int SHOW_ENTITY = 32;
	public final static int SHOW_ENTITY_REFERENCE = 16;
	public final static int SHOW_NOTATION = 2048;
	public final static int SHOW_PROCESSING_INSTRUCTION = 64;
	public final static int SHOW_TEXT = 4;
}

