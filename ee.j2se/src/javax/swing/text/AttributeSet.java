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

package javax.swing.text;
public abstract interface AttributeSet {
	public abstract boolean containsAttribute(java.lang.Object var0, java.lang.Object var1);
	public abstract boolean containsAttributes(javax.swing.text.AttributeSet var0);
	public abstract javax.swing.text.AttributeSet copyAttributes();
	public abstract java.lang.Object getAttribute(java.lang.Object var0);
	public abstract int getAttributeCount();
	public abstract java.util.Enumeration getAttributeNames();
	public abstract javax.swing.text.AttributeSet getResolveParent();
	public abstract boolean isDefined(java.lang.Object var0);
	public abstract boolean isEqual(javax.swing.text.AttributeSet var0);
	public final static java.lang.Object NameAttribute = null;
	public final static java.lang.Object ResolveAttribute = null;
	public static abstract interface CharacterAttribute {
	}
	public static abstract interface ColorAttribute {
	}
	public static abstract interface FontAttribute {
	}
	public static abstract interface ParagraphAttribute {
	}
}

