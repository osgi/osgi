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

package javax.swing;
public class JLayeredPane extends javax.swing.JComponent implements javax.accessibility.Accessible {
	public JLayeredPane() { }
	public int getComponentCountInLayer(int var0) { return 0; }
	protected java.util.Hashtable getComponentToLayer() { return null; }
	public java.awt.Component[] getComponentsInLayer(int var0) { return null; }
	public int getIndexOf(java.awt.Component var0) { return 0; }
	public int getLayer(java.awt.Component var0) { return 0; }
	public static int getLayer(javax.swing.JComponent var0) { return 0; }
	public static javax.swing.JLayeredPane getLayeredPaneAbove(java.awt.Component var0) { return null; }
	protected java.lang.Integer getObjectForLayer(int var0) { return null; }
	public int getPosition(java.awt.Component var0) { return 0; }
	public int highestLayer() { return 0; }
	protected int insertIndexForLayer(int var0, int var1) { return 0; }
	public int lowestLayer() { return 0; }
	public void moveToBack(java.awt.Component var0) { }
	public void moveToFront(java.awt.Component var0) { }
	public static void putLayer(javax.swing.JComponent var0, int var1) { }
	public void setLayer(java.awt.Component var0, int var1) { }
	public void setLayer(java.awt.Component var0, int var1, int var2) { }
	public void setPosition(java.awt.Component var0, int var1) { }
	public final static java.lang.Integer DEFAULT_LAYER; static { DEFAULT_LAYER = null; }
	public final static java.lang.Integer DRAG_LAYER; static { DRAG_LAYER = null; }
	public final static java.lang.Integer FRAME_CONTENT_LAYER; static { FRAME_CONTENT_LAYER = null; }
	public final static java.lang.String LAYER_PROPERTY = "layeredContainerLayer";
	public final static java.lang.Integer MODAL_LAYER; static { MODAL_LAYER = null; }
	public final static java.lang.Integer PALETTE_LAYER; static { PALETTE_LAYER = null; }
	public final static java.lang.Integer POPUP_LAYER; static { POPUP_LAYER = null; }
	protected class AccessibleJLayeredPane extends javax.swing.JComponent.AccessibleJComponent {
		protected AccessibleJLayeredPane() { }
	}
}

