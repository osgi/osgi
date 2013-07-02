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
public class MethodHandles {
	public static final class Lookup {
		public final static int PACKAGE = 8;
		public final static int PRIVATE = 2;
		public final static int PROTECTED = 4;
		public final static int PUBLIC = 1;
		public java.lang.invoke.MethodHandle bind(java.lang.Object var0, java.lang.String var1, java.lang.invoke.MethodType var2) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException { return null; }
		public java.lang.invoke.MethodHandle findConstructor(java.lang.Class<?> var0, java.lang.invoke.MethodType var1) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException { return null; }
		public java.lang.invoke.MethodHandle findGetter(java.lang.Class<?> var0, java.lang.String var1, java.lang.Class<?> var2) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException { return null; }
		public java.lang.invoke.MethodHandle findSetter(java.lang.Class<?> var0, java.lang.String var1, java.lang.Class<?> var2) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException { return null; }
		public java.lang.invoke.MethodHandle findSpecial(java.lang.Class<?> var0, java.lang.String var1, java.lang.invoke.MethodType var2, java.lang.Class<?> var3) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException { return null; }
		public java.lang.invoke.MethodHandle findStatic(java.lang.Class<?> var0, java.lang.String var1, java.lang.invoke.MethodType var2) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException { return null; }
		public java.lang.invoke.MethodHandle findStaticGetter(java.lang.Class<?> var0, java.lang.String var1, java.lang.Class<?> var2) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException { return null; }
		public java.lang.invoke.MethodHandle findStaticSetter(java.lang.Class<?> var0, java.lang.String var1, java.lang.Class<?> var2) throws java.lang.IllegalAccessException, java.lang.NoSuchFieldException { return null; }
		public java.lang.invoke.MethodHandle findVirtual(java.lang.Class<?> var0, java.lang.String var1, java.lang.invoke.MethodType var2) throws java.lang.IllegalAccessException, java.lang.NoSuchMethodException { return null; }
		public java.lang.invoke.MethodHandles.Lookup in(java.lang.Class<?> var0) { return null; }
		public java.lang.Class<?> lookupClass() { return null; }
		public int lookupModes() { return 0; }
		public java.lang.invoke.MethodHandle unreflect(java.lang.reflect.Method var0) throws java.lang.IllegalAccessException { return null; }
		public java.lang.invoke.MethodHandle unreflectConstructor(java.lang.reflect.Constructor var0) throws java.lang.IllegalAccessException { return null; }
		public java.lang.invoke.MethodHandle unreflectGetter(java.lang.reflect.Field var0) throws java.lang.IllegalAccessException { return null; }
		public java.lang.invoke.MethodHandle unreflectSetter(java.lang.reflect.Field var0) throws java.lang.IllegalAccessException { return null; }
		public java.lang.invoke.MethodHandle unreflectSpecial(java.lang.reflect.Method var0, java.lang.Class<?> var1) throws java.lang.IllegalAccessException { return null; }
		private Lookup() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	public static java.lang.invoke.MethodHandle arrayElementGetter(java.lang.Class<?> var0) { return null; }
	public static java.lang.invoke.MethodHandle arrayElementSetter(java.lang.Class<?> var0) { return null; }
	public static java.lang.invoke.MethodHandle catchException(java.lang.invoke.MethodHandle var0, java.lang.Class<? extends java.lang.Throwable> var1, java.lang.invoke.MethodHandle var2) { return null; }
	public static java.lang.invoke.MethodHandle constant(java.lang.Class<?> var0, java.lang.Object var1) { return null; }
	public static java.lang.invoke.MethodHandle dropArguments(java.lang.invoke.MethodHandle var0, int var1, java.util.List<java.lang.Class<?>> var2) { return null; }
	public static java.lang.invoke.MethodHandle dropArguments(java.lang.invoke.MethodHandle var0, int var1, java.lang.Class<?>... var2) { return null; }
	public static java.lang.invoke.MethodHandle exactInvoker(java.lang.invoke.MethodType var0) { return null; }
	public static java.lang.invoke.MethodHandle explicitCastArguments(java.lang.invoke.MethodHandle var0, java.lang.invoke.MethodType var1) { return null; }
	public static java.lang.invoke.MethodHandle filterArguments(java.lang.invoke.MethodHandle var0, int var1, java.lang.invoke.MethodHandle... var2) { return null; }
	public static java.lang.invoke.MethodHandle filterReturnValue(java.lang.invoke.MethodHandle var0, java.lang.invoke.MethodHandle var1) { return null; }
	public static java.lang.invoke.MethodHandle foldArguments(java.lang.invoke.MethodHandle var0, java.lang.invoke.MethodHandle var1) { return null; }
	public static java.lang.invoke.MethodHandle guardWithTest(java.lang.invoke.MethodHandle var0, java.lang.invoke.MethodHandle var1, java.lang.invoke.MethodHandle var2) { return null; }
	public static java.lang.invoke.MethodHandle identity(java.lang.Class<?> var0) { return null; }
	public static java.lang.invoke.MethodHandle insertArguments(java.lang.invoke.MethodHandle var0, int var1, java.lang.Object... var2) { return null; }
	public static java.lang.invoke.MethodHandle invoker(java.lang.invoke.MethodType var0) { return null; }
	public static java.lang.invoke.MethodHandles.Lookup lookup() { return null; }
	public static java.lang.invoke.MethodHandle permuteArguments(java.lang.invoke.MethodHandle var0, java.lang.invoke.MethodType var1, int... var2) { return null; }
	public static java.lang.invoke.MethodHandles.Lookup publicLookup() { return null; }
	public static java.lang.invoke.MethodHandle spreadInvoker(java.lang.invoke.MethodType var0, int var1) { return null; }
	public static java.lang.invoke.MethodHandle throwException(java.lang.Class<?> var0, java.lang.Class<? extends java.lang.Throwable> var1) { return null; }
	private MethodHandles() { } /* generated constructor to prevent compiler adding default public constructor */
}

