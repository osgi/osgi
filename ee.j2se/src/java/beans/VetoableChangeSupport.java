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

package java.beans;
public class VetoableChangeSupport implements java.io.Serializable {
	public VetoableChangeSupport(java.lang.Object var0) { }
	public void addVetoableChangeListener(java.beans.VetoableChangeListener var0) { }
	public void addVetoableChangeListener(java.lang.String var0, java.beans.VetoableChangeListener var1) { }
	public void fireVetoableChange(java.beans.PropertyChangeEvent var0) throws java.beans.PropertyVetoException { }
	public void fireVetoableChange(java.lang.String var0, int var1, int var2) throws java.beans.PropertyVetoException { }
	public void fireVetoableChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) throws java.beans.PropertyVetoException { }
	public void fireVetoableChange(java.lang.String var0, boolean var1, boolean var2) throws java.beans.PropertyVetoException { }
	public java.beans.VetoableChangeListener[] getVetoableChangeListeners() { return null; }
	public java.beans.VetoableChangeListener[] getVetoableChangeListeners(java.lang.String var0) { return null; }
	public boolean hasListeners(java.lang.String var0) { return false; }
	public void removeVetoableChangeListener(java.beans.VetoableChangeListener var0) { }
	public void removeVetoableChangeListener(java.lang.String var0, java.beans.VetoableChangeListener var1) { }
}

