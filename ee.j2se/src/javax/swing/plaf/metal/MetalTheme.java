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

package javax.swing.plaf.metal;
public abstract class MetalTheme {
	public MetalTheme() { }
	public void addCustomEntriesToTable(javax.swing.UIDefaults var0) { }
	public javax.swing.plaf.ColorUIResource getAcceleratorForeground() { return null; }
	public javax.swing.plaf.ColorUIResource getAcceleratorSelectedForeground() { return null; }
	protected javax.swing.plaf.ColorUIResource getBlack() { return null; }
	public javax.swing.plaf.ColorUIResource getControl() { return null; }
	public javax.swing.plaf.ColorUIResource getControlDarkShadow() { return null; }
	public javax.swing.plaf.ColorUIResource getControlDisabled() { return null; }
	public javax.swing.plaf.ColorUIResource getControlHighlight() { return null; }
	public javax.swing.plaf.ColorUIResource getControlInfo() { return null; }
	public javax.swing.plaf.ColorUIResource getControlShadow() { return null; }
	public javax.swing.plaf.ColorUIResource getControlTextColor() { return null; }
	public abstract javax.swing.plaf.FontUIResource getControlTextFont();
	public javax.swing.plaf.ColorUIResource getDesktopColor() { return null; }
	public javax.swing.plaf.ColorUIResource getFocusColor() { return null; }
	public javax.swing.plaf.ColorUIResource getHighlightedTextColor() { return null; }
	public javax.swing.plaf.ColorUIResource getInactiveControlTextColor() { return null; }
	public javax.swing.plaf.ColorUIResource getInactiveSystemTextColor() { return null; }
	public javax.swing.plaf.ColorUIResource getMenuBackground() { return null; }
	public javax.swing.plaf.ColorUIResource getMenuDisabledForeground() { return null; }
	public javax.swing.plaf.ColorUIResource getMenuForeground() { return null; }
	public javax.swing.plaf.ColorUIResource getMenuSelectedBackground() { return null; }
	public javax.swing.plaf.ColorUIResource getMenuSelectedForeground() { return null; }
	public abstract javax.swing.plaf.FontUIResource getMenuTextFont();
	public abstract java.lang.String getName();
	protected abstract javax.swing.plaf.ColorUIResource getPrimary1();
	protected abstract javax.swing.plaf.ColorUIResource getPrimary2();
	protected abstract javax.swing.plaf.ColorUIResource getPrimary3();
	public javax.swing.plaf.ColorUIResource getPrimaryControl() { return null; }
	public javax.swing.plaf.ColorUIResource getPrimaryControlDarkShadow() { return null; }
	public javax.swing.plaf.ColorUIResource getPrimaryControlHighlight() { return null; }
	public javax.swing.plaf.ColorUIResource getPrimaryControlInfo() { return null; }
	public javax.swing.plaf.ColorUIResource getPrimaryControlShadow() { return null; }
	protected abstract javax.swing.plaf.ColorUIResource getSecondary1();
	protected abstract javax.swing.plaf.ColorUIResource getSecondary2();
	protected abstract javax.swing.plaf.ColorUIResource getSecondary3();
	public javax.swing.plaf.ColorUIResource getSeparatorBackground() { return null; }
	public javax.swing.plaf.ColorUIResource getSeparatorForeground() { return null; }
	public abstract javax.swing.plaf.FontUIResource getSubTextFont();
	public javax.swing.plaf.ColorUIResource getSystemTextColor() { return null; }
	public abstract javax.swing.plaf.FontUIResource getSystemTextFont();
	public javax.swing.plaf.ColorUIResource getTextHighlightColor() { return null; }
	public javax.swing.plaf.ColorUIResource getUserTextColor() { return null; }
	public abstract javax.swing.plaf.FontUIResource getUserTextFont();
	protected javax.swing.plaf.ColorUIResource getWhite() { return null; }
	public javax.swing.plaf.ColorUIResource getWindowBackground() { return null; }
	public javax.swing.plaf.ColorUIResource getWindowTitleBackground() { return null; }
	public abstract javax.swing.plaf.FontUIResource getWindowTitleFont();
	public javax.swing.plaf.ColorUIResource getWindowTitleForeground() { return null; }
	public javax.swing.plaf.ColorUIResource getWindowTitleInactiveBackground() { return null; }
	public javax.swing.plaf.ColorUIResource getWindowTitleInactiveForeground() { return null; }
}

