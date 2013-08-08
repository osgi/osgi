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

package java.util.concurrent;
public abstract class ForkJoinTask<V> implements java.io.Serializable, java.util.concurrent.Future<V> {
	public ForkJoinTask() { } 
	public static java.util.concurrent.ForkJoinTask<?> adapt(java.lang.Runnable var0) { return null; }
	public static <T> java.util.concurrent.ForkJoinTask<T> adapt(java.lang.Runnable var0, T var1) { return null; }
	public static <T> java.util.concurrent.ForkJoinTask<T> adapt(java.util.concurrent.Callable<? extends T> var0) { return null; }
	public boolean cancel(boolean var0) { return false; }
	public void complete(V var0) { }
	public void completeExceptionally(java.lang.Throwable var0) { }
	protected abstract boolean exec();
	public final java.util.concurrent.ForkJoinTask<V> fork() { return null; }
	public final V get() throws java.lang.InterruptedException, java.util.concurrent.ExecutionException { return null; }
	public final V get(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException { return null; }
	public final java.lang.Throwable getException() { return null; }
	public static java.util.concurrent.ForkJoinPool getPool() { return null; }
	public static int getQueuedTaskCount() { return 0; }
	public abstract V getRawResult();
	public static int getSurplusQueuedTaskCount() { return 0; }
	public static void helpQuiesce() { }
	public static boolean inForkJoinPool() { return false; }
	public final V invoke() { return null; }
	public static <T extends java.util.concurrent.ForkJoinTask<?>> java.util.Collection<T> invokeAll(java.util.Collection<T> var0) { return null; }
	public static void invokeAll(java.util.concurrent.ForkJoinTask<?> var0, java.util.concurrent.ForkJoinTask<?> var1) { }
	public static void invokeAll(java.util.concurrent.ForkJoinTask<?>... var0) { }
	public final boolean isCancelled() { return false; }
	public final boolean isCompletedAbnormally() { return false; }
	public final boolean isCompletedNormally() { return false; }
	public final boolean isDone() { return false; }
	public final V join() { return null; }
	protected static java.util.concurrent.ForkJoinTask<?> peekNextLocalTask() { return null; }
	protected static java.util.concurrent.ForkJoinTask<?> pollNextLocalTask() { return null; }
	protected static java.util.concurrent.ForkJoinTask<?> pollTask() { return null; }
	public final void quietlyInvoke() { }
	public final void quietlyJoin() { }
	public void reinitialize() { }
	protected abstract void setRawResult(V var0);
	public boolean tryUnfork() { return false; }
}

