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
public class UIDefaults extends java.util.Hashtable<java.lang.Object,java.lang.Object> {
	public interface ActiveValue {
		java.lang.Object createValue(javax.swing.UIDefaults var0);
	}
	public static class LazyInputMap implements javax.swing.UIDefaults.LazyValue {
		public LazyInputMap(java.lang.Object[] var0) { } 
		public java.lang.Object createValue(javax.swing.UIDefaults var0) { return null; }
	}
	public interface LazyValue {
		java.lang.Object createValue(javax.swing.UIDefaults var0);
	}
	public static class ProxyLazyValue implements javax.swing.UIDefaults.LazyValue {
		public ProxyLazyValue(java.lang.String var0) { } 
		public ProxyLazyValue(java.lang.String var0, java.lang.String var1) { } 
		public ProxyLazyValue(java.lang.String var0, java.lang.String var1, java.lang.Object[] var2) { } 
		public ProxyLazyValue(java.lang.String var0, java.lang.Object[] var1) { } 
		public java.lang.Object createValue(javax.swing.UIDefaults var0) { return null; }
	}
	public UIDefaults() { } 
	public UIDefaults(java.lang.Object[] var0) { } 
	public void addPropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void addResourceBundle(java.lang.String var0) { }
	protected void firePropertyChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) { }
	public java.lang.Object get(java.lang.Object var0) { return null; }
	public java.lang.Object get(java.lang.Object var0, java.util.Locale var1) { return null; }
	public boolean getBoolean(java.lang.Object var0) { return false; }
	public boolean getBoolean(java.lang.Object var0, java.util.Locale var1) { return false; }
	public javax.swing.border.Border getBorder(java.lang.Object var0) { return null; }
	public javax.swing.border.Border getBorder(java.lang.Object var0, java.util.Locale var1) { return null; }
	public java.awt.Color getColor(java.lang.Object var0) { return null; }
	public java.awt.Color getColor(java.lang.Object var0, java.util.Locale var1) { return null; }
	public java.util.Locale getDefaultLocale() { return null; }
	public java.awt.Dimension getDimension(java.lang.Object var0) { return null; }
	public java.awt.Dimension getDimension(java.lang.Object var0, java.util.Locale var1) { return null; }
	public java.awt.Font getFont(java.lang.Object var0) { return null; }
	public java.awt.Font getFont(java.lang.Object var0, java.util.Locale var1) { return null; }
	public javax.swing.Icon getIcon(java.lang.Object var0) { return null; }
	public javax.swing.Icon getIcon(java.lang.Object var0, java.util.Locale var1) { return null; }
	public java.awt.Insets getInsets(java.lang.Object var0) { return null; }
	public java.awt.Insets getInsets(java.lang.Object var0, java.util.Locale var1) { return null; }
	public int getInt(java.lang.Object var0) { return 0; }
	public int getInt(java.lang.Object var0, java.util.Locale var1) { return 0; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public java.lang.String getString(java.lang.Object var0) { return null; }
	public java.lang.String getString(java.lang.Object var0, java.util.Locale var1) { return null; }
	public javax.swing.plaf.ComponentUI getUI(javax.swing.JComponent var0) { return null; }
	public java.lang.Class<? extends javax.swing.plaf.ComponentUI> getUIClass(java.lang.String var0) { return null; }
	public java.lang.Class<? extends javax.swing.plaf.ComponentUI> getUIClass(java.lang.String var0, java.lang.ClassLoader var1) { return null; }
	protected void getUIError(java.lang.String var0) { }
	public java.lang.Object put(java.lang.Object var0, java.lang.Object var1) { return null; }
	public void putDefaults(java.lang.Object[] var0) { }
	public void removePropertyChangeListener(java.beans.PropertyChangeListener var0) { }
	public void removeResourceBundle(java.lang.String var0) { }
	public void setDefaultLocale(java.util.Locale var0) { }
}

