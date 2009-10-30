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

package java.beans;
public class PropertyChangeSupport implements java.io.Serializable {
	public PropertyChangeSupport(java.lang.Object var0) { } 
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void addPropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public void fireIndexedPropertyChange(java.lang.String var0, int var1, int var2, int var3) { }
	public void fireIndexedPropertyChange(java.lang.String var0, int var1, java.lang.Object var2, java.lang.Object var3) { }
	public void fireIndexedPropertyChange(java.lang.String var0, int var1, boolean var2, boolean var3) { }
	public void firePropertyChange(java.beans.PropertyChangeEvent var0) { }
	public void firePropertyChange(java.lang.String var0, int var1, int var2) { }
	public void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	public void firePropertyChange(java.lang.String var0, boolean var1, boolean var2) { }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String var0) { return null; }
	public boolean hasListeners(java.lang.String var0) { return false; }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void removePropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
}

