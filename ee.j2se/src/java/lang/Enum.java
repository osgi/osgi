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

package java.lang;
public abstract class Enum<E extends java.lang.Enum<E>> implements java.io.Serializable, java.lang.Comparable<E> {
	protected Enum(java.lang.String var0, int var1) { } 
	protected final java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	public final int compareTo(E var0) { return 0; }
	public final boolean equals(java.lang.Object var0) { return false; }
	public final java.lang.Class<E> getDeclaringClass() { return null; }
	public final int hashCode() { return 0; }
	public final java.lang.String name() { return null; }
	public final int ordinal() { return 0; }
	public static <T extends java.lang.Enum<T>> T valueOf(java.lang.Class<T> var0, java.lang.String var1) { return null; }
}

