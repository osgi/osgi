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

package javax.swing.plaf.metal;
public class MetalComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
	public class MetalComboBoxLayoutManager extends javax.swing.plaf.basic.BasicComboBoxUI.ComboBoxLayoutManager {
		public MetalComboBoxLayoutManager() { } 
		public void superLayout(java.awt.Container var0) { }
	}
	/** @deprecated */
	@java.lang.Deprecated
	public class MetalComboPopup extends javax.swing.plaf.basic.BasicComboPopup {
		public MetalComboPopup(javax.swing.JComboBox var0)  { super((javax.swing.JComboBox) null); } 
		public void delegateFocus(java.awt.event.MouseEvent var0) { }
	}
	public class MetalPropertyChangeListener extends javax.swing.plaf.basic.BasicComboBoxUI.PropertyChangeHandler {
		public MetalPropertyChangeListener() { } 
	}
	public MetalComboBoxUI() { } 
	public void configureEditor() { }
	public java.beans.PropertyChangeListener createPropertyChangeListener() { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	protected void editablePropertyChanged(java.beans.PropertyChangeEvent var0) { }
	public void layoutComboBox(java.awt.Container var0, javax.swing.plaf.metal.MetalComboBoxUI.MetalComboBoxLayoutManager var1) { }
	/** @deprecated */
	@java.lang.Deprecated
	protected void removeListeners() { }
	public void unconfigureEditor() { }
}

