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

package javax.swing.text;
public class SimpleAttributeSet implements java.io.Serializable, java.lang.Cloneable, javax.swing.text.MutableAttributeSet {
	public final static javax.swing.text.AttributeSet EMPTY; static { EMPTY = null; }
	public SimpleAttributeSet() { } 
	public SimpleAttributeSet(javax.swing.text.AttributeSet var0) { } 
	public void addAttribute(java.lang.Object var0, java.lang.Object var1) { }
	public void addAttributes(javax.swing.text.AttributeSet var0) { }
	public java.lang.Object clone() { return null; }
	public boolean containsAttribute(java.lang.Object var0, java.lang.Object var1) { return false; }
	public boolean containsAttributes(javax.swing.text.AttributeSet var0) { return false; }
	public javax.swing.text.AttributeSet copyAttributes() { return null; }
	public java.lang.Object getAttribute(java.lang.Object var0) { return null; }
	public int getAttributeCount() { return 0; }
	public java.util.Enumeration<?> getAttributeNames() { return null; }
	public javax.swing.text.AttributeSet getResolveParent() { return null; }
	public int hashCode() { return 0; }
	public boolean isDefined(java.lang.Object var0) { return false; }
	public boolean isEmpty() { return false; }
	public boolean isEqual(javax.swing.text.AttributeSet var0) { return false; }
	public void removeAttribute(java.lang.Object var0) { }
	public void removeAttributes(java.util.Enumeration<?> var0) { }
	public void removeAttributes(javax.swing.text.AttributeSet var0) { }
	public void setResolveParent(javax.swing.text.AttributeSet var0) { }
}

