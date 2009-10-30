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

package javax.swing;
public interface DesktopManager {
	void activateFrame(javax.swing.JInternalFrame var0);
	void beginDraggingFrame(javax.swing.JComponent var0);
	void beginResizingFrame(javax.swing.JComponent var0, int var1);
	void closeFrame(javax.swing.JInternalFrame var0);
	void deactivateFrame(javax.swing.JInternalFrame var0);
	void deiconifyFrame(javax.swing.JInternalFrame var0);
	void dragFrame(javax.swing.JComponent var0, int var1, int var2);
	void endDraggingFrame(javax.swing.JComponent var0);
	void endResizingFrame(javax.swing.JComponent var0);
	void iconifyFrame(javax.swing.JInternalFrame var0);
	void maximizeFrame(javax.swing.JInternalFrame var0);
	void minimizeFrame(javax.swing.JInternalFrame var0);
	void openFrame(javax.swing.JInternalFrame var0);
	void resizeFrame(javax.swing.JComponent var0, int var1, int var2, int var3, int var4);
	void setBoundsForFrame(javax.swing.JComponent var0, int var1, int var2, int var3, int var4);
}

