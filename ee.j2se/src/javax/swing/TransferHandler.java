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

package javax.swing;
public class TransferHandler implements java.io.Serializable {
	protected TransferHandler() { }
	public TransferHandler(java.lang.String var0) { }
	public boolean canImport(javax.swing.JComponent var0, java.awt.datatransfer.DataFlavor[] var1) { return false; }
	protected java.awt.datatransfer.Transferable createTransferable(javax.swing.JComponent var0) { return null; }
	public void exportAsDrag(javax.swing.JComponent var0, java.awt.event.InputEvent var1, int var2) { }
	protected void exportDone(javax.swing.JComponent var0, java.awt.datatransfer.Transferable var1, int var2) { }
	public void exportToClipboard(javax.swing.JComponent var0, java.awt.datatransfer.Clipboard var1, int var2) { }
	public static javax.swing.Action getCopyAction() { return null; }
	public static javax.swing.Action getCutAction() { return null; }
	public static javax.swing.Action getPasteAction() { return null; }
	public int getSourceActions(javax.swing.JComponent var0) { return 0; }
	public javax.swing.Icon getVisualRepresentation(java.awt.datatransfer.Transferable var0) { return null; }
	public boolean importData(javax.swing.JComponent var0, java.awt.datatransfer.Transferable var1) { return false; }
	public final static int COPY = 1;
	public final static int COPY_OR_MOVE = 3;
	public final static int MOVE = 2;
	public final static int NONE = 0;
}

