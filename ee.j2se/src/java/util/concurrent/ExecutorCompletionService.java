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
public class ExecutorCompletionService<V> implements java.util.concurrent.CompletionService<V> {
	public ExecutorCompletionService(java.util.concurrent.Executor var0) { } 
	public ExecutorCompletionService(java.util.concurrent.Executor var0, java.util.concurrent.BlockingQueue<java.util.concurrent.Future<V>> var1) { } 
	public java.util.concurrent.Future<V> poll() { return null; }
	public java.util.concurrent.Future<V> poll(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return null; }
	public java.util.concurrent.Future<V> submit(java.lang.Runnable var0, V var1) { return null; }
	public java.util.concurrent.Future<V> submit(java.util.concurrent.Callable<V> var0) { return null; }
	public java.util.concurrent.Future<V> take() throws java.lang.InterruptedException { return null; }
}

