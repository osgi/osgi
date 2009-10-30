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

package javax.swing.plaf.metal;
public class MetalBorders {
	public static class ButtonBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		protected static java.awt.Insets borderInsets;
		public ButtonBorder() { } 
	}
	public static class Flush3DBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public Flush3DBorder() { } 
	}
	public static class InternalFrameBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public InternalFrameBorder() { } 
	}
	public static class MenuBarBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		protected static java.awt.Insets borderInsets;
		public MenuBarBorder() { } 
	}
	public static class MenuItemBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		protected static java.awt.Insets borderInsets;
		public MenuItemBorder() { } 
	}
	public static class OptionDialogBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public OptionDialogBorder() { } 
	}
	public static class PaletteBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public PaletteBorder() { } 
	}
	public static class PopupMenuBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		protected static java.awt.Insets borderInsets;
		public PopupMenuBorder() { } 
	}
	public static class RolloverButtonBorder extends javax.swing.plaf.metal.MetalBorders.ButtonBorder {
		public RolloverButtonBorder() { } 
	}
	public static class ScrollPaneBorder extends javax.swing.border.AbstractBorder implements javax.swing.plaf.UIResource {
		public ScrollPaneBorder() { } 
	}
	public static class TableHeaderBorder extends javax.swing.border.AbstractBorder {
		protected java.awt.Insets editorBorderInsets;
		public TableHeaderBorder() { } 
	}
	public static class TextFieldBorder extends javax.swing.plaf.metal.MetalBorders.Flush3DBorder {
		public TextFieldBorder() { } 
	}
	public static class ToggleButtonBorder extends javax.swing.plaf.metal.MetalBorders.ButtonBorder {
		public ToggleButtonBorder() { } 
	}
	public static class ToolBarBorder extends javax.swing.border.AbstractBorder implements javax.swing.SwingConstants, javax.swing.plaf.UIResource {
		protected javax.swing.plaf.metal.MetalBumps bumps;
		public ToolBarBorder() { } 
	}
	public MetalBorders() { } 
	public static javax.swing.border.Border getButtonBorder() { return null; }
	public static javax.swing.border.Border getDesktopIconBorder() { return null; }
	public static javax.swing.border.Border getTextBorder() { return null; }
	public static javax.swing.border.Border getTextFieldBorder() { return null; }
	public static javax.swing.border.Border getToggleButtonBorder() { return null; }
}

