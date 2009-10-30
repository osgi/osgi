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

package java.util.concurrent.locks;
public class ReentrantLock implements java.io.Serializable, java.util.concurrent.locks.Lock {
	public ReentrantLock() { } 
	public ReentrantLock(boolean var0) { } 
	public int getHoldCount() { return 0; }
	protected java.lang.Thread getOwner() { return null; }
	public final int getQueueLength() { return 0; }
	protected java.util.Collection<java.lang.Thread> getQueuedThreads() { return null; }
	public int getWaitQueueLength(java.util.concurrent.locks.Condition var0) { return 0; }
	protected java.util.Collection<java.lang.Thread> getWaitingThreads(java.util.concurrent.locks.Condition var0) { return null; }
	public final boolean hasQueuedThread(java.lang.Thread var0) { return false; }
	public final boolean hasQueuedThreads() { return false; }
	public boolean hasWaiters(java.util.concurrent.locks.Condition var0) { return false; }
	public final boolean isFair() { return false; }
	public boolean isHeldByCurrentThread() { return false; }
	public boolean isLocked() { return false; }
	public void lock() { }
	public void lockInterruptibly() throws java.lang.InterruptedException { }
	public java.util.concurrent.locks.Condition newCondition() { return null; }
	public boolean tryLock() { return false; }
	public boolean tryLock(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return false; }
	public void unlock() { }
}

