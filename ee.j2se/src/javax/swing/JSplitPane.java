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
public class JSplitPane extends javax.swing.JComponent implements javax.accessibility.Accessible {
	public JSplitPane() { }
	public JSplitPane(int var0) { }
	public JSplitPane(int var0, java.awt.Component var1, java.awt.Component var2) { }
	public JSplitPane(int var0, boolean var1) { }
	public JSplitPane(int var0, boolean var1, java.awt.Component var2, java.awt.Component var3) { }
	public java.awt.Component getBottomComponent() { return null; }
	public int getDividerLocation() { return 0; }
	public int getDividerSize() { return 0; }
	public int getLastDividerLocation() { return 0; }
	public java.awt.Component getLeftComponent() { return null; }
	public int getMaximumDividerLocation() { return 0; }
	public int getMinimumDividerLocation() { return 0; }
	public int getOrientation() { return 0; }
	public double getResizeWeight() { return 0.0d; }
	public java.awt.Component getRightComponent() { return null; }
	public java.awt.Component getTopComponent() { return null; }
	public javax.swing.plaf.SplitPaneUI getUI() { return null; }
	public boolean isContinuousLayout() { return false; }
	public boolean isOneTouchExpandable() { return false; }
	public void resetToPreferredSizes() { }
	public void setBottomComponent(java.awt.Component var0) { }
	public void setContinuousLayout(boolean var0) { }
	public void setDividerLocation(double var0) { }
	public void setDividerLocation(int var0) { }
	public void setDividerSize(int var0) { }
	public void setLastDividerLocation(int var0) { }
	public void setLeftComponent(java.awt.Component var0) { }
	public void setOneTouchExpandable(boolean var0) { }
	public void setOrientation(int var0) { }
	public void setResizeWeight(double var0) { }
	public void setRightComponent(java.awt.Component var0) { }
	public void setTopComponent(java.awt.Component var0) { }
	public void setUI(javax.swing.plaf.SplitPaneUI var0) { }
	public final static java.lang.String BOTTOM = "bottom";
	public final static java.lang.String CONTINUOUS_LAYOUT_PROPERTY = "continuousLayout";
	public final static java.lang.String DIVIDER = "divider";
	public final static java.lang.String DIVIDER_LOCATION_PROPERTY = "dividerLocation";
	public final static java.lang.String DIVIDER_SIZE_PROPERTY = "dividerSize";
	public final static int HORIZONTAL_SPLIT = 1;
	public final static java.lang.String LAST_DIVIDER_LOCATION_PROPERTY = "lastDividerLocation";
	public final static java.lang.String LEFT = "left";
	public final static java.lang.String ONE_TOUCH_EXPANDABLE_PROPERTY = "oneTouchExpandable";
	public final static java.lang.String ORIENTATION_PROPERTY = "orientation";
	public final static java.lang.String RESIZE_WEIGHT_PROPERTY = "resizeWeight";
	public final static java.lang.String RIGHT = "right";
	public final static java.lang.String TOP = "top";
	public final static int VERTICAL_SPLIT = 0;
	protected boolean continuousLayout;
	protected int dividerSize;
	protected int lastDividerLocation;
	protected java.awt.Component leftComponent;
	protected boolean oneTouchExpandable;
	protected int orientation;
	protected java.awt.Component rightComponent;
	protected class AccessibleJSplitPane extends javax.swing.JComponent.AccessibleJComponent implements javax.accessibility.AccessibleValue {
		protected AccessibleJSplitPane() { }
		public java.lang.Number getCurrentAccessibleValue() { return null; }
		public java.lang.Number getMaximumAccessibleValue() { return null; }
		public java.lang.Number getMinimumAccessibleValue() { return null; }
		public boolean setCurrentAccessibleValue(java.lang.Number var0) { return false; }
	}
}

