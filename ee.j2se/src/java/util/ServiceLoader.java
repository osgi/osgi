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

package java.util;
public final class ServiceLoader<S> implements java.lang.Iterable<S> {
	public java.util.Iterator<S> iterator() { return null; }
	public static <S> java.util.ServiceLoader<S> load(java.lang.Class<S> var0) { return null; }
	public static <S> java.util.ServiceLoader<S> load(java.lang.Class<S> var0, java.lang.ClassLoader var1) { return null; }
	public static <S> java.util.ServiceLoader<S> loadInstalled(java.lang.Class<S> var0) { return null; }
	public void reload() { }
	private ServiceLoader() { } /* generated constructor to prevent compiler adding default public constructor */
}

