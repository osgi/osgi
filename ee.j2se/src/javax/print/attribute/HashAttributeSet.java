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

package javax.print.attribute;
public class HashAttributeSet implements java.io.Serializable, javax.print.attribute.AttributeSet {
	public HashAttributeSet() { } 
	protected HashAttributeSet(java.lang.Class<?> var0) { } 
	public HashAttributeSet(javax.print.attribute.Attribute var0) { } 
	protected HashAttributeSet(javax.print.attribute.Attribute var0, java.lang.Class<?> var1) { } 
	public HashAttributeSet(javax.print.attribute.AttributeSet var0) { } 
	protected HashAttributeSet(javax.print.attribute.AttributeSet var0, java.lang.Class<?> var1) { } 
	public HashAttributeSet(javax.print.attribute.Attribute[] var0) { } 
	protected HashAttributeSet(javax.print.attribute.Attribute[] var0, java.lang.Class<?> var1) { } 
	public boolean add(javax.print.attribute.Attribute var0) { return false; }
	public boolean addAll(javax.print.attribute.AttributeSet var0) { return false; }
	public void clear() { }
	public boolean containsKey(java.lang.Class<?> var0) { return false; }
	public boolean containsValue(javax.print.attribute.Attribute var0) { return false; }
	public javax.print.attribute.Attribute get(java.lang.Class<?> var0) { return null; }
	public int hashCode() { return 0; }
	public boolean isEmpty() { return false; }
	public boolean remove(java.lang.Class<?> var0) { return false; }
	public boolean remove(javax.print.attribute.Attribute var0) { return false; }
	public int size() { return 0; }
	public javax.print.attribute.Attribute[] toArray() { return null; }
}

