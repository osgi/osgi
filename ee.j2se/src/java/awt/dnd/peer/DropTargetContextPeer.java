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

package java.awt.dnd.peer;
public abstract interface DropTargetContextPeer {
	public abstract void acceptDrag(int var0);
	public abstract void acceptDrop(int var0);
	public abstract void dropComplete(boolean var0);
	public abstract java.awt.dnd.DropTarget getDropTarget();
	public abstract int getTargetActions();
	public abstract java.awt.datatransfer.DataFlavor[] getTransferDataFlavors();
	public abstract java.awt.datatransfer.Transferable getTransferable();
	public abstract boolean isTransferableJVMLocal();
	public abstract void rejectDrag();
	public abstract void rejectDrop();
	public abstract void setTargetActions(int var0);
}

