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
public class NotificationBroadcasterSupport implements javax.management.NotificationEmitter {
	public NotificationBroadcasterSupport() { } 
	public NotificationBroadcasterSupport(java.util.concurrent.Executor var0) { } 
	public NotificationBroadcasterSupport(java.util.concurrent.Executor var0, javax.management.MBeanNotificationInfo... var1) { } 
	public NotificationBroadcasterSupport(javax.management.MBeanNotificationInfo... var0) { } 
	public void addNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) { }
	public javax.management.MBeanNotificationInfo[] getNotificationInfo() { return null; }
	protected void handleNotification(javax.management.NotificationListener var0, javax.management.Notification var1, java.lang.Object var2) { }
	public void removeNotificationListener(javax.management.NotificationListener var0) throws javax.management.ListenerNotFoundException { }
	public void removeNotificationListener(javax.management.NotificationListener var0, javax.management.NotificationFilter var1, java.lang.Object var2) throws javax.management.ListenerNotFoundException { }
	public void sendNotification(javax.management.Notification var0) { }
}

