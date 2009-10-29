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
public class MetalScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
	public MetalScrollBarUI() { }
	public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent var0) { return null; }
	public final static java.lang.String FREE_STANDING_PROP = "JScrollBar.isFreeStanding";
	protected javax.swing.plaf.metal.MetalBumps bumps;
	protected javax.swing.plaf.metal.MetalScrollButton decreaseButton;
	protected javax.swing.plaf.metal.MetalScrollButton increaseButton;
	protected boolean isFreeStanding;
	protected int scrollBarWidth;
	private static java.awt.Color thumbColor;
	private static java.awt.Color thumbHighlightColor;
}

