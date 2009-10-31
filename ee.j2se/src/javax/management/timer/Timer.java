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

package javax.management.timer;
public class Timer extends javax.management.NotificationBroadcasterSupport implements javax.management.MBeanRegistration, javax.management.timer.TimerMBean {
	public final static long ONE_DAY = 86400000l;
	public final static long ONE_HOUR = 3600000l;
	public final static long ONE_MINUTE = 60000l;
	public final static long ONE_SECOND = 1000l;
	public final static long ONE_WEEK = 604800000l;
	public Timer() { } 
	public java.lang.Integer addNotification(java.lang.String var0, java.lang.String var1, java.lang.Object var2, java.util.Date var3) { return null; }
	public java.lang.Integer addNotification(java.lang.String var0, java.lang.String var1, java.lang.Object var2, java.util.Date var3, long var4) { return null; }
	public java.lang.Integer addNotification(java.lang.String var0, java.lang.String var1, java.lang.Object var2, java.util.Date var3, long var4, long var5) { return null; }
	public java.lang.Integer addNotification(java.lang.String var0, java.lang.String var1, java.lang.Object var2, java.util.Date var3, long var4, long var5, boolean var6) { return null; }
	public java.util.Vector<java.lang.Integer> getAllNotificationIDs() { return null; }
	public java.util.Date getDate(java.lang.Integer var0) { return null; }
	public java.lang.Boolean getFixedRate(java.lang.Integer var0) { return null; }
	public int getNbNotifications() { return 0; }
	public java.lang.Long getNbOccurences(java.lang.Integer var0) { return null; }
	public java.util.Vector<java.lang.Integer> getNotificationIDs(java.lang.String var0) { return null; }
	public java.lang.String getNotificationMessage(java.lang.Integer var0) { return null; }
	public java.lang.String getNotificationType(java.lang.Integer var0) { return null; }
	public java.lang.Object getNotificationUserData(java.lang.Integer var0) { return null; }
	public java.lang.Long getPeriod(java.lang.Integer var0) { return null; }
	public boolean getSendPastNotifications() { return false; }
	public boolean isActive() { return false; }
	public boolean isEmpty() { return false; }
	public void postDeregister() { }
	public void postRegister(java.lang.Boolean var0) { }
	public void preDeregister() throws java.lang.Exception { }
	public javax.management.ObjectName preRegister(javax.management.MBeanServer var0, javax.management.ObjectName var1) throws java.lang.Exception { return null; }
	public void removeAllNotifications() { }
	public void removeNotification(java.lang.Integer var0) throws javax.management.InstanceNotFoundException { }
	public void removeNotifications(java.lang.String var0) throws javax.management.InstanceNotFoundException { }
	public void setSendPastNotifications(boolean var0) { }
	public void start() { }
	public void stop() { }
}

