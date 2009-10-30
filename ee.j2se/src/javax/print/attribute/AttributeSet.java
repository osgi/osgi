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
public interface AttributeSet {
	boolean add(javax.print.attribute.Attribute var0);
	boolean addAll(javax.print.attribute.AttributeSet var0);
	void clear();
	boolean containsKey(java.lang.Class<?> var0);
	boolean containsValue(javax.print.attribute.Attribute var0);
	boolean equals(java.lang.Object var0);
	javax.print.attribute.Attribute get(java.lang.Class<?> var0);
	int hashCode();
	boolean isEmpty();
	boolean remove(java.lang.Class<?> var0);
	boolean remove(javax.print.attribute.Attribute var0);
	int size();
	javax.print.attribute.Attribute[] toArray();
}

