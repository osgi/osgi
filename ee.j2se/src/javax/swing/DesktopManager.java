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
public abstract interface DesktopManager {
	public abstract void activateFrame(javax.swing.JInternalFrame var0);
	public abstract void beginDraggingFrame(javax.swing.JComponent var0);
	public abstract void beginResizingFrame(javax.swing.JComponent var0, int var1);
	public abstract void closeFrame(javax.swing.JInternalFrame var0);
	public abstract void deactivateFrame(javax.swing.JInternalFrame var0);
	public abstract void deiconifyFrame(javax.swing.JInternalFrame var0);
	public abstract void dragFrame(javax.swing.JComponent var0, int var1, int var2);
	public abstract void endDraggingFrame(javax.swing.JComponent var0);
	public abstract void endResizingFrame(javax.swing.JComponent var0);
	public abstract void iconifyFrame(javax.swing.JInternalFrame var0);
	public abstract void maximizeFrame(javax.swing.JInternalFrame var0);
	public abstract void minimizeFrame(javax.swing.JInternalFrame var0);
	public abstract void openFrame(javax.swing.JInternalFrame var0);
	public abstract void resizeFrame(javax.swing.JComponent var0, int var1, int var2, int var3, int var4);
	public abstract void setBoundsForFrame(javax.swing.JComponent var0, int var1, int var2, int var3, int var4);
}

