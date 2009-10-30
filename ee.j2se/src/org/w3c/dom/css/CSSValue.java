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

package org.w3c.dom.css;
public interface CSSValue {
	public final static short CSS_CUSTOM = 3;
	public final static short CSS_INHERIT = 0;
	public final static short CSS_PRIMITIVE_VALUE = 1;
	public final static short CSS_VALUE_LIST = 2;
	java.lang.String getCssText();
	short getCssValueType();
	void setCssText(java.lang.String var0);
}

