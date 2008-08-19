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

package java.beans;
public class PropertyEditorSupport implements java.beans.PropertyEditor {
	protected PropertyEditorSupport() { }
	protected PropertyEditorSupport(java.lang.Object var0) { }
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void firePropertyChange() { }
	public java.lang.String getAsText() { return null; }
	public java.awt.Component getCustomEditor() { return null; }
	public java.lang.String getJavaInitializationString() { return null; }
	public java.lang.String[] getTags() { return null; }
	public java.lang.Object getValue() { return null; }
	public boolean isPaintable() { return false; }
	public void paintValue(java.awt.Graphics var0, java.awt.Rectangle var1) { }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void setAsText(java.lang.String var0) { }
	public void setValue(java.lang.Object var0) { }
	public boolean supportsCustomEditor() { return false; }
}

