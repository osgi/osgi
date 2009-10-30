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
public class Semaphore implements java.io.Serializable {
	public Semaphore(int var0) { } 
	public Semaphore(int var0, boolean var1) { } 
	public void acquire() throws java.lang.InterruptedException { }
	public void acquire(int var0) throws java.lang.InterruptedException { }
	public void acquireUninterruptibly() { }
	public void acquireUninterruptibly(int var0) { }
	public int availablePermits() { return 0; }
	public int drainPermits() { return 0; }
	public final int getQueueLength() { return 0; }
	protected java.util.Collection<java.lang.Thread> getQueuedThreads() { return null; }
	public final boolean hasQueuedThreads() { return false; }
	public boolean isFair() { return false; }
	protected void reducePermits(int var0) { }
	public void release() { }
	public void release(int var0) { }
	public boolean tryAcquire() { return false; }
	public boolean tryAcquire(int var0) { return false; }
	public boolean tryAcquire(int var0, long var1, java.util.concurrent.TimeUnit var2) throws java.lang.InterruptedException { return false; }
	public boolean tryAcquire(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return false; }
}

