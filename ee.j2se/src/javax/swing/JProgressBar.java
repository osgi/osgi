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
public class JProgressBar extends javax.swing.JComponent implements javax.accessibility.Accessible, javax.swing.SwingConstants {
	public JProgressBar() { }
	public JProgressBar(int var0) { }
	public JProgressBar(int var0, int var1) { }
	public JProgressBar(int var0, int var1, int var2) { }
	public JProgressBar(javax.swing.BoundedRangeModel var0) { }
	public void addChangeListener(javax.swing.event.ChangeListener var0) { }
	protected javax.swing.event.ChangeListener createChangeListener() { return null; }
	protected void fireStateChanged() { }
	public javax.swing.event.ChangeListener[] getChangeListeners() { return null; }
	public int getMaximum() { return 0; }
	public int getMinimum() { return 0; }
	public javax.swing.BoundedRangeModel getModel() { return null; }
	public int getOrientation() { return 0; }
	public double getPercentComplete() { return 0.0d; }
	public java.lang.String getString() { return null; }
	public javax.swing.plaf.ProgressBarUI getUI() { return null; }
	public int getValue() { return 0; }
	public boolean isBorderPainted() { return false; }
	public boolean isIndeterminate() { return false; }
	public boolean isStringPainted() { return false; }
	public void removeChangeListener(javax.swing.event.ChangeListener var0) { }
	public void setBorderPainted(boolean var0) { }
	public void setIndeterminate(boolean var0) { }
	public void setMaximum(int var0) { }
	public void setMinimum(int var0) { }
	public void setModel(javax.swing.BoundedRangeModel var0) { }
	public void setOrientation(int var0) { }
	public void setString(java.lang.String var0) { }
	public void setStringPainted(boolean var0) { }
	public void setUI(javax.swing.plaf.ProgressBarUI var0) { }
	public void setValue(int var0) { }
	protected javax.swing.event.ChangeEvent changeEvent;
	protected javax.swing.event.ChangeListener changeListener;
	protected javax.swing.BoundedRangeModel model;
	protected int orientation;
	protected boolean paintBorder;
	protected boolean paintString;
	protected java.lang.String progressString;
	protected class AccessibleJProgressBar extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleValue {
		protected AccessibleJProgressBar() { }
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
}

