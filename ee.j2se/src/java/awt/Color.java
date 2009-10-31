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
public class Color implements java.awt.Paint, java.io.Serializable {
	public final static java.awt.Color BLACK; static { BLACK = null; }
	public final static java.awt.Color BLUE; static { BLUE = null; }
	public final static java.awt.Color CYAN; static { CYAN = null; }
	public final static java.awt.Color DARK_GRAY; static { DARK_GRAY = null; }
	public final static java.awt.Color GRAY; static { GRAY = null; }
	public final static java.awt.Color GREEN; static { GREEN = null; }
	public final static java.awt.Color LIGHT_GRAY; static { LIGHT_GRAY = null; }
	public final static java.awt.Color MAGENTA; static { MAGENTA = null; }
	public final static java.awt.Color ORANGE; static { ORANGE = null; }
	public final static java.awt.Color PINK; static { PINK = null; }
	public final static java.awt.Color RED; static { RED = null; }
	public final static java.awt.Color WHITE; static { WHITE = null; }
	public final static java.awt.Color YELLOW; static { YELLOW = null; }
	public final static java.awt.Color black; static { black = null; }
	public final static java.awt.Color blue; static { blue = null; }
	public final static java.awt.Color cyan; static { cyan = null; }
	public final static java.awt.Color darkGray; static { darkGray = null; }
	public final static java.awt.Color gray; static { gray = null; }
	public final static java.awt.Color green; static { green = null; }
	public final static java.awt.Color lightGray; static { lightGray = null; }
	public final static java.awt.Color magenta; static { magenta = null; }
	public final static java.awt.Color orange; static { orange = null; }
	public final static java.awt.Color pink; static { pink = null; }
	public final static java.awt.Color red; static { red = null; }
	public final static java.awt.Color white; static { white = null; }
	public final static java.awt.Color yellow; static { yellow = null; }
	public Color(float var0, float var1, float var2) { } 
	public Color(float var0, float var1, float var2, float var3) { } 
	public Color(int var0) { } 
	public Color(int var0, int var1, int var2) { } 
	public Color(int var0, int var1, int var2, int var3) { } 
	public Color(int var0, boolean var1) { } 
	public Color(java.awt.color.ColorSpace var0, float[] var1, float var2) { } 
	public static int HSBtoRGB(float var0, float var1, float var2) { return 0; }
	public static float[] RGBtoHSB(int var0, int var1, int var2, float[] var3) { return null; }
	public java.awt.Color brighter() { return null; }
	public java.awt.PaintContext createContext(java.awt.image.ColorModel var0, java.awt.Rectangle var1, java.awt.geom.Rectangle2D var2, java.awt.geom.AffineTransform var3, java.awt.RenderingHints var4) { return null; }
	public java.awt.Color darker() { return null; }
	public static java.awt.Color decode(java.lang.String var0) { return null; }
	public int getAlpha() { return 0; }
	public int getBlue() { return 0; }
	public static java.awt.Color getColor(java.lang.String var0) { return null; }
	public static java.awt.Color getColor(java.lang.String var0, int var1) { return null; }
	public static java.awt.Color getColor(java.lang.String var0, java.awt.Color var1) { return null; }
	public float[] getColorComponents(java.awt.color.ColorSpace var0, float[] var1) { return null; }
	public float[] getColorComponents(float[] var0) { return null; }
	public java.awt.color.ColorSpace getColorSpace() { return null; }
	public float[] getComponents(java.awt.color.ColorSpace var0, float[] var1) { return null; }
	public float[] getComponents(float[] var0) { return null; }
	public int getGreen() { return 0; }
	public static java.awt.Color getHSBColor(float var0, float var1, float var2) { return null; }
	public int getRGB() { return 0; }
	public float[] getRGBColorComponents(float[] var0) { return null; }
	public float[] getRGBComponents(float[] var0) { return null; }
	public int getRed() { return 0; }
	public int getTransparency() { return 0; }
}

