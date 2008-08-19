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

package javax.print.attribute;
public abstract interface AttributeSet {
	public abstract boolean add(javax.print.attribute.Attribute var0);
	public abstract boolean addAll(javax.print.attribute.AttributeSet var0);
	public abstract void clear();
	public abstract boolean containsKey(java.lang.Class var0);
	public abstract boolean containsValue(javax.print.attribute.Attribute var0);
	public abstract boolean equals(java.lang.Object var0);
	public abstract javax.print.attribute.Attribute get(java.lang.Class var0);
	public abstract int hashCode();
	public abstract boolean isEmpty();
	public abstract boolean remove(java.lang.Class var0);
	public abstract boolean remove(javax.print.attribute.Attribute var0);
	public abstract int size();
	public abstract javax.print.attribute.Attribute[] toArray();
}

