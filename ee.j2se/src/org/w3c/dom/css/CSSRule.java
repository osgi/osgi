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

package org.w3c.dom.css;
public abstract interface CSSRule {
	public abstract java.lang.String getCssText();
	public abstract org.w3c.dom.css.CSSRule getParentRule();
	public abstract org.w3c.dom.css.CSSStyleSheet getParentStyleSheet();
	public abstract short getType();
	public abstract void setCssText(java.lang.String var0);
	public final static short CHARSET_RULE = 2;
	public final static short FONT_FACE_RULE = 5;
	public final static short IMPORT_RULE = 3;
	public final static short MEDIA_RULE = 4;
	public final static short PAGE_RULE = 6;
	public final static short STYLE_RULE = 1;
	public final static short UNKNOWN_RULE = 0;
}

