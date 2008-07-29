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

package java.awt.dnd;
public class DragGestureEvent extends java.util.EventObject {
	public DragGestureEvent(java.awt.dnd.DragGestureRecognizer var0, int var1, java.awt.Point var2, java.util.List var3) { super((java.lang.Object) null); }
	public java.awt.Component getComponent() { return null; }
	public int getDragAction() { return 0; }
	public java.awt.Point getDragOrigin() { return null; }
	public java.awt.dnd.DragSource getDragSource() { return null; }
	public java.awt.dnd.DragGestureRecognizer getSourceAsDragGestureRecognizer() { return null; }
	public java.awt.event.InputEvent getTriggerEvent() { return null; }
	public java.util.Iterator iterator() { return null; }
	public void startDrag(java.awt.Cursor var0, java.awt.Image var1, java.awt.Point var2, java.awt.datatransfer.Transferable var3, java.awt.dnd.DragSourceListener var4) { }
	public void startDrag(java.awt.Cursor var0, java.awt.datatransfer.Transferable var1) { }
	public void startDrag(java.awt.Cursor var0, java.awt.datatransfer.Transferable var1, java.awt.dnd.DragSourceListener var2) { }
	public java.lang.Object[] toArray() { return null; }
	public java.lang.Object[] toArray(java.lang.Object[] var0) { return null; }
}

