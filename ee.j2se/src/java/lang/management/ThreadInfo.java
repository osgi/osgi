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

package java.lang.management;
public class ThreadInfo {
	public static java.lang.management.ThreadInfo from(javax.management.openmbean.CompositeData var0) { return null; }
	public long getBlockedCount() { return 0l; }
	public long getBlockedTime() { return 0l; }
	public java.lang.management.LockInfo getLockInfo() { return null; }
	public java.lang.String getLockName() { return null; }
	public long getLockOwnerId() { return 0l; }
	public java.lang.String getLockOwnerName() { return null; }
	public java.lang.management.MonitorInfo[] getLockedMonitors() { return null; }
	public java.lang.management.LockInfo[] getLockedSynchronizers() { return null; }
	public java.lang.StackTraceElement[] getStackTrace() { return null; }
	public long getThreadId() { return 0l; }
	public java.lang.String getThreadName() { return null; }
	public java.lang.Thread.State getThreadState() { return null; }
	public long getWaitedCount() { return 0l; }
	public long getWaitedTime() { return 0l; }
	public boolean isInNative() { return false; }
	public boolean isSuspended() { return false; }
	private ThreadInfo() { } /* generated constructor to prevent compiler adding default public constructor */
}

