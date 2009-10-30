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

package java.util.concurrent;
public class Executors {
	public static java.util.concurrent.Callable<java.lang.Object> callable(java.lang.Runnable var0) { return null; }
	public static <T> java.util.concurrent.Callable<T> callable(java.lang.Runnable var0, T var1) { return null; }
	public static java.util.concurrent.Callable<java.lang.Object> callable(java.security.PrivilegedAction var0) { return null; }
	public static java.util.concurrent.Callable<java.lang.Object> callable(java.security.PrivilegedExceptionAction var0) { return null; }
	public static java.util.concurrent.ThreadFactory defaultThreadFactory() { return null; }
	public static java.util.concurrent.ExecutorService newCachedThreadPool() { return null; }
	public static java.util.concurrent.ExecutorService newCachedThreadPool(java.util.concurrent.ThreadFactory var0) { return null; }
	public static java.util.concurrent.ExecutorService newFixedThreadPool(int var0) { return null; }
	public static java.util.concurrent.ExecutorService newFixedThreadPool(int var0, java.util.concurrent.ThreadFactory var1) { return null; }
	public static java.util.concurrent.ScheduledExecutorService newScheduledThreadPool(int var0) { return null; }
	public static java.util.concurrent.ScheduledExecutorService newScheduledThreadPool(int var0, java.util.concurrent.ThreadFactory var1) { return null; }
	public static java.util.concurrent.ExecutorService newSingleThreadExecutor() { return null; }
	public static java.util.concurrent.ExecutorService newSingleThreadExecutor(java.util.concurrent.ThreadFactory var0) { return null; }
	public static java.util.concurrent.ScheduledExecutorService newSingleThreadScheduledExecutor() { return null; }
	public static java.util.concurrent.ScheduledExecutorService newSingleThreadScheduledExecutor(java.util.concurrent.ThreadFactory var0) { return null; }
	public static <T> java.util.concurrent.Callable<T> privilegedCallable(java.util.concurrent.Callable<T> var0) { return null; }
	public static <T> java.util.concurrent.Callable<T> privilegedCallableUsingCurrentClassLoader(java.util.concurrent.Callable<T> var0) { return null; }
	public static java.util.concurrent.ThreadFactory privilegedThreadFactory() { return null; }
	public static java.util.concurrent.ExecutorService unconfigurableExecutorService(java.util.concurrent.ExecutorService var0) { return null; }
	public static java.util.concurrent.ScheduledExecutorService unconfigurableScheduledExecutorService(java.util.concurrent.ScheduledExecutorService var0) { return null; }
	private Executors() { } /* generated constructor to prevent compiler adding default public constructor */
}

