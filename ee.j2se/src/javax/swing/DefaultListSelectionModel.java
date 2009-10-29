/*
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
public class DefaultListSelectionModel implements java.io.Serializable, java.lang.Cloneable, javax.swing.ListSelectionModel {
	public DefaultListSelectionModel() { }
	public void addListSelectionListener(javax.swing.event.ListSelectionListener var0) { }
	public void addSelectionInterval(int var0, int var1) { }
	public void clearSelection() { }
	public java.lang.Object clone() throws java.lang.CloneNotSupportedException { return null; }
	protected void fireValueChanged(int var0, int var1) { }
	protected void fireValueChanged(int var0, int var1, boolean var2) { }
	protected void fireValueChanged(boolean var0) { }
	public int getAnchorSelectionIndex() { return 0; }
	public int getLeadSelectionIndex() { return 0; }
	public javax.swing.event.ListSelectionListener[] getListSelectionListeners() { return null; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public int getMaxSelectionIndex() { return 0; }
	public int getMinSelectionIndex() { return 0; }
	public int getSelectionMode() { return 0; }
	public boolean getValueIsAdjusting() { return false; }
	public void insertIndexInterval(int var0, int var1, boolean var2) { }
	public boolean isLeadAnchorNotificationEnabled() { return false; }
	public boolean isSelectedIndex(int var0) { return false; }
	public boolean isSelectionEmpty() { return false; }
	public void removeIndexInterval(int var0, int var1) { }
	public void removeListSelectionListener(javax.swing.event.ListSelectionListener var0) { }
	public void removeSelectionInterval(int var0, int var1) { }
	public void setAnchorSelectionIndex(int var0) { }
	public void setLeadAnchorNotificationEnabled(boolean var0) { }
	public void setLeadSelectionIndex(int var0) { }
	public void setSelectionInterval(int var0, int var1) { }
	public void setSelectionMode(int var0) { }
	public void setValueIsAdjusting(boolean var0) { }
	protected boolean leadAnchorNotificationEnabled;
	protected javax.swing.event.EventListenerList listenerList;
}

