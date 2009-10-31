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

package java.awt;
public abstract class GraphicsEnvironment {
	protected GraphicsEnvironment() { } 
	public abstract java.awt.Graphics2D createGraphics(java.awt.image.BufferedImage var0);
	public abstract java.awt.Font[] getAllFonts();
	public abstract java.lang.String[] getAvailableFontFamilyNames();
	public abstract java.lang.String[] getAvailableFontFamilyNames(java.util.Locale var0);
	public java.awt.Point getCenterPoint() { return null; }
	public abstract java.awt.GraphicsDevice getDefaultScreenDevice();
	public static java.awt.GraphicsEnvironment getLocalGraphicsEnvironment() { return null; }
	public java.awt.Rectangle getMaximumWindowBounds() { return null; }
	public abstract java.awt.GraphicsDevice[] getScreenDevices();
	public static boolean isHeadless() { return false; }
	public boolean isHeadlessInstance() { return false; }
	public void preferLocaleFonts() { }
	public void preferProportionalFonts() { }
	public boolean registerFont(java.awt.Font var0) { return false; }
}

