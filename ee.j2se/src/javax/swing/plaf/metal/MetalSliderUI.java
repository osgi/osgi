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

package javax.swing.plaf.metal;
public class MetalSliderUI extends javax.swing.plaf.basic.BasicSliderUI {
	public MetalSliderUI() { super((javax.swing.JSlider) null); }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	protected int getThumbOverhang() { return 0; }
	public int getTickLength() { return 0; }
	protected int getTrackLength() { return 0; }
	protected int getTrackWidth() { return 0; }
	protected final java.lang.String SLIDER_FILL = "JSlider.isFilled";
	protected final int TICK_BUFFER = 4;
	protected static java.awt.Color darkShadowColor;
	protected boolean filledSlider;
	protected static java.awt.Color highlightColor;
	protected static javax.swing.Icon horizThumbIcon;
	protected static java.awt.Color thumbColor;
	protected static int tickLength;
	protected static int trackWidth;
	protected static javax.swing.Icon vertThumbIcon;
	protected class MetalPropertyListener extends javax.swing.plaf.basic.BasicSliderUI.PropertyChangeHandler {
		protected MetalPropertyListener() { }
	}
}

