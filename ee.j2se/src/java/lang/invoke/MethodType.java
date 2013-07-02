/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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

package java.lang.invoke;
public final class MethodType implements java.io.Serializable {
	public java.lang.invoke.MethodType appendParameterTypes(java.util.List<java.lang.Class<?>> var0) { return null; }
	public java.lang.invoke.MethodType appendParameterTypes(java.lang.Class<?>... var0) { return null; }
	public java.lang.invoke.MethodType changeParameterType(int var0, java.lang.Class<?> var1) { return null; }
	public java.lang.invoke.MethodType changeReturnType(java.lang.Class<?> var0) { return null; }
	public java.lang.invoke.MethodType dropParameterTypes(int var0, int var1) { return null; }
	public java.lang.invoke.MethodType erase() { return null; }
	public static java.lang.invoke.MethodType fromMethodDescriptorString(java.lang.String var0, java.lang.ClassLoader var1) { return null; }
	public java.lang.invoke.MethodType generic() { return null; }
	public static java.lang.invoke.MethodType genericMethodType(int var0) { return null; }
	public static java.lang.invoke.MethodType genericMethodType(int var0, boolean var1) { return null; }
	public boolean hasPrimitives() { return false; }
	public boolean hasWrappers() { return false; }
	public java.lang.invoke.MethodType insertParameterTypes(int var0, java.util.List<java.lang.Class<?>> var1) { return null; }
	public java.lang.invoke.MethodType insertParameterTypes(int var0, java.lang.Class<?>... var1) { return null; }
	public static java.lang.invoke.MethodType methodType(java.lang.Class<?> var0) { return null; }
	public static java.lang.invoke.MethodType methodType(java.lang.Class<?> var0, java.lang.Class<?> var1) { return null; }
	public static java.lang.invoke.MethodType methodType(java.lang.Class<?> var0, java.lang.Class<?> var1, java.lang.Class<?>... var2) { return null; }
	public static java.lang.invoke.MethodType methodType(java.lang.Class<?> var0, java.lang.invoke.MethodType var1) { return null; }
	public static java.lang.invoke.MethodType methodType(java.lang.Class<?> var0, java.util.List<java.lang.Class<?>> var1) { return null; }
	public static java.lang.invoke.MethodType methodType(java.lang.Class<?> var0, java.lang.Class<?>[] var1) { return null; }
	public java.lang.Class<?>[] parameterArray() { return null; }
	public int parameterCount() { return 0; }
	public java.util.List<java.lang.Class<?>> parameterList() { return null; }
	public java.lang.Class<?> parameterType(int var0) { return null; }
	public java.lang.Class<?> returnType() { return null; }
	public java.lang.String toMethodDescriptorString() { return null; }
	public java.lang.invoke.MethodType unwrap() { return null; }
	public java.lang.invoke.MethodType wrap() { return null; }
	private MethodType() { } /* generated constructor to prevent compiler adding default public constructor */
}

