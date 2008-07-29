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

package java.awt;
public class Dialog extends java.awt.Window {
	public Dialog(java.awt.Dialog var0) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Dialog(java.awt.Dialog var0, java.lang.String var1) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Dialog(java.awt.Dialog var0, java.lang.String var1, boolean var2) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Dialog(java.awt.Dialog var0, java.lang.String var1, boolean var2, java.awt.GraphicsConfiguration var3) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Dialog(java.awt.Frame var0) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Dialog(java.awt.Frame var0, java.lang.String var1) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Dialog(java.awt.Frame var0, java.lang.String var1, boolean var2) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Dialog(java.awt.Frame var0, java.lang.String var1, boolean var2, java.awt.GraphicsConfiguration var3) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public Dialog(java.awt.Frame var0, boolean var1) { super((java.awt.Window) null, (java.awt.GraphicsConfiguration) null); }
	public java.lang.String getTitle() { return null; }
	public boolean isModal() { return false; }
	public boolean isResizable() { return false; }
	public boolean isUndecorated() { return false; }
	public void setModal(boolean var0) { }
	public void setResizable(boolean var0) { }
	public void setTitle(java.lang.String var0) { }
	public void setUndecorated(boolean var0) { }
	protected class AccessibleAWTDialog extends java.awt.Window.AccessibleAWTWindow {
		protected AccessibleAWTDialog() { }
	}
}

