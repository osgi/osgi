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
public class Clipboard {
	protected java.awt.datatransfer.Transferable contents;
	protected java.awt.datatransfer.ClipboardOwner owner;
	public Clipboard(java.lang.String var0) { } 
	public void addFlavorListener(java.awt.datatransfer.FlavorListener var0) { }
	public java.awt.datatransfer.DataFlavor[] getAvailableDataFlavors() { return null; }
	public java.awt.datatransfer.Transferable getContents(java.lang.Object var0) { return null; }
	public java.lang.Object getData(java.awt.datatransfer.DataFlavor var0) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException { return null; }
	public java.awt.datatransfer.FlavorListener[] getFlavorListeners() { return null; }
	public java.lang.String getName() { return null; }
	public boolean isDataFlavorAvailable(java.awt.datatransfer.DataFlavor var0) { return false; }
	public void removeFlavorListener(java.awt.datatransfer.FlavorListener var0) { }
	public void setContents(java.awt.datatransfer.Transferable var0, java.awt.datatransfer.ClipboardOwner var1) { }
}

