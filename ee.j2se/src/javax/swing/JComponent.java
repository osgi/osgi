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
public abstract class JComponent extends java.awt.Container implements java.io.Serializable, javax.swing.TransferHandler.HasGetTransferHandler {
	public abstract class AccessibleJComponent extends java.awt.Container.AccessibleAWTContainer implements javax.accessibility.AccessibleExtendedComponent {
		protected class AccessibleContainerHandler implements java.awt.event.ContainerListener {
			protected AccessibleContainerHandler() { } 
			public void componentAdded(java.awt.event.ContainerEvent var0) { }
			public void componentRemoved(java.awt.event.ContainerEvent var0) { }
		}
		protected class AccessibleFocusHandler implements java.awt.event.FocusListener {
			protected AccessibleFocusHandler() { } 
			public void focusGained(java.awt.event.FocusEvent var0) { }
			public void focusLost(java.awt.event.FocusEvent var0) { }
		}
		protected java.awt.event.ContainerListener accessibleContainerHandler;
		protected java.awt.event.FocusListener accessibleFocusHandler;
		protected AccessibleJComponent() { } 
		public javax.accessibility.AccessibleKeyBinding getAccessibleKeyBinding() { return null; }
		protected java.lang.String getBorderTitle(javax.swing.border.Border var0) { return null; }
		public java.lang.String getTitledBorderText() { return null; }
		public java.lang.String getToolTipText() { return null; }
	}
	public final static java.lang.String TOOL_TIP_TEXT_KEY = "ToolTipText";
	public final static int UNDEFINED_CONDITION = -1;
	public final static int WHEN_ANCESTOR_OF_FOCUSED_COMPONENT = 1;
	public final static int WHEN_FOCUSED = 0;
	public final static int WHEN_IN_FOCUSED_WINDOW = 2;
	protected javax.accessibility.AccessibleContext accessibleContext;
	protected javax.swing.event.EventListenerList listenerList;
	protected javax.swing.plaf.ComponentUI ui;
	public JComponent() { } 
	public void addAncestorListener(javax.swing.event.AncestorListener var0) { }
	public void addVetoableChangeListener(java.beans.VetoableChangeListener var0) { }
	public void computeVisibleRect(java.awt.Rectangle var0) { }
	public javax.swing.JToolTip createToolTip() { return null; }
	public void firePropertyChange(java.lang.String var0, int var1, int var2) { }
	public void firePropertyChange(java.lang.String var0, boolean var1, boolean var2) { }
	protected void fireVetoableChange(java.lang.String var0, java.lang.Object var1, java.lang.Object var2) throws java.beans.PropertyVetoException { }
	public java.awt.event.ActionListener getActionForKeyStroke(javax.swing.KeyStroke var0) { return null; }
	public final javax.swing.ActionMap getActionMap() { return null; }
	public javax.swing.event.AncestorListener[] getAncestorListeners() { return null; }
	public boolean getAutoscrolls() { return false; }
	public javax.swing.border.Border getBorder() { return null; }
	public final java.lang.Object getClientProperty(java.lang.Object var0) { return null; }
	protected java.awt.Graphics getComponentGraphics(java.awt.Graphics var0) { return null; }
	public javax.swing.JPopupMenu getComponentPopupMenu() { return null; }
	public int getConditionForKeyStroke(javax.swing.KeyStroke var0) { return 0; }
	public int getDebugGraphicsOptions() { return 0; }
	public static java.util.Locale getDefaultLocale() { return null; }
	public boolean getInheritsPopupMenu() { return false; }
	public final javax.swing.InputMap getInputMap() { return null; }
	public final javax.swing.InputMap getInputMap(int var0) { return null; }
	public javax.swing.InputVerifier getInputVerifier() { return null; }
	public java.awt.Insets getInsets(java.awt.Insets var0) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public java.awt.Component getNextFocusableComponent() { return null; }
	public java.awt.Point getPopupLocation(java.awt.event.MouseEvent var0) { return null; }
	public javax.swing.KeyStroke[] getRegisteredKeyStrokes() { return null; }
	public javax.swing.JRootPane getRootPane() { return null; }
	public java.awt.Point getToolTipLocation(java.awt.event.MouseEvent var0) { return null; }
	public java.lang.String getToolTipText() { return null; }
	public java.lang.String getToolTipText(java.awt.event.MouseEvent var0) { return null; }
	public java.awt.Container getTopLevelAncestor() { return null; }
	public javax.swing.TransferHandler getTransferHandler() { return null; }
	public java.lang.String getUIClassID() { return null; }
	public boolean getVerifyInputWhenFocusTarget() { return false; }
	public java.beans.VetoableChangeListener[] getVetoableChangeListeners() { return null; }
	public java.awt.Rectangle getVisibleRect() { return null; }
	public void grabFocus() { }
	public static boolean isLightweightComponent(java.awt.Component var0) { return false; }
	/** @deprecated */
	@java.lang.Deprecated
	public boolean isManagingFocus() { return false; }
	public boolean isOptimizedDrawingEnabled() { return false; }
	public final boolean isPaintingForPrint() { return false; }
	public boolean isPaintingTile() { return false; }
	public boolean isRequestFocusEnabled() { return false; }
	public boolean isValidateRoot() { return false; }
	protected void paintBorder(java.awt.Graphics var0) { }
	protected void paintChildren(java.awt.Graphics var0) { }
	protected void paintComponent(java.awt.Graphics var0) { }
	public void paintImmediately(int var0, int var1, int var2, int var3) { }
	public void paintImmediately(java.awt.Rectangle var0) { }
	protected void printBorder(java.awt.Graphics var0) { }
	protected void printChildren(java.awt.Graphics var0) { }
	protected void printComponent(java.awt.Graphics var0) { }
	protected void processComponentKeyEvent(java.awt.event.KeyEvent var0) { }
	protected boolean processKeyBinding(javax.swing.KeyStroke var0, java.awt.event.KeyEvent var1, int var2, boolean var3) { return false; }
	public final void putClientProperty(java.lang.Object var0, java.lang.Object var1) { }
	public void registerKeyboardAction(java.awt.event.ActionListener var0, java.lang.String var1, javax.swing.KeyStroke var2, int var3) { }
	public void registerKeyboardAction(java.awt.event.ActionListener var0, javax.swing.KeyStroke var1, int var2) { }
	public void removeAncestorListener(javax.swing.event.AncestorListener var0) { }
	public void removeVetoableChangeListener(java.beans.VetoableChangeListener var0) { }
	public void repaint(java.awt.Rectangle var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public boolean requestDefaultFocus() { return false; }
	public boolean requestFocus(boolean var0) { return false; }
	public void resetKeyboardActions() { }
	public void revalidate() { }
	public void scrollRectToVisible(java.awt.Rectangle var0) { }
	public final void setActionMap(javax.swing.ActionMap var0) { }
	public void setAlignmentX(float var0) { }
	public void setAlignmentY(float var0) { }
	public void setAutoscrolls(boolean var0) { }
	public void setBorder(javax.swing.border.Border var0) { }
	public void setComponentPopupMenu(javax.swing.JPopupMenu var0) { }
	public void setDebugGraphicsOptions(int var0) { }
	public static void setDefaultLocale(java.util.Locale var0) { }
	public void setDoubleBuffered(boolean var0) { }
	public void setInheritsPopupMenu(boolean var0) { }
	public final void setInputMap(int var0, javax.swing.InputMap var1) { }
	public void setInputVerifier(javax.swing.InputVerifier var0) { }
	/** @deprecated */
	@java.lang.Deprecated
	public void setNextFocusableComponent(java.awt.Component var0) { }
	public void setOpaque(boolean var0) { }
	public void setRequestFocusEnabled(boolean var0) { }
	public void setToolTipText(java.lang.String var0) { }
	public void setTransferHandler(javax.swing.TransferHandler var0) { }
	protected void setUI(javax.swing.plaf.ComponentUI var0) { }
	public void setVerifyInputWhenFocusTarget(boolean var0) { }
	public void unregisterKeyboardAction(javax.swing.KeyStroke var0) { }
	public void updateUI() { }
}

