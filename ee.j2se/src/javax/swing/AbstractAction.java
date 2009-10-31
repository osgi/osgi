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
public abstract class AbstractAction implements java.io.Serializable, java.lang.Cloneable, javax.swing.Action {
	protected javax.swing.event.SwingPropertyChangeSupport changeSupport;
	protected boolean enabled;
	public AbstractAction() { } 
	public AbstractAction(java.lang.String var0) { } 
	public AbstractAction(java.lang.String var0, javax.swing.Icon var1) { } 
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	protected void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	public java.lang.Object[] getKeys() { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public java.lang.Object getValue(java.lang.String var0) { return null; }
	public boolean isEnabled() { return false; }
	public void putValue(java.lang.String var0, java.lang.Object var1) { }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void setEnabled(boolean var0) { }
}

