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
public class FutureTask<V> implements java.util.concurrent.RunnableFuture<V> {
	public FutureTask(java.lang.Runnable var0, V var1) { } 
	public FutureTask(java.util.concurrent.Callable<V> var0) { } 
	public boolean cancel(boolean var0) { return false; }
	protected void done() { }
	public V get() throws java.lang.InterruptedException, java.util.concurrent.ExecutionException { return null; }
	public V get(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException { return null; }
	public boolean isCancelled() { return false; }
	public boolean isDone() { return false; }
	public void run() { }
	protected boolean runAndReset() { return false; }
	protected void set(V var0) { }
	protected void setException(java.lang.Throwable var0) { }
}

