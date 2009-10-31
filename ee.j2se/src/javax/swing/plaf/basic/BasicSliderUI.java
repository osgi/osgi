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

package javax.swing.plaf.basic;
public class BasicSliderUI extends javax.swing.plaf.SliderUI {
	public class ActionScroller extends javax.swing.AbstractAction {
		public ActionScroller(javax.swing.JSlider var0, int var1, boolean var2) { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
	}
	public class ChangeHandler implements javax.swing.event.ChangeListener {
		public ChangeHandler() { } 
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	public class ComponentHandler extends java.awt.event.ComponentAdapter {
		public ComponentHandler() { } 
	}
	public class FocusHandler implements java.awt.event.FocusListener {
		public FocusHandler() { } 
		public void focusGained(java.awt.event.FocusEvent var0) { }
		public void focusLost(java.awt.event.FocusEvent var0) { }
	}
	public class PropertyChangeHandler implements java.beans.PropertyChangeListener {
		public PropertyChangeHandler() { } 
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
	}
	public class ScrollListener implements java.awt.event.ActionListener {
		public ScrollListener() { } 
		public ScrollListener(int var0, boolean var1) { } 
		public void actionPerformed(java.awt.event.ActionEvent var0) { }
		public void setDirection(int var0) { }
		public void setScrollByBlock(boolean var0) { }
	}
	public class TrackListener extends javax.swing.event.MouseInputAdapter {
		protected int currentMouseX;
		protected int currentMouseY;
		protected int offset;
		public TrackListener() { } 
		public boolean shouldScroll(int var0) { return false; }
	}
	public final static int MAX_SCROLL = 2;
	public final static int MIN_SCROLL = -2;
	public final static int NEGATIVE_SCROLL = -1;
	public final static int POSITIVE_SCROLL = 1;
	protected javax.swing.event.ChangeListener changeListener;
	protected java.awt.event.ComponentListener componentListener;
	protected java.awt.Rectangle contentRect;
	protected java.awt.Insets focusInsets;
	protected java.awt.event.FocusListener focusListener;
	protected java.awt.Rectangle focusRect;
	protected java.awt.Insets insetCache;
	protected java.awt.Rectangle labelRect;
	protected boolean leftToRightCache;
	protected java.beans.PropertyChangeListener propertyChangeListener;
	protected javax.swing.plaf.basic.BasicSliderUI.ScrollListener scrollListener;
	protected javax.swing.Timer scrollTimer;
	protected javax.swing.JSlider slider;
	protected java.awt.Rectangle thumbRect;
	protected java.awt.Rectangle tickRect;
	protected int trackBuffer;
	protected javax.swing.plaf.basic.BasicSliderUI.TrackListener trackListener;
	protected java.awt.Rectangle trackRect;
	public BasicSliderUI(javax.swing.JSlider var0) { } 
	protected void calculateContentRect() { }
	protected void calculateFocusRect() { }
	protected void calculateGeometry() { }
	protected void calculateLabelRect() { }
	protected void calculateThumbLocation() { }
	protected void calculateThumbSize() { }
	protected void calculateTickRect() { }
	protected void calculateTrackBuffer() { }
	protected void calculateTrackRect() { }
	protected javax.swing.event.ChangeListener createChangeListener(javax.swing.JSlider var0) { return null; }
	protected java.awt.event.ComponentListener createComponentListener(javax.swing.JSlider var0) { return null; }
	protected java.awt.event.FocusListener createFocusListener(javax.swing.JSlider var0) { return null; }
	protected java.beans.PropertyChangeListener createPropertyChangeListener(javax.swing.JSlider var0) { return null; }
	protected javax.swing.plaf.basic.BasicSliderUI.ScrollListener createScrollListener(javax.swing.JSlider var0) { return null; }
	protected javax.swing.plaf.basic.BasicSliderUI.TrackListener createTrackListener(javax.swing.JSlider var0) { return null; }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected boolean drawInverted() { return false; }
	protected java.awt.Color getFocusColor() { return null; }
	protected int getHeightOfHighValueLabel() { return 0; }
	protected int getHeightOfLowValueLabel() { return 0; }
	protected int getHeightOfTallestLabel() { return 0; }
	protected java.lang.Integer getHighestValue() { return null; }
	protected java.awt.Component getHighestValueLabel() { return null; }
	protected java.awt.Color getHighlightColor() { return null; }
	protected java.lang.Integer getLowestValue() { return null; }
	protected java.awt.Component getLowestValueLabel() { return null; }
	public java.awt.Dimension getMinimumHorizontalSize() { return null; }
	public java.awt.Dimension getMinimumVerticalSize() { return null; }
	public java.awt.Dimension getPreferredHorizontalSize() { return null; }
	public java.awt.Dimension getPreferredVerticalSize() { return null; }
	protected java.awt.Color getShadowColor() { return null; }
	protected java.awt.Dimension getThumbSize() { return null; }
	protected int getTickLength() { return 0; }
	protected int getWidthOfHighValueLabel() { return 0; }
	protected int getWidthOfLowValueLabel() { return 0; }
	protected int getWidthOfWidestLabel() { return 0; }
	protected void installDefaults(javax.swing.JSlider var0) { }
	protected void installKeyboardActions(javax.swing.JSlider var0) { }
	protected void installListeners(javax.swing.JSlider var0) { }
	protected boolean isDragging() { return false; }
	protected boolean labelsHaveSameBaselines() { return false; }
	public void paintFocus(java.awt.Graphics var0) { }
	protected void paintHorizontalLabel(java.awt.Graphics var0, int var1, java.awt.Component var2) { }
	public void paintLabels(java.awt.Graphics var0) { }
	protected void paintMajorTickForHorizSlider(java.awt.Graphics var0, java.awt.Rectangle var1, int var2) { }
	protected void paintMajorTickForVertSlider(java.awt.Graphics var0, java.awt.Rectangle var1, int var2) { }
	protected void paintMinorTickForHorizSlider(java.awt.Graphics var0, java.awt.Rectangle var1, int var2) { }
	protected void paintMinorTickForVertSlider(java.awt.Graphics var0, java.awt.Rectangle var1, int var2) { }
	public void paintThumb(java.awt.Graphics var0) { }
	public void paintTicks(java.awt.Graphics var0) { }
	public void paintTrack(java.awt.Graphics var0) { }
	protected void paintVerticalLabel(java.awt.Graphics var0, int var1, java.awt.Component var2) { }
	protected void recalculateIfInsetsChanged() { }
	protected void recalculateIfOrientationChanged() { }
	public void scrollByBlock(int var0) { }
	public void scrollByUnit(int var0) { }
	protected void scrollDueToClickInTrack(int var0) { }
	public void setThumbLocation(int var0, int var1) { }
	protected void uninstallKeyboardActions(javax.swing.JSlider var0) { }
	protected void uninstallListeners(javax.swing.JSlider var0) { }
	public int valueForXPosition(int var0) { return 0; }
	public int valueForYPosition(int var0) { return 0; }
	protected int xPositionForValue(int var0) { return 0; }
	protected int yPositionForValue(int var0) { return 0; }
	protected int yPositionForValue(int var0, int var1, int var2) { return 0; }
}

