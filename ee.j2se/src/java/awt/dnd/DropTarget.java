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

package java.awt.dnd;
public class DropTarget implements java.awt.dnd.DropTargetListener, java.io.Serializable {
	public DropTarget() { }
	public DropTarget(java.awt.Component var0, int var1, java.awt.dnd.DropTargetListener var2) { }
	public DropTarget(java.awt.Component var0, int var1, java.awt.dnd.DropTargetListener var2, boolean var3) { }
	public DropTarget(java.awt.Component var0, int var1, java.awt.dnd.DropTargetListener var2, boolean var3, java.awt.datatransfer.FlavorMap var4) { }
	public DropTarget(java.awt.Component var0, java.awt.dnd.DropTargetListener var1) { }
	public void addDropTargetListener(java.awt.dnd.DropTargetListener var0) throws java.util.TooManyListenersException { }
	public void addNotify(java.awt.peer.ComponentPeer var0) { }
	protected void clearAutoscroll() { }
	protected java.awt.dnd.DropTarget.DropTargetAutoScroller createDropTargetAutoScroller(java.awt.Component var0, java.awt.Point var1) { return null; }
	protected java.awt.dnd.DropTargetContext createDropTargetContext() { return null; }
	public void dragEnter(java.awt.dnd.DropTargetDragEvent var0) { }
	public void dragExit(java.awt.dnd.DropTargetEvent var0) { }
	public void dragOver(java.awt.dnd.DropTargetDragEvent var0) { }
	public void drop(java.awt.dnd.DropTargetDropEvent var0) { }
	public void dropActionChanged(java.awt.dnd.DropTargetDragEvent var0) { }
	public java.awt.Component getComponent() { return null; }
	public int getDefaultActions() { return 0; }
	public java.awt.dnd.DropTargetContext getDropTargetContext() { return null; }
	public java.awt.datatransfer.FlavorMap getFlavorMap() { return null; }
	protected void initializeAutoscrolling(java.awt.Point var0) { }
	public boolean isActive() { return false; }
	public void removeDropTargetListener(java.awt.dnd.DropTargetListener var0) { }
	public void removeNotify(java.awt.peer.ComponentPeer var0) { }
	public void setActive(boolean var0) { }
	public void setComponent(java.awt.Component var0) { }
	public void setDefaultActions(int var0) { }
	public void setFlavorMap(java.awt.datatransfer.FlavorMap var0) { }
	protected void updateAutoscroll(java.awt.Point var0) { }
	protected static class DropTargetAutoScroller implements java.awt.event.ActionListener {
		protected DropTargetAutoScroller(java.awt.Component var0, java.awt.Point var1) { }
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		protected void stop() { }
		protected void updateLocation(java.awt.Point var0) { }
	}
}

