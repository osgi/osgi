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
public class ForkJoinPool extends java.util.concurrent.AbstractExecutorService {
	public interface ForkJoinWorkerThreadFactory {
		java.util.concurrent.ForkJoinWorkerThread newThread(java.util.concurrent.ForkJoinPool var0);
	}
	public interface ManagedBlocker {
		boolean block() throws java.lang.InterruptedException;
		boolean isReleasable();
	}
	public final static java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory; static { defaultForkJoinWorkerThreadFactory = null; }
	public ForkJoinPool() { } 
	public ForkJoinPool(int var0) { } 
	public ForkJoinPool(int var0, java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory var1, java.lang.Thread.UncaughtExceptionHandler var2, boolean var3) { } 
	public boolean awaitTermination(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return false; }
	protected int drainTasksTo(java.util.Collection<? super java.util.concurrent.ForkJoinTask<?>> var0) { return 0; }
	public void execute(java.lang.Runnable var0) { }
	public void execute(java.util.concurrent.ForkJoinTask<?> var0) { }
	public int getActiveThreadCount() { return 0; }
	public boolean getAsyncMode() { return false; }
	public java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory getFactory() { return null; }
	public int getParallelism() { return 0; }
	public int getPoolSize() { return 0; }
	public int getQueuedSubmissionCount() { return 0; }
	public long getQueuedTaskCount() { return 0l; }
	public int getRunningThreadCount() { return 0; }
	public long getStealCount() { return 0l; }
	public java.lang.Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() { return null; }
	public boolean hasQueuedSubmissions() { return false; }
	public <T> T invoke(java.util.concurrent.ForkJoinTask<T> var0) { return null; }
	public <T> java.util.List<java.util.concurrent.Future<T>> invokeAll(java.util.Collection<? extends java.util.concurrent.Callable<T>> var0) { return null; }
	public boolean isQuiescent() { return false; }
	public boolean isShutdown() { return false; }
	public boolean isTerminated() { return false; }
	public boolean isTerminating() { return false; }
	public static void managedBlock(java.util.concurrent.ForkJoinPool.ManagedBlocker var0) throws java.lang.InterruptedException { }
	protected java.util.concurrent.ForkJoinTask<?> pollSubmission() { return null; }
	public void shutdown() { }
	public java.util.List<java.lang.Runnable> shutdownNow() { return null; }
	public java.util.concurrent.ForkJoinTask<?> submit(java.lang.Runnable var0) { return null; }
	public <T> java.util.concurrent.ForkJoinTask<T> submit(java.lang.Runnable var0, T var1) { return null; }
	public <T> java.util.concurrent.ForkJoinTask<T> submit(java.util.concurrent.Callable<T> var0) { return null; }
	public <T> java.util.concurrent.ForkJoinTask<T> submit(java.util.concurrent.ForkJoinTask<T> var0) { return null; }
}

