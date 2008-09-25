/*
 * $Revision$
 *
 * Copyright (c) OSGi Alliance (2001, 2008). All Rights Reserved.
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

package javax.swing;
public class Timer implements java.io.Serializable {
	public Timer(int var0, java.awt.event.ActionListener var1) { }
	public void addActionListener(java.awt.event.ActionListener var0) { }
	protected void fireActionPerformed(java.awt.event.ActionEvent var0) { }
	public java.awt.event.ActionListener[] getActionListeners() { return null; }
	public int getDelay() { return 0; }
	public int getInitialDelay() { return 0; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public static boolean getLogTimers() { return false; }
	public boolean isCoalesce() { return false; }
	public boolean isRepeats() { return false; }
	public boolean isRunning() { return false; }
	public void removeActionListener(java.awt.event.ActionListener var0) { }
	public void restart() { }
	public void setCoalesce(boolean var0) { }
	public void setDelay(int var0) { }
	public void setInitialDelay(int var0) { }
	public static void setLogTimers(boolean var0) { }
	public void setRepeats(boolean var0) { }
	public void start() { }
	public void stop() { }
	protected javax.swing.event.EventListenerList listenerList;
}

