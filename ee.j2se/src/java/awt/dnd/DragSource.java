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

package java.awt.dnd;
public class DragSource implements java.io.Serializable {
	public final static java.awt.Cursor DefaultCopyDrop; static { DefaultCopyDrop = null; }
	public final static java.awt.Cursor DefaultCopyNoDrop; static { DefaultCopyNoDrop = null; }
	public final static java.awt.Cursor DefaultLinkDrop; static { DefaultLinkDrop = null; }
	public final static java.awt.Cursor DefaultLinkNoDrop; static { DefaultLinkNoDrop = null; }
	public final static java.awt.Cursor DefaultMoveDrop; static { DefaultMoveDrop = null; }
	public final static java.awt.Cursor DefaultMoveNoDrop; static { DefaultMoveNoDrop = null; }
	public DragSource() { } 
	public void addDragSourceListener(java.awt.dnd.DragSourceListener var0) { }
	public void addDragSourceMotionListener(java.awt.dnd.DragSourceMotionListener var0) { }
	public java.awt.dnd.DragGestureRecognizer createDefaultDragGestureRecognizer(java.awt.Component var0, int var1, java.awt.dnd.DragGestureListener var2) { return null; }
	public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(java.lang.Class<T> var0, java.awt.Component var1, int var2, java.awt.dnd.DragGestureListener var3) { return null; }
	protected java.awt.dnd.DragSourceContext createDragSourceContext(java.awt.dnd.peer.DragSourceContextPeer var0, java.awt.dnd.DragGestureEvent var1, java.awt.Cursor var2, java.awt.Image var3, java.awt.Point var4, java.awt.datatransfer.Transferable var5, java.awt.dnd.DragSourceListener var6) { return null; }
	public static java.awt.dnd.DragSource getDefaultDragSource() { return null; }
	public java.awt.dnd.DragSourceListener[] getDragSourceListeners() { return null; }
	public java.awt.dnd.DragSourceMotionListener[] getDragSourceMotionListeners() { return null; }
	public static int getDragThreshold() { return 0; }
	public java.awt.datatransfer.FlavorMap getFlavorMap() { return null; }
	public <T extends java.util.EventListener> T[] getListeners(java.lang.Class<T> var0) { return null; }
	public static boolean isDragImageSupported() { return false; }
	public void removeDragSourceListener(java.awt.dnd.DragSourceListener var0) { }
	public void removeDragSourceMotionListener(java.awt.dnd.DragSourceMotionListener var0) { }
	public void startDrag(java.awt.dnd.DragGestureEvent var0, java.awt.Cursor var1, java.awt.Image var2, java.awt.Point var3, java.awt.datatransfer.Transferable var4, java.awt.dnd.DragSourceListener var5) { }
	public void startDrag(java.awt.dnd.DragGestureEvent var0, java.awt.Cursor var1, java.awt.Image var2, java.awt.Point var3, java.awt.datatransfer.Transferable var4, java.awt.dnd.DragSourceListener var5, java.awt.datatransfer.FlavorMap var6) { }
	public void startDrag(java.awt.dnd.DragGestureEvent var0, java.awt.Cursor var1, java.awt.datatransfer.Transferable var2, java.awt.dnd.DragSourceListener var3) { }
	public void startDrag(java.awt.dnd.DragGestureEvent var0, java.awt.Cursor var1, java.awt.datatransfer.Transferable var2, java.awt.dnd.DragSourceListener var3, java.awt.datatransfer.FlavorMap var4) { }
}

