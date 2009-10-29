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
public class DropTargetDragEvent extends java.awt.dnd.DropTargetEvent {
	public DropTargetDragEvent(java.awt.dnd.DropTargetContext var0, java.awt.Point var1, int var2, int var3) { super((java.awt.dnd.DropTargetContext) null); }
	public void acceptDrag(int var0) { }
	public java.awt.datatransfer.DataFlavor[] getCurrentDataFlavors() { return null; }
	public java.util.List getCurrentDataFlavorsAsList() { return null; }
	public int getDropAction() { return 0; }
	public java.awt.Point getLocation() { return null; }
	public int getSourceActions() { return 0; }
	public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor var0) { return false; }
	public void rejectDrag() { }
}

