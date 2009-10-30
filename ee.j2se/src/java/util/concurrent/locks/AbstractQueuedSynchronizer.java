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
public abstract class AbstractQueuedSynchronizer implements java.io.Serializable {
	public class ConditionObject implements java.io.Serializable, java.util.concurrent.locks.Condition {
		public ConditionObject() { } 
		public final void await() throws java.lang.InterruptedException { }
		public final boolean await(long var0, java.util.concurrent.TimeUnit var1) throws java.lang.InterruptedException { return false; }
		public final long awaitNanos(long var0) throws java.lang.InterruptedException { return 0l; }
		public final void awaitUninterruptibly() { }
		public final boolean awaitUntil(java.util.Date var0) throws java.lang.InterruptedException { return false; }
		protected final int getWaitQueueLength() { return 0; }
		protected final java.util.Collection<java.lang.Thread> getWaitingThreads() { return null; }
		protected final boolean hasWaiters() { return false; }
		public final void signal() { }
		public final void signalAll() { }
	}
	protected AbstractQueuedSynchronizer() { } 
	public final void acquire(int var0) { }
	public final void acquireInterruptibly(int var0) throws java.lang.InterruptedException { }
	public final void acquireShared(int var0) { }
	public final void acquireSharedInterruptibly(int var0) throws java.lang.InterruptedException { }
	protected final boolean compareAndSetState(int var0, int var1) { return false; }
	public final java.util.Collection<java.lang.Thread> getExclusiveQueuedThreads() { return null; }
	public final java.lang.Thread getFirstQueuedThread() { return null; }
	public final int getQueueLength() { return 0; }
	public final java.util.Collection<java.lang.Thread> getQueuedThreads() { return null; }
	public final java.util.Collection<java.lang.Thread> getSharedQueuedThreads() { return null; }
	protected final int getState() { return 0; }
	public final int getWaitQueueLength(java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject var0) { return 0; }
	public final java.util.Collection<java.lang.Thread> getWaitingThreads(java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject var0) { return null; }
	public final boolean hasContended() { return false; }
	public final boolean hasQueuedThreads() { return false; }
	public final boolean hasWaiters(java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject var0) { return false; }
	protected boolean isHeldExclusively() { return false; }
	public final boolean isQueued(java.lang.Thread var0) { return false; }
	public final boolean owns(java.util.concurrent.locks.AbstractQueuedSynchronizer.ConditionObject var0) { return false; }
	public final boolean release(int var0) { return false; }
	public final boolean releaseShared(int var0) { return false; }
	protected final void setState(int var0) { }
	protected boolean tryAcquire(int var0) { return false; }
	public final boolean tryAcquireNanos(int var0, long var1) throws java.lang.InterruptedException { return false; }
	protected int tryAcquireShared(int var0) { return 0; }
	public final boolean tryAcquireSharedNanos(int var0, long var1) throws java.lang.InterruptedException { return false; }
	protected boolean tryRelease(int var0) { return false; }
	protected boolean tryReleaseShared(int var0) { return false; }
}

