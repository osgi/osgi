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

package java.awt.datatransfer;
public class StringSelection implements java.awt.datatransfer.ClipboardOwner, java.awt.datatransfer.Transferable {
	public StringSelection(java.lang.String var0) { } 
	public java.lang.Object getTransferData(java.awt.datatransfer.DataFlavor var0) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { return null; }
	public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() { return null; }
	public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor var0) { return false; }
	public void lostOwnership(java.awt.datatransfer.Clipboard var0, java.awt.datatransfer.Transferable var1) { }
}

