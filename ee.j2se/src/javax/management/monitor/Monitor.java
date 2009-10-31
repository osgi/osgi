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

package javax.management.monitor;
public abstract class Monitor extends javax.management.NotificationBroadcasterSupport implements javax.management.MBeanRegistration, javax.management.monitor.MonitorMBean {
	protected final static int OBSERVED_ATTRIBUTE_ERROR_NOTIFIED = 2;
	protected final static int OBSERVED_ATTRIBUTE_TYPE_ERROR_NOTIFIED = 4;
	protected final static int OBSERVED_OBJECT_ERROR_NOTIFIED = 1;
	protected final static int RESET_FLAGS_ALREADY_NOTIFIED = 0;
	protected final static int RUNTIME_ERROR_NOTIFIED = 8;
	/** @deprecated */
	@java.lang.Deprecated
	protected int alreadyNotified;
	protected int[] alreadyNotifieds;
	protected final static int capacityIncrement = 16;
	/** @deprecated */
	@java.lang.Deprecated
	protected java.lang.String dbgTag;
	protected int elementCount;
	protected javax.management.MBeanServer server;
	public Monitor() { } 
	public void addObservedObject(javax.management.ObjectName var0) { }
	public boolean containsObservedObject(javax.management.ObjectName var0) { return false; }
	public long getGranularityPeriod() { return 0l; }
	public java.lang.String getObservedAttribute() { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public javax.management.ObjectName getObservedObject() { return null; }
	public javax.management.ObjectName[] getObservedObjects() { return null; }
	public boolean isActive() { return false; }
	public void postDeregister() { }
	public void postRegister(java.lang.Boolean var0) { }
	public void preDeregister() throws java.lang.Exception { }
	public javax.management.ObjectName preRegister(javax.management.MBeanServer var0, javax.management.ObjectName var1) throws java.lang.Exception { return null; }
	public void removeObservedObject(javax.management.ObjectName var0) { }
	public void setGranularityPeriod(long var0) { }
	public void setObservedAttribute(java.lang.String var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public void setObservedObject(javax.management.ObjectName var0) { }
}

