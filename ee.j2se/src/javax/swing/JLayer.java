/*
 * Copyright (c) OSGi Alliance (2001, 2013). All Rights Reserved.
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
public final class JLayer<V extends java.awt.Component> extends javax.swing.JComponent implements java.beans.PropertyChangeListener, javax.accessibility.Accessible, javax.swing.Scrollable {
	public JLayer() { } 
	public JLayer(V var0) { } 
	public JLayer(V var0, javax.swing.plaf.LayerUI<V> var1) { } 
	public javax.swing.JPanel createGlassPane() { return null; }
	public javax.swing.JPanel getGlassPane() { return null; }
	public long getLayerEventMask() { return 0l; }
	public java.awt.Dimension getPreferredScrollableViewportSize() { return null; }
	public int getScrollableBlockIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public boolean getScrollableTracksViewportHeight() { return false; }
	public boolean getScrollableTracksViewportWidth() { return false; }
	public int getScrollableUnitIncrement(java.awt.Rectangle var0, int var1, int var2) { return 0; }
	public javax.swing.plaf.LayerUI<? super V> getUI() { return null; }
	public V getView() { return null; }
	public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	public void setGlassPane(javax.swing.JPanel var0) { }
	public void setLayerEventMask(long var0) { }
	public void setUI(javax.swing.plaf.LayerUI<? super V> var0) { }
	public void setView(V var0) { }
}

