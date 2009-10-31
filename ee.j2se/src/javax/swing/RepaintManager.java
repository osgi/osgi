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
public class RepaintManager {
	public RepaintManager() { } 
	public void addDirtyRegion(java.applet.Applet var0, int var1, int var2, int var3, int var4) { }
	public void addDirtyRegion(java.awt.Window var0, int var1, int var2, int var3, int var4) { }
	public void addDirtyRegion(javax.swing.JComponent var0, int var1, int var2, int var3, int var4) { }
	public void addInvalidComponent(javax.swing.JComponent var0) { }
	public static javax.swing.RepaintManager currentManager(java.awt.Component var0) { return null; }
	public static javax.swing.RepaintManager currentManager(javax.swing.JComponent var0) { return null; }
	public java.awt.Rectangle getDirtyRegion(javax.swing.JComponent var0) { return null; }
	public java.awt.Dimension getDoubleBufferMaximumSize() { return null; }
	public java.awt.Image getOffscreenBuffer(java.awt.Component var0, int var1, int var2) { return null; }
	public java.awt.Image getVolatileOffscreenBuffer(java.awt.Component var0, int var1, int var2) { return null; }
	public boolean isCompletelyDirty(javax.swing.JComponent var0) { return false; }
	public boolean isDoubleBufferingEnabled() { return false; }
	public void markCompletelyClean(javax.swing.JComponent var0) { }
	public void markCompletelyDirty(javax.swing.JComponent var0) { }
	public void paintDirtyRegions() { }
	public void removeInvalidComponent(javax.swing.JComponent var0) { }
	public static void setCurrentManager(javax.swing.RepaintManager var0) { }
	public void setDoubleBufferMaximumSize(java.awt.Dimension var0) { }
	public void setDoubleBufferingEnabled(boolean var0) { }
	public void validateInvalidComponents() { }
}

