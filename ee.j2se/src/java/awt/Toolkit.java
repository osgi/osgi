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

package java.awt;
public abstract class Toolkit {
	protected final java.util.Map<java.lang.String,java.lang.Object> desktopProperties; { desktopProperties = null; }
	protected final java.beans.PropertyChangeSupport desktopPropsSupport; { desktopPropsSupport = null; }
	public Toolkit() { } 
	public void addAWTEventListener(java.awt.event.AWTEventListener var0, long var1) { }
	public void addPropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	public abstract void beep();
	public abstract int checkImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3);
	protected abstract java.awt.peer.ButtonPeer createButton(java.awt.Button var0);
	protected abstract java.awt.peer.CanvasPeer createCanvas(java.awt.Canvas var0);
	protected abstract java.awt.peer.CheckboxPeer createCheckbox(java.awt.Checkbox var0);
	protected abstract java.awt.peer.CheckboxMenuItemPeer createCheckboxMenuItem(java.awt.CheckboxMenuItem var0);
	protected abstract java.awt.peer.ChoicePeer createChoice(java.awt.Choice var0);
	protected java.awt.peer.LightweightPeer createComponent(java.awt.Component var0) { return null; }
	public java.awt.Cursor createCustomCursor(java.awt.Image var0, java.awt.Point var1, java.lang.String var2) { return null; }
	protected abstract java.awt.peer.DesktopPeer createDesktopPeer(java.awt.Desktop var0);
	protected abstract java.awt.peer.DialogPeer createDialog(java.awt.Dialog var0);
	public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(java.lang.Class<T> var0, java.awt.dnd.DragSource var1, java.awt.Component var2, int var3, java.awt.dnd.DragGestureListener var4) { return null; }
	public abstract java.awt.dnd.peer.DragSourceContextPeer createDragSourceContextPeer(java.awt.dnd.DragGestureEvent var0);
	protected abstract java.awt.peer.FileDialogPeer createFileDialog(java.awt.FileDialog var0);
	protected abstract java.awt.peer.FramePeer createFrame(java.awt.Frame var0);
	public abstract java.awt.Image createImage(java.awt.image.ImageProducer var0);
	public abstract java.awt.Image createImage(java.lang.String var0);
	public abstract java.awt.Image createImage(java.net.URL var0);
	public java.awt.Image createImage(byte[] var0) { return null; }
	public abstract java.awt.Image createImage(byte[] var0, int var1, int var2);
	protected abstract java.awt.peer.LabelPeer createLabel(java.awt.Label var0);
	protected abstract java.awt.peer.ListPeer createList(java.awt.List var0);
	protected abstract java.awt.peer.MenuPeer createMenu(java.awt.Menu var0);
	protected abstract java.awt.peer.MenuBarPeer createMenuBar(java.awt.MenuBar var0);
	protected abstract java.awt.peer.MenuItemPeer createMenuItem(java.awt.MenuItem var0);
	protected abstract java.awt.peer.PanelPeer createPanel(java.awt.Panel var0);
	protected abstract java.awt.peer.PopupMenuPeer createPopupMenu(java.awt.PopupMenu var0);
	protected abstract java.awt.peer.ScrollPanePeer createScrollPane(java.awt.ScrollPane var0);
	protected abstract java.awt.peer.ScrollbarPeer createScrollbar(java.awt.Scrollbar var0);
	protected abstract java.awt.peer.TextAreaPeer createTextArea(java.awt.TextArea var0);
	protected abstract java.awt.peer.TextFieldPeer createTextField(java.awt.TextField var0);
	protected abstract java.awt.peer.WindowPeer createWindow(java.awt.Window var0);
	public java.awt.event.AWTEventListener[] getAWTEventListeners() { return null; }
	public java.awt.event.AWTEventListener[] getAWTEventListeners(long var0) { return null; }
	public java.awt.Dimension getBestCursorSize(int var0, int var1) { return null; }
	public abstract java.awt.image.ColorModel getColorModel();
	public static java.awt.Toolkit getDefaultToolkit() { return null; }
	public final java.lang.Object getDesktopProperty(java.lang.String var0) { return null; }
	/** @deprecated */
	@java.lang.Deprecated
	public abstract java.lang.String[] getFontList();
	/** @deprecated */
	@java.lang.Deprecated
	public abstract java.awt.FontMetrics getFontMetrics(java.awt.Font var0);
	/** @deprecated */
	@java.lang.Deprecated
	protected abstract java.awt.peer.FontPeer getFontPeer(java.lang.String var0, int var1);
	public abstract java.awt.Image getImage(java.lang.String var0);
	public abstract java.awt.Image getImage(java.net.URL var0);
	public boolean getLockingKeyState(int var0) { return false; }
	public int getMaximumCursorColors() { return 0; }
	public int getMenuShortcutKeyMask() { return 0; }
	protected java.awt.peer.MouseInfoPeer getMouseInfoPeer() { return null; }
	protected static java.awt.Container getNativeContainer(java.awt.Component var0) { return null; }
	public java.awt.PrintJob getPrintJob(java.awt.Frame var0, java.lang.String var1, java.awt.JobAttributes var2, java.awt.PageAttributes var3) { return null; }
	public abstract java.awt.PrintJob getPrintJob(java.awt.Frame var0, java.lang.String var1, java.util.Properties var2);
	public static java.lang.String getProperty(java.lang.String var0, java.lang.String var1) { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners() { return null; }
	public java.beans.PropertyChangeListener[] getPropertyChangeListeners(java.lang.String var0) { return null; }
	public java.awt.Insets getScreenInsets(java.awt.GraphicsConfiguration var0) { return null; }
	public abstract int getScreenResolution();
	public abstract java.awt.Dimension getScreenSize();
	public abstract java.awt.datatransfer.Clipboard getSystemClipboard();
	public final java.awt.EventQueue getSystemEventQueue() { return null; }
	protected abstract java.awt.EventQueue getSystemEventQueueImpl();
	public java.awt.datatransfer.Clipboard getSystemSelection() { return null; }
	protected void initializeDesktopProperties() { }
	public boolean isAlwaysOnTopSupported() { return false; }
	public boolean isDynamicLayoutActive() { return false; }
	protected boolean isDynamicLayoutSet() { return false; }
	public boolean isFrameStateSupported(int var0) { return false; }
	public abstract boolean isModalExclusionTypeSupported(java.awt.Dialog.ModalExclusionType var0);
	public abstract boolean isModalityTypeSupported(java.awt.Dialog.ModalityType var0);
	protected java.lang.Object lazilyLoadDesktopProperty(java.lang.String var0) { return null; }
	protected void loadSystemColors(int[] var0) { }
	public abstract java.util.Map<java.awt.font.TextAttribute,?> mapInputMethodHighlight(java.awt.im.InputMethodHighlight var0);
	public abstract boolean prepareImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3);
	public void removeAWTEventListener(java.awt.event.AWTEventListener var0) { }
	public void removePropertyChangeListener(java.lang.String var0, java.beans.PropertyChangeListener var1) { }
	protected final void setDesktopProperty(java.lang.String var0, java.lang.Object var1) { }
	public void setDynamicLayout(boolean var0) { }
	public void setLockingKeyState(int var0, boolean var1) { }
	public abstract void sync();
}

