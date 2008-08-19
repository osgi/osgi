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
public class JSlider extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.SwingConstants {
	public JSlider() { }
	public JSlider(int var0) { }
	public JSlider(int var0, int var1) { }
	public JSlider(int var0, int var1, int var2) { }
	public JSlider(int var0, int var1, int var2, int var3) { }
	public JSlider(javax.swing.BoundedRangeModel var0) { }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	protected javax.swing.event.ChangeListener createChangeListener() { return null; }
	public java.util.Hashtable createStandardLabels(int var0) { return null; }
	public java.util.Hashtable createStandardLabels(int var0, int var1) { return null; }
	protected void fireStateChanged() { }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public int getExtent() { return 0; }
	public boolean getInverted() { return false; }
	public java.util.Dictionary getLabelTable() { return null; }
	public int getMajorTickSpacing() { return 0; }
	public int getMaximum() { return 0; }
	public int getMinimum() { return 0; }
	public int getMinorTickSpacing() { return 0; }
	public javax.swing.BoundedRangeModel getModel() { return null; }
	public int getOrientation() { return 0; }
	public boolean getPaintLabels() { return false; }
	public boolean getPaintTicks() { return false; }
	public boolean getPaintTrack() { return false; }
	public boolean getSnapToTicks() { return false; }
	public javax.swing.plaf.SliderUI getUI() { return null; }
	public int getValue() { return 0; }
	public boolean getValueIsAdjusting() { return false; }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void setExtent(int var0) { }
	public void setInverted(boolean var0) { }
	public void setLabelTable(java.util.Dictionary var0) { }
	public void setMajorTickSpacing(int var0) { }
	public void setMaximum(int var0) { }
	public void setMinimum(int var0) { }
	public void setMinorTickSpacing(int var0) { }
	public void setModel(javax.swing.BoundedRangeModel var0) { }
	public void setOrientation(int var0) { }
	public void setPaintLabels(boolean var0) { }
	public void setPaintTicks(boolean var0) { }
	public void setPaintTrack(boolean var0) { }
	public void setSnapToTicks(boolean var0) { }
	public void setUI(javax.swing.plaf.SliderUI var0) { }
	public void setValue(int var0) { }
	public void setValueIsAdjusting(boolean var0) { }
	protected void updateLabelUIs() { }
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.event.ChangeListener changeListener;
	protected int majorTickSpacing;
	protected int minorTickSpacing;
	protected int orientation;
	protected javax.swing.BoundedRangeModel sliderModel;
	protected boolean snapToTicks;
	protected class AccessibleJSlider extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleValue {
		protected AccessibleJSlider() { }
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
}

