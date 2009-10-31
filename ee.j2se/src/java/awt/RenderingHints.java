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
public class RenderingHints implements java.lang.Cloneable, java.util.Map<java.lang.Object,java.lang.Object> {
	public static abstract class Key {
		protected Key(int var0) { } 
		public final boolean equals(java.lang.Object var0) { return false; }
		public final int hashCode() { return 0; }
		protected final int intKey() { return 0; }
		public abstract boolean isCompatibleValue(java.lang.Object var0);
	}
	public final static java.awt.RenderingHints.Key KEY_ALPHA_INTERPOLATION; static { KEY_ALPHA_INTERPOLATION = null; }
	public final static java.awt.RenderingHints.Key KEY_ANTIALIASING; static { KEY_ANTIALIASING = null; }
	public final static java.awt.RenderingHints.Key KEY_COLOR_RENDERING; static { KEY_COLOR_RENDERING = null; }
	public final static java.awt.RenderingHints.Key KEY_DITHERING; static { KEY_DITHERING = null; }
	public final static java.awt.RenderingHints.Key KEY_FRACTIONALMETRICS; static { KEY_FRACTIONALMETRICS = null; }
	public final static java.awt.RenderingHints.Key KEY_INTERPOLATION; static { KEY_INTERPOLATION = null; }
	public final static java.awt.RenderingHints.Key KEY_RENDERING; static { KEY_RENDERING = null; }
	public final static java.awt.RenderingHints.Key KEY_STROKE_CONTROL; static { KEY_STROKE_CONTROL = null; }
	public final static java.awt.RenderingHints.Key KEY_TEXT_ANTIALIASING; static { KEY_TEXT_ANTIALIASING = null; }
	public final static java.awt.RenderingHints.Key KEY_TEXT_LCD_CONTRAST; static { KEY_TEXT_LCD_CONTRAST = null; }
	public final static java.lang.Object VALUE_ALPHA_INTERPOLATION_DEFAULT; static { VALUE_ALPHA_INTERPOLATION_DEFAULT = null; }
	public final static java.lang.Object VALUE_ALPHA_INTERPOLATION_QUALITY; static { VALUE_ALPHA_INTERPOLATION_QUALITY = null; }
	public final static java.lang.Object VALUE_ALPHA_INTERPOLATION_SPEED; static { VALUE_ALPHA_INTERPOLATION_SPEED = null; }
	public final static java.lang.Object VALUE_ANTIALIAS_DEFAULT; static { VALUE_ANTIALIAS_DEFAULT = null; }
	public final static java.lang.Object VALUE_ANTIALIAS_OFF; static { VALUE_ANTIALIAS_OFF = null; }
	public final static java.lang.Object VALUE_ANTIALIAS_ON; static { VALUE_ANTIALIAS_ON = null; }
	public final static java.lang.Object VALUE_COLOR_RENDER_DEFAULT; static { VALUE_COLOR_RENDER_DEFAULT = null; }
	public final static java.lang.Object VALUE_COLOR_RENDER_QUALITY; static { VALUE_COLOR_RENDER_QUALITY = null; }
	public final static java.lang.Object VALUE_COLOR_RENDER_SPEED; static { VALUE_COLOR_RENDER_SPEED = null; }
	public final static java.lang.Object VALUE_DITHER_DEFAULT; static { VALUE_DITHER_DEFAULT = null; }
	public final static java.lang.Object VALUE_DITHER_DISABLE; static { VALUE_DITHER_DISABLE = null; }
	public final static java.lang.Object VALUE_DITHER_ENABLE; static { VALUE_DITHER_ENABLE = null; }
	public final static java.lang.Object VALUE_FRACTIONALMETRICS_DEFAULT; static { VALUE_FRACTIONALMETRICS_DEFAULT = null; }
	public final static java.lang.Object VALUE_FRACTIONALMETRICS_OFF; static { VALUE_FRACTIONALMETRICS_OFF = null; }
	public final static java.lang.Object VALUE_FRACTIONALMETRICS_ON; static { VALUE_FRACTIONALMETRICS_ON = null; }
	public final static java.lang.Object VALUE_INTERPOLATION_BICUBIC; static { VALUE_INTERPOLATION_BICUBIC = null; }
	public final static java.lang.Object VALUE_INTERPOLATION_BILINEAR; static { VALUE_INTERPOLATION_BILINEAR = null; }
	public final static java.lang.Object VALUE_INTERPOLATION_NEAREST_NEIGHBOR; static { VALUE_INTERPOLATION_NEAREST_NEIGHBOR = null; }
	public final static java.lang.Object VALUE_RENDER_DEFAULT; static { VALUE_RENDER_DEFAULT = null; }
	public final static java.lang.Object VALUE_RENDER_QUALITY; static { VALUE_RENDER_QUALITY = null; }
	public final static java.lang.Object VALUE_RENDER_SPEED; static { VALUE_RENDER_SPEED = null; }
	public final static java.lang.Object VALUE_STROKE_DEFAULT; static { VALUE_STROKE_DEFAULT = null; }
	public final static java.lang.Object VALUE_STROKE_NORMALIZE; static { VALUE_STROKE_NORMALIZE = null; }
	public final static java.lang.Object VALUE_STROKE_PURE; static { VALUE_STROKE_PURE = null; }
	public final static java.lang.Object VALUE_TEXT_ANTIALIAS_DEFAULT; static { VALUE_TEXT_ANTIALIAS_DEFAULT = null; }
	public final static java.lang.Object VALUE_TEXT_ANTIALIAS_GASP; static { VALUE_TEXT_ANTIALIAS_GASP = null; }
	public final static java.lang.Object VALUE_TEXT_ANTIALIAS_LCD_HBGR; static { VALUE_TEXT_ANTIALIAS_LCD_HBGR = null; }
	public final static java.lang.Object VALUE_TEXT_ANTIALIAS_LCD_HRGB; static { VALUE_TEXT_ANTIALIAS_LCD_HRGB = null; }
	public final static java.lang.Object VALUE_TEXT_ANTIALIAS_LCD_VBGR; static { VALUE_TEXT_ANTIALIAS_LCD_VBGR = null; }
	public final static java.lang.Object VALUE_TEXT_ANTIALIAS_LCD_VRGB; static { VALUE_TEXT_ANTIALIAS_LCD_VRGB = null; }
	public final static java.lang.Object VALUE_TEXT_ANTIALIAS_OFF; static { VALUE_TEXT_ANTIALIAS_OFF = null; }
	public final static java.lang.Object VALUE_TEXT_ANTIALIAS_ON; static { VALUE_TEXT_ANTIALIAS_ON = null; }
	public RenderingHints(java.awt.RenderingHints.Key var0, java.lang.Object var1) { } 
	public RenderingHints(java.util.Map<java.awt.RenderingHints.Key,?> var0) { } 
	public void add(java.awt.RenderingHints var0) { }
	public void clear() { }
	public java.lang.Object clone() { return null; }
	public boolean containsKey(java.lang.Object var0) { return false; }
	public boolean containsValue(java.lang.Object var0) { return false; }
	public java.util.Set<java.util.Map.Entry<java.lang.Object,java.lang.Object>> entrySet() { return null; }
	public java.lang.Object get(java.lang.Object var0) { return null; }
	public boolean isEmpty() { return false; }
	public java.util.Set<java.lang.Object> keySet() { return null; }
	public java.lang.Object put(java.lang.Object var0, java.lang.Object var1) { return null; }
	public void putAll(java.util.Map<?,?> var0) { }
	public java.lang.Object remove(java.lang.Object var0) { return null; }
	public int size() { return 0; }
	public java.util.Collection<java.lang.Object> values() { return null; }
}

