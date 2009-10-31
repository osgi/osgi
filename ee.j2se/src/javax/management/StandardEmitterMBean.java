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

package javax.management;
public class StandardEmitterMBean extends javax.management.StandardMBean implements javax.management.NotificationEmitter {
	protected StandardEmitterMBean(java.lang.Class<?> var0, javax.management.NotificationEmitter var1)  { super((java.lang.Class<?>) null, false); } 
	protected StandardEmitterMBean(java.lang.Class<?> var0, boolean var1, javax.management.NotificationEmitter var2)  { super((java.lang.Class<?>) null, false); } 
	public <T> StandardEmitterMBean(T var0, java.lang.Class<T> var1, javax.management.NotificationEmitter var2)  { super((java.lang.Class<?>) null, false); } 
	public <T> StandardEmitterMBean(T var0, java.lang.Class<T> var1, boolean var2, javax.management.NotificationEmitter var3)  { super((java.lang.Class<?>) null, false); } 
	public void addNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) { }
	public javax.management.MBeanNotificationInfo[] getNotificationInfo() { return null; }
	public void removeNotificationListener(javax.management.NotificationListener var0) throws javax.management.ListenerNotFoundException { }
	public void removeNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) throws javax.management.ListenerNotFoundException { }
	public void sendNotification(javax.management.Notification var0) { }
}

