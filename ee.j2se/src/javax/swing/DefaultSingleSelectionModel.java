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

package javax.swing;
public class DefaultSingleSelectionModel implements java.io.Serializable, javax.swing.SingleSelectionModel {
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.event.EventListenerList listenerList;
	public DefaultSingleSelectionModel() { } 
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	public void clearSelection() { }
	protected void fireStateChanged() { }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> var0) { return null; }
	public int getSelectedIndex() { return 0; }
	public boolean isSelected() { return false; }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void setSelectedIndex(int var0) { }
}

