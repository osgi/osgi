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
public class DropTargetContext implements java.io.Serializable {
	protected void acceptDrag(int var0) { }
	protected void acceptDrop(int var0) { }
	public void addNotify(java.awt.dnd.peer.DropTargetContextPeer var0) { }
	protected java.awt.datatransfer.Transferable createTransferableProxy(java.awt.datatransfer.Transferable var0, boolean var1) { return null; }
	public void dropComplete(boolean var0) { }
	public java.awt.Component getComponent() { return null; }
	protected java.awt.datatransfer.DataFlavor[] getCurrentDataFlavors() { return null; }
	protected java.util.List getCurrentDataFlavorsAsList() { return null; }
	public java.awt.dnd.DropTarget getDropTarget() { return null; }
	protected int getTargetActions() { return 0; }
	protected java.awt.datatransfer.Transferable getTransferable() { return null; }
	protected boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor var0) { return false; }
	protected void rejectDrag() { }
	protected void rejectDrop() { }
	public void removeNotify() { }
	protected void setTargetActions(int var0) { }
	protected class TransferableProxy implements java.awt.datatransfer.Transferable {
		public java.lang.Object getTransferData(java.awt.datatransfer.DataFlavor var0) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { return null; }
		public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() { return null; }
		public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor var0) { return false; }
		protected boolean isLocal;
		protected java.awt.datatransfer.Transferable transferable;
		private TransferableProxy() { } /* generated constructor to prevent compiler adding default public constructor */
	}
	private DropTargetContext() { } /* generated constructor to prevent compiler adding default public constructor */
}

