/*
 * $Revision$
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

package javax.swing;
public class DefaultDesktopManager implements java.io.Serializable, javax.swing.DesktopManager {
	public DefaultDesktopManager() { }
	public void activateFrame(javax.swing.JInternalFrame var0) { }
	public void beginDraggingFrame(javax.swing.JComponent var0) { }
	public void beginResizingFrame(javax.swing.JComponent var0, int var1) { }
	public void closeFrame(javax.swing.JInternalFrame var0) { }
	public void deactivateFrame(javax.swing.JInternalFrame var0) { }
	public void deiconifyFrame(javax.swing.JInternalFrame var0) { }
	public void dragFrame(javax.swing.JComponent var0, int var1, int var2) { }
	public void endDraggingFrame(javax.swing.JComponent var0) { }
	public void endResizingFrame(javax.swing.JComponent var0) { }
	protected java.awt.Rectangle getBoundsForIconOf(javax.swing.JInternalFrame var0) { return null; }
	protected java.awt.Rectangle getPreviousBounds(javax.swing.JInternalFrame var0) { return null; }
	public void iconifyFrame(javax.swing.JInternalFrame var0) { }
	public void maximizeFrame(javax.swing.JInternalFrame var0) { }
	public void minimizeFrame(javax.swing.JInternalFrame var0) { }
	public void openFrame(javax.swing.JInternalFrame var0) { }
	protected void removeIconFor(javax.swing.JInternalFrame var0) { }
	public void resizeFrame(javax.swing.JComponent var0, int var1, int var2, int var3, int var4) { }
	public void setBoundsForFrame(javax.swing.JComponent var0, int var1, int var2, int var3, int var4) { }
	protected void setPreviousBounds(javax.swing.JInternalFrame var0, java.awt.Rectangle var1) { }
	protected void setWasIcon(javax.swing.JInternalFrame var0, java.lang.Boolean var1) { }
	protected boolean wasIcon(javax.swing.JInternalFrame var0) { return false; }
}

