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
public abstract class DragGestureRecognizer implements java.io.Serializable {
	protected DragGestureRecognizer(java.awt.dnd.DragSource var0) { }
	protected DragGestureRecognizer(java.awt.dnd.DragSource var0, java.awt.Component var1) { }
	protected DragGestureRecognizer(java.awt.dnd.DragSource var0, java.awt.Component var1, int var2) { }
	protected DragGestureRecognizer(java.awt.dnd.DragSource var0, java.awt.Component var1, int var2, java.awt.dnd.DragGestureListener var3) { }
	public void addDragGestureListener(java.awt.dnd.DragGestureListener var0) throws java.util.TooManyListenersException { }
	protected void appendEvent(java.awt.event.InputEvent var0) { }
	protected void fireDragGestureRecognized(int var0, java.awt.Point var1) { }
	public java.awt.Component getComponent() { return null; }
	public java.awt.dnd.DragSource getDragSource() { return null; }
	public int getSourceActions() { return 0; }
	public java.awt.event.InputEvent getTriggerEvent() { return null; }
	protected abstract void registerListeners();
	public void removeDragGestureListener(java.awt.dnd.DragGestureListener var0) { }
	public void resetRecognizer() { }
	public void setComponent(java.awt.Component var0) { }
	public void setSourceActions(int var0) { }
	protected abstract void unregisterListeners();
	protected java.awt.Component component;
	protected java.awt.dnd.DragGestureListener dragGestureListener;
	protected java.awt.dnd.DragSource dragSource;
	protected java.util.ArrayList events;
	protected int sourceActions;
}

