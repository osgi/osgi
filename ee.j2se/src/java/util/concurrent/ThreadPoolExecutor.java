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
public class ThreadPoolExecutor extends java.util.concurrent.AbstractExecutorService {
	public static class AbortPolicy implements java.util.concurrent.RejectedExecutionHandler {
		public AbortPolicy() { } 
		public void rejectedExecution(java.lang.Runnable var0, java.util.concurrent.ThreadPoolExecutor var1) { }
	}
	public static class CallerRunsPolicy implements java.util.concurrent.RejectedExecutionHandler {
		public CallerRunsPolicy() { } 
		public void rejectedExecution(java.lang.Runnable var0, java.util.concurrent.ThreadPoolExecutor var1) { }
	}
	public static class DiscardOldestPolicy implements java.util.concurrent.RejectedExecutionHandler {
		public DiscardOldestPolicy() { } 
		public void rejectedExecution(java.lang.Runnable var0, java.util.concurrent.ThreadPoolExecutor var1) { }
	}
	public static class DiscardPolicy implements java.util.concurrent.RejectedExecutionHandler {
		public DiscardPolicy() { } 
		public void rejectedExecution(java.lang.Runnable var0, java.util.concurrent.ThreadPoolExecutor var1) { }
	}
	public ThreadPoolExecutor(int var0, int var1, long var2, java.util.concurrent.TimeUnit var3, java.util.concurrent.BlockingQueue<java.lang.Runnable> var4) { } 
	public ThreadPoolExecutor(int var0, int var1, long var2, java.util.concurrent.TimeUnit var3, java.util.concurrent.BlockingQueue<java.lang.Runnable> var4, java.util.concurrent.RejectedExecutionHandler var5) { } 
	public ThreadPoolExecutor(int var0, int var1, long var2, java.util.concurrent.TimeUnit var3, java.util.concurrent.BlockingQueue<java.lang.Runnable> var4, java.util.concurrent.ThreadFactory var5) { } 
	public ThreadPoolExecutor(int var0, int var1, long var2, java.util.concurrent.TimeUnit var3, java.util.concurrent.BlockingQueue<java.lang.Runnable> var4, java.util.concurrent.ThreadFactory var5, java.util.concurrent.RejectedExecutionHandler var6) { } 
	protected void afterExecute(java.lang.Runnable var0, java.lang.Throwable var1) { }
	public void allowCoreThreadTimeOut(boolean var0) { }
	public boolean allowsCoreThreadTimeOut() { return false; }
	public boolean awaitTermination(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return false; }
	protected void beforeExecute(java.lang.Thread var0, java.lang.Runnable var1) { }
	public void execute(java.lang.Runnable var0) { }
	protected void finalize() { }
	public int getActiveCount() { return 0; }
	public long getCompletedTaskCount() { return 0l; }
	public int getCorePoolSize() { return 0; }
	public long getKeepAliveTime(java.util.concurrent.TimeUnit var0) { return 0l; }
	public int getLargestPoolSize() { return 0; }
	public int getMaximumPoolSize() { return 0; }
	public int getPoolSize() { return 0; }
	public java.util.concurrent.BlockingQueue<java.lang.Runnable> getQueue() { return null; }
	public java.util.concurrent.RejectedExecutionHandler getRejectedExecutionHandler() { return null; }
	public long getTaskCount() { return 0l; }
	public java.util.concurrent.ThreadFactory getThreadFactory() { return null; }
	public boolean isShutdown() { return false; }
	public boolean isTerminated() { return false; }
	public boolean isTerminating() { return false; }
	public int prestartAllCoreThreads() { return 0; }
	public boolean prestartCoreThread() { return false; }
	public void purge() { }
	public boolean remove(java.lang.Runnable var0) { return false; }
	public void setCorePoolSize(int var0) { }
	public void setKeepAliveTime(long var0, java.util.concurrent.TimeUnit var1) { }
	public void setMaximumPoolSize(int var0) { }
	public void setRejectedExecutionHandler(java.util.concurrent.RejectedExecutionHandler var0) { }
	public void setThreadFactory(java.util.concurrent.ThreadFactory var0) { }
	public void shutdown() { }
	public java.util.List<java.lang.Runnable> shutdownNow() { return null; }
	protected void terminated() { }
}

