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

package javax.swing.text;
public abstract interface MutableAttributeSet extends javax.swing.text.AttributeSet {
	public abstract void addAttribute(java.lang.Object var0, java.lang.Object var1);
	public abstract void addAttributes(javax.swing.text.AttributeSet var0);
	public abstract void removeAttribute(java.lang.Object var0);
	public abstract void removeAttributes(java.util.Enumeration var0);
	public abstract void removeAttributes(javax.swing.text.AttributeSet var0);
	public abstract void setResolveParent(javax.swing.text.AttributeSet var0);
}

