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
public class ReentrantReadWriteLock implements java.io.Serializable, java.util.concurrent.locks.ReadWriteLock {
	public static class ReadLock implements java.io.Serializable, java.util.concurrent.locks.Lock {
		protected ReadLock(java.util.concurrent.locks.ReentrantReadWriteLock var0) { } 
		public void lock() { }
		public void lockInterruptibly() throws java.lang.InterruptedException { }
		public java.util.concurrent.locks.Condition newCondition() { return null; }
		public boolean tryLock() { return false; }
		public boolean tryLock(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return false; }
		public void unlock() { }
	}
	public static class WriteLock implements java.io.Serializable, java.util.concurrent.locks.Lock {
		protected WriteLock(java.util.concurrent.locks.ReentrantReadWriteLock var0) { } 
		public void lock() { }
		public void lockInterruptibly() throws java.lang.InterruptedException { }
		public java.util.concurrent.locks.Condition newCondition() { return null; }
		public boolean tryLock() { return false; }
		public boolean tryLock(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return false; }
		public void unlock() { }
	}
	public ReentrantReadWriteLock() { } 
	public ReentrantReadWriteLock(boolean var0) { } 
	protected java.lang.Thread getOwner() { return null; }
	public final int getQueueLength() { return 0; }
	protected java.util.Collection<java.lang.Thread> getQueuedReaderThreads() { return null; }
	protected java.util.Collection<java.lang.Thread> getQueuedThreads() { return null; }
	protected java.util.Collection<java.lang.Thread> getQueuedWriterThreads() { return null; }
	public int getReadLockCount() { return 0; }
	public int getWaitQueueLength(java.util.concurrent.locks.Condition var0) { return 0; }
	protected java.util.Collection<java.lang.Thread> getWaitingThreads(java.util.concurrent.locks.Condition var0) { return null; }
	public int getWriteHoldCount() { return 0; }
	public final boolean hasQueuedThread(java.lang.Thread var0) { return false; }
	public final boolean hasQueuedThreads() { return false; }
	public boolean hasWaiters(java.util.concurrent.locks.Condition var0) { return false; }
	public final boolean isFair() { return false; }
	public boolean isWriteLocked() { return false; }
	public boolean isWriteLockedByCurrentThread() { return false; }
	public java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock readLock() { return null; }
	public java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock writeLock() { return null; }
}

