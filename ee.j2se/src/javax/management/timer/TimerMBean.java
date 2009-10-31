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
public interface TimerMBean {
	java.lang.Integer addNotification(java.lang.String var0, java.lang.String var1, java.lang.Object var2, java.util.Date var3);
	java.lang.Integer addNotification(java.lang.String var0, java.lang.String var1, java.lang.Object var2, java.util.Date var3, long var4);
	java.lang.Integer addNotification(java.lang.String var0, java.lang.String var1, java.lang.Object var2, java.util.Date var3, long var4, long var5);
	java.lang.Integer addNotification(java.lang.String var0, java.lang.String var1, java.lang.Object var2, java.util.Date var3, long var4, long var5, boolean var6);
	java.util.Vector<java.lang.Integer> getAllNotificationIDs();
	java.util.Date getDate(java.lang.Integer var0);
	java.lang.Boolean getFixedRate(java.lang.Integer var0);
	int getNbNotifications();
	java.lang.Long getNbOccurences(java.lang.Integer var0);
	java.util.Vector<java.lang.Integer> getNotificationIDs(java.lang.String var0);
	java.lang.String getNotificationMessage(java.lang.Integer var0);
	java.lang.String getNotificationType(java.lang.Integer var0);
	java.lang.Object getNotificationUserData(java.lang.Integer var0);
	java.lang.Long getPeriod(java.lang.Integer var0);
	boolean getSendPastNotifications();
	boolean isActive();
	boolean isEmpty();
	void removeAllNotifications();
	void removeNotification(java.lang.Integer var0) throws javax.management.InstanceNotFoundException;
	void removeNotifications(java.lang.String var0) throws javax.management.InstanceNotFoundException;
	void setSendPastNotifications(boolean var0);
	void start();
	void stop();
}

