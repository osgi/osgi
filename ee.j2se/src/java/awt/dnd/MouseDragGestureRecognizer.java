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
public abstract class MouseDragGestureRecognizer extends java.awt.dnd.DragGestureRecognizer implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
	protected MouseDragGestureRecognizer(java.awt.dnd.DragSource var0) { super((java.awt.dnd.DragSource) null, (java.awt.Component) null, 0, (java.awt.dnd.DragGestureListener) null); }
	protected MouseDragGestureRecognizer(java.awt.dnd.DragSource var0, java.awt.Component var1) { super((java.awt.dnd.DragSource) null, (java.awt.Component) null, 0, (java.awt.dnd.DragGestureListener) null); }
	protected MouseDragGestureRecognizer(java.awt.dnd.DragSource var0, java.awt.Component var1, int var2) { super((java.awt.dnd.DragSource) null, (java.awt.Component) null, 0, (java.awt.dnd.DragGestureListener) null); }
	protected MouseDragGestureRecognizer(java.awt.dnd.DragSource var0, java.awt.Component var1, int var2, java.awt.dnd.DragGestureListener var3) { super((java.awt.dnd.DragSource) null, (java.awt.Component) null, 0, (java.awt.dnd.DragGestureListener) null); }
	public void mouseClicked(java.awt.event.MouseEvent var0) { }
	public void mouseDragged(java.awt.event.MouseEvent var0) { }
	public void mouseEntered(java.awt.event.MouseEvent var0) { }
	public void mouseExited(java.awt.event.MouseEvent var0) { }
	public void mouseMoved(java.awt.event.MouseEvent var0) { }
	public void mousePressed(java.awt.event.MouseEvent var0) { }
	public void mouseReleased(java.awt.event.MouseEvent var0) { }
	protected void registerListeners() { }
	protected void unregisterListeners() { }
}

