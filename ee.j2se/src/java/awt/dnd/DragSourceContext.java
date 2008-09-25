/*
 * $Revision$
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

package java.awt.dnd;
public class DragSourceContext implements java.awt.dnd.DragSourceListener, java.awt.dnd.DragSourceMotionListener, java.io.Serializable {
	public DragSourceContext(java.awt.dnd.peer.DragSourceContextPeer var0, java.awt.dnd.DragGestureEvent var1, java.awt.Cursor var2, java.awt.Image var3, java.awt.Point var4, java.awt.datatransfer.Transferable var5, java.awt.dnd.DragSourceListener var6) { }
	public void addDragSourceListener(java.awt.dnd.DragSourceListener var0) throws java.util.TooManyListenersException { }
	public void dragDropEnd(java.awt.dnd.DragSourceDropEvent var0) { }
	public void dragEnter(java.awt.dnd.DragSourceDragEvent var0) { }
	public void dragExit(java.awt.dnd.DragSourceEvent var0) { }
	public void dragMouseMoved(java.awt.dnd.DragSourceDragEvent var0) { }
	public void dragOver(java.awt.dnd.DragSourceDragEvent var0) { }
	public void dropActionChanged(java.awt.dnd.DragSourceDragEvent var0) { }
	public java.awt.Component getComponent() { return null; }
	public java.awt.Cursor getCursor() { return null; }
	public java.awt.dnd.DragSource getDragSource() { return null; }
	public int getSourceActions() { return 0; }
	public java.awt.datatransfer.Transferable getTransferable() { return null; }
	public java.awt.dnd.DragGestureEvent getTrigger() { return null; }
	public void removeDragSourceListener(java.awt.dnd.DragSourceListener var0) { }
	public void setCursor(java.awt.Cursor var0) { }
	public void transferablesFlavorsChanged() { }
	protected void updateCurrentCursor(int var0, int var1, int var2) { }
	protected final static int CHANGED = 3;
	protected final static int DEFAULT = 0;
	protected final static int ENTER = 1;
	protected final static int OVER = 2;
}

