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

package org.w3c.dom.css;
public abstract interface CSSStyleDeclaration {
	public abstract java.lang.String getCssText();
	public abstract int getLength();
	public abstract org.w3c.dom.css.CSSRule getParentRule();
	public abstract org.w3c.dom.css.CSSValue getPropertyCSSValue(java.lang.String var0);
	public abstract java.lang.String getPropertyPriority(java.lang.String var0);
	public abstract java.lang.String getPropertyValue(java.lang.String var0);
	public abstract java.lang.String item(int var0);
	public abstract java.lang.String removeProperty(java.lang.String var0);
	public abstract void setCssText(java.lang.String var0);
	public abstract void setProperty(java.lang.String var0, java.lang.String var1, java.lang.String var2);
}

