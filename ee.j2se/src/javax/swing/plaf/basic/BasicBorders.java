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

package javax.swing.plaf.basic;
public class BasicBorders {
	public BasicBorders() { }
	public static javax.swing.border.Border getButtonBorder() { return null; }
	public static javax.swing.border.Border getInternalFrameBorder() { return null; }
	public static javax.swing.border.Border getMenuBarBorder() { return null; }
	public static javax.swing.border.Border getProgressBarBorder() { return null; }
	public static javax.swing.border.Border getRadioButtonBorder() { return null; }
	public static javax.swing.border.Border getSplitPaneBorder() { return null; }
	public static javax.swing.border.Border getSplitPaneDividerBorder() { return null; }
	public static javax.swing.border.Border getTextFieldBorder() { return null; }
	public static javax.swing.border.Border getToggleButtonBorder() { return null; }
	public static class ButtonBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public ButtonBorder(java.awt.Color var0, java.awt.Color var1, java.awt.Color var2, java.awt.Color var3) { }
		protected java.awt.Color darkShadow;
		protected java.awt.Color highlight;
		protected java.awt.Color lightHighlight;
		protected java.awt.Color shadow;
	}
	public static class FieldBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public FieldBorder(java.awt.Color var0, java.awt.Color var1, java.awt.Color var2, java.awt.Color var3) { }
		protected java.awt.Color darkShadow;
		protected java.awt.Color highlight;
		protected java.awt.Color lightHighlight;
		protected java.awt.Color shadow;
	}
	public static class MarginBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public MarginBorder() { }
	}
	public static class MenuBarBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public MenuBarBorder(java.awt.Color var0, java.awt.Color var1) { }
	}
	public static class RadioButtonBorder extends javax.swing.plaf.basic.BasicBorders.ButtonBorder {
		public RadioButtonBorder(java.awt.Color var0, java.awt.Color var1, java.awt.Color var2, java.awt.Color var3) { super((java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null); }
	}
	public static class RolloverButtonBorder extends javax.swing.plaf.basic.BasicBorders.ButtonBorder {
		public RolloverButtonBorder(java.awt.Color var0, java.awt.Color var1, java.awt.Color var2, java.awt.Color var3) { super((java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null); }
	}
	public static class SplitPaneBorder implements javax.swing.border.Border, javax.swing.plaf.UIResource {
		public SplitPaneBorder(java.awt.Color var0, java.awt.Color var1) { }
		public java.awt.Insets getBorderInsets(java.awt.Component var0) { return null; }
		public boolean isBorderOpaque() { return false; }
		public void paintBorder(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3, int var4, int var5) { }
		protected java.awt.Color highlight;
		protected java.awt.Color shadow;
	}
	public static class ToggleButtonBorder extends javax.swing.plaf.basic.BasicBorders.ButtonBorder {
		public ToggleButtonBorder(java.awt.Color var0, java.awt.Color var1, java.awt.Color var2, java.awt.Color var3) { super((java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null, (java.awt.Color) null); }
	}
}

