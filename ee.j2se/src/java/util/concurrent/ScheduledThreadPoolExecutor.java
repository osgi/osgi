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
public class ScheduledThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor implements java.util.concurrent.ScheduledExecutorService {
	public ScheduledThreadPoolExecutor(int var0)  { super(0, 0, 0l, (java.util.concurrent.TimeUnit) null, (java.util.concurrent.BlockingQueue<java.lang.Runnable>) null, (java.util.concurrent.ThreadFactory) null, (java.util.concurrent.RejectedExecutionHandler) null); } 
	public ScheduledThreadPoolExecutor(int var0, java.util.concurrent.RejectedExecutionHandler var1)  { super(0, 0, 0l, (java.util.concurrent.TimeUnit) null, (java.util.concurrent.BlockingQueue<java.lang.Runnable>) null, (java.util.concurrent.ThreadFactory) null, (java.util.concurrent.RejectedExecutionHandler) null); } 
	public ScheduledThreadPoolExecutor(int var0, java.util.concurrent.ThreadFactory var1)  { super(0, 0, 0l, (java.util.concurrent.TimeUnit) null, (java.util.concurrent.BlockingQueue<java.lang.Runnable>) null, (java.util.concurrent.ThreadFactory) null, (java.util.concurrent.RejectedExecutionHandler) null); } 
	public ScheduledThreadPoolExecutor(int var0, java.util.concurrent.ThreadFactory var1, java.util.concurrent.RejectedExecutionHandler var2)  { super(0, 0, 0l, (java.util.concurrent.TimeUnit) null, (java.util.concurrent.BlockingQueue<java.lang.Runnable>) null, (java.util.concurrent.ThreadFactory) null, (java.util.concurrent.RejectedExecutionHandler) null); } 
	public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() { return false; }
	public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() { return false; }
	public java.util.concurrent.ScheduledFuture<?> schedule(java.lang.Runnable var0, long var1, java.util.concurrent.TimeUnit var2) { return null; }
	public <V> java.util.concurrent.ScheduledFuture<V> schedule(java.util.concurrent.Callable<V> var0, long var1, java.util.concurrent.TimeUnit var2) { return null; }
	public java.util.concurrent.ScheduledFuture<?> scheduleAtFixedRate(java.lang.Runnable var0, long var1, long var2, java.util.concurrent.TimeUnit var3) { return null; }
	public java.util.concurrent.ScheduledFuture<?> scheduleWithFixedDelay(java.lang.Runnable var0, long var1, long var2, java.util.concurrent.TimeUnit var3) { return null; }
	public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean var0) { }
	public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean var0) { }
}

