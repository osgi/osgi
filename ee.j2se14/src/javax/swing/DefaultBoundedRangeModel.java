/*
 * $Date$
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
public class DefaultBoundedRangeModel implements java.io.Serializable, javax.swing.BoundedRangeModel {
	public DefaultBoundedRangeModel() { }
	public DefaultBoundedRangeModel(int var0, int var1, int var2, int var3) { }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	protected void fireStateChanged() { }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public int getExtent() { return 0; }
	public java.util.EventListener[] getListeners(java.lang.Class var0) { return null; }
	public int getMaximum() { return 0; }
	public int getMinimum() { return 0; }
	public int getValue() { return 0; }
	public boolean getValueIsAdjusting() { return false; }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void setExtent(int var0) { }
	public void setMaximum(int var0) { }
	public void setMinimum(int var0) { }
	public void setRangeProperties(int var0, int var1, int var2, int var3, boolean var4) { }
	public void setValue(int var0) { }
	public void setValueIsAdjusting(boolean var0) { }
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.event.EventListenerList listenerList;
}

