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
public class JDialog extends java.awt.Dialog implements javax.accessibility.Accessible, javax.swing.RootPaneContainer, javax.swing.TransferHandler.HasGetTransferHandler, javax.swing.WindowConstants {
	protected class AccessibleJDialog extends java.awt.Dialog.AccessibleAWTDialog {
		protected AccessibleJDialog() { } 
	}
	protected javax.accessibility.AccessibleContext accessibleContext;
	protected javax.swing.JRootPane rootPane;
	protected boolean rootPaneCheckingEnabled;
	public JDialog()  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Dialog var0)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Dialog var0, java.lang.String var1)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Dialog var0, java.lang.String var1, boolean var2)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Dialog var0, java.lang.String var1, boolean var2, java.awt.GraphicsConfiguration var3)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Dialog var0, boolean var1)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Frame var0)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Frame var0, java.lang.String var1)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Frame var0, java.lang.String var1, boolean var2)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Frame var0, java.lang.String var1, boolean var2, java.awt.GraphicsConfiguration var3)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Frame var0, boolean var1)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Window var0)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Window var0, java.awt.Dialog.ModalityType var1)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Window var0, java.lang.String var1)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Window var0, java.lang.String var1, java.awt.Dialog.ModalityType var2)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	public JDialog(java.awt.Window var0, java.lang.String var1, java.awt.Dialog.ModalityType var2, java.awt.GraphicsConfiguration var3)  { super((java.awt.Window) null, (java.lang.String) null, (java.awt.Dialog.ModalityType) null, (java.awt.GraphicsConfiguration) null); } 
	protected javax.swing.JRootPane createRootPane() { return null; }
	protected void dialogInit() { }
	public java.awt.Container getContentPane() { return null; }
	public int getDefaultCloseOperation() { return 0; }
	public java.awt.Component getGlassPane() { return null; }
	public javax.swing.JMenuBar getJMenuBar() { return null; }
	public javax.swing.JLayeredPane getLayeredPane() { return null; }
	public javax.swing.JRootPane getRootPane() { return null; }
	public javax.swing.TransferHandler getTransferHandler() { return null; }
	public static boolean isDefaultLookAndFeelDecorated() { return false; }
	protected boolean isRootPaneCheckingEnabled() { return false; }
	public void setContentPane(java.awt.Container var0) { }
	public void setDefaultCloseOperation(int var0) { }
	public static void setDefaultLookAndFeelDecorated(boolean var0) { }
	public void setGlassPane(java.awt.Component var0) { }
	public void setJMenuBar(javax.swing.JMenuBar var0) { }
	public void setLayeredPane(javax.swing.JLayeredPane var0) { }
	protected void setRootPane(javax.swing.JRootPane var0) { }
	protected void setRootPaneCheckingEnabled(boolean var0) { }
	public void setTransferHandler(javax.swing.TransferHandler var0) { }
}

