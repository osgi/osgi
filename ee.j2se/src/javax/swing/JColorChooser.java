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
public class JColorChooser extends javax.swing.JComponent implements javax.accessibility.Accessible {
	public JColorChooser() { }
	public JColorChooser(java.awt.Color var0) { }
	public JColorChooser(javax.swing.colorchooser.ColorSelectionModel var0) { }
	public void addChooserPanel(javax.swing.colorchooser.AbstractColorChooserPanel var0) { }
	public static javax.swing.JDialog createDialog(java.awt.Component var0, java.lang.String var1, boolean var2, javax.swing.JColorChooser var3, java.awt.event.ActionListener var4, java.awt.event.ActionListener var5) { return null; }
	public javax.swing.colorchooser.AbstractColorChooserPanel[] getChooserPanels() { return null; }
	public java.awt.Color getColor() { return null; }
	public boolean getDragEnabled() { return false; }
	public javax.swing.JComponent getPreviewPanel() { return null; }
	public javax.swing.colorchooser.ColorSelectionModel getSelectionModel() { return null; }
	public javax.swing.plaf.ColorChooserUI getUI() { return null; }
	public javax.swing.colorchooser.AbstractColorChooserPanel removeChooserPanel(javax.swing.colorchooser.AbstractColorChooserPanel var0) { return null; }
	public void setChooserPanels(javax.swing.colorchooser.AbstractColorChooserPanel[] var0) { }
	public void setColor(int var0) { }
	public void setColor(int var0, int var1, int var2) { }
	public void setColor(java.awt.Color var0) { }
	public void setDragEnabled(boolean var0) { }
	public void setPreviewPanel(javax.swing.JComponent var0) { }
	public void setSelectionModel(javax.swing.colorchooser.ColorSelectionModel var0) { }
	public void setUI(javax.swing.plaf.ColorChooserUI var0) { }
	public static java.awt.Color showDialog(java.awt.Component var0, java.lang.String var1, java.awt.Color var2) { return null; }
	public final static java.lang.String CHOOSER_PANELS_PROPERTY = "chooserPanels";
	public final static java.lang.String PREVIEW_PANEL_PROPERTY = "previewPanel";
	public final static java.lang.String SELECTION_MODEL_PROPERTY = "selectionModel";
	protected javax.accessibility.AccessibleContext accessibleContext;
	protected class AccessibleJColorChooser extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJColorChooser() { }
	}
}

