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
public interface HTMLFrameElement extends org.w3c.dom.html.HTMLElement {
	org.w3c.dom.Document getContentDocument();
	java.lang.String getFrameBorder();
	java.lang.String getLongDesc();
	java.lang.String getMarginHeight();
	java.lang.String getMarginWidth();
	java.lang.String getName();
	boolean getNoResize();
	java.lang.String getScrolling();
	java.lang.String getSrc();
	void setFrameBorder(java.lang.String var0);
	void setLongDesc(java.lang.String var0);
	void setMarginHeight(java.lang.String var0);
	void setMarginWidth(java.lang.String var0);
	void setName(java.lang.String var0);
	void setNoResize(boolean var0);
	void setScrolling(java.lang.String var0);
	void setSrc(java.lang.String var0);
}

