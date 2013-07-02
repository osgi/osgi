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
public abstract class MethodHandle {
	public java.lang.invoke.MethodHandle asCollector(java.lang.Class<?> var0, int var1) { return null; }
	public java.lang.invoke.MethodHandle asFixedArity() { return null; }
	public java.lang.invoke.MethodHandle asSpreader(java.lang.Class<?> var0, int var1) { return null; }
	public java.lang.invoke.MethodHandle asType(java.lang.invoke.MethodType var0) { return null; }
	public java.lang.invoke.MethodHandle asVarargsCollector(java.lang.Class<?> var0) { return null; }
	public java.lang.invoke.MethodHandle bindTo(java.lang.Object var0) { return null; }
	public final java.lang.Object invoke(java.lang.Object... var0) throws java.lang.Throwable { return null; }
	public final java.lang.Object invokeExact(java.lang.Object... var0) throws java.lang.Throwable { return null; }
	public java.lang.Object invokeWithArguments(java.util.List<?> var0) throws java.lang.Throwable { return null; }
	public java.lang.Object invokeWithArguments(java.lang.Object... var0) throws java.lang.Throwable { return null; }
	public boolean isVarargsCollector() { return false; }
	public java.lang.invoke.MethodType type() { return null; }
	private MethodHandle() { } /* generated constructor to prevent compiler adding default public constructor */
}

