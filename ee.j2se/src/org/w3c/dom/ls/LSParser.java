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

package org.w3c.dom.ls;
public interface LSParser {
	public final static short ACTION_APPEND_AS_CHILDREN = 1;
	public final static short ACTION_INSERT_AFTER = 4;
	public final static short ACTION_INSERT_BEFORE = 3;
	public final static short ACTION_REPLACE = 5;
	public final static short ACTION_REPLACE_CHILDREN = 2;
	void abort();
	boolean getAsync();
	boolean getBusy();
	org.w3c.dom.DOMConfiguration getDomConfig();
	org.w3c.dom.ls.LSParserFilter getFilter();
	org.w3c.dom.Document parse(org.w3c.dom.ls.LSInput var0);
	org.w3c.dom.Document parseURI(java.lang.String var0);
	org.w3c.dom.Node parseWithContext(org.w3c.dom.ls.LSInput var0, org.w3c.dom.Node var1, short var2);
	void setFilter(org.w3c.dom.ls.LSParserFilter var0);
}

