/*
 * $Date$
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

package javax.swing;
public class DebugGraphics extends java.awt.Graphics {
	public DebugGraphics() { }
	public DebugGraphics(java.awt.Graphics var0) { }
	public DebugGraphics(java.awt.Graphics var0, javax.swing.JComponent var1) { }
	public void clearRect(int var0, int var1, int var2, int var3) { }
	public void clipRect(int var0, int var1, int var2, int var3) { }
	public void copyArea(int var0, int var1, int var2, int var3, int var4, int var5) { }
	public java.awt.Graphics create() { return null; }
	public void dispose() { }
	public void drawArc(int var0, int var1, int var2, int var3, int var4, int var5) { }
	public boolean drawImage(java.awt.Image var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, java.awt.Color var9, java.awt.image.ImageObserver var10) { return false; }
	public boolean drawImage(java.awt.Image var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, java.awt.image.ImageObserver var9) { return false; }
	public boolean drawImage(java.awt.Image var0, int var1, int var2, int var3, int var4, java.awt.Color var5, java.awt.image.ImageObserver var6) { return false; }
	public boolean drawImage(java.awt.Image var0, int var1, int var2, int var3, int var4, java.awt.image.ImageObserver var5) { return false; }
	public boolean drawImage(java.awt.Image var0, int var1, int var2, java.awt.Color var3, java.awt.image.ImageObserver var4) { return false; }
	public boolean drawImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3) { return false; }
	public void drawLine(int var0, int var1, int var2, int var3) { }
	public void drawOval(int var0, int var1, int var2, int var3) { }
	public void drawPolygon(int[] var0, int[] var1, int var2) { }
	public void drawPolyline(int[] var0, int[] var1, int var2) { }
	public void drawRoundRect(int var0, int var1, int var2, int var3, int var4, int var5) { }
	public void drawString(java.lang.String var0, int var1, int var2) { }
	public void drawString(java.text.AttributedCharacterIterator var0, int var1, int var2) { }
	public void fillArc(int var0, int var1, int var2, int var3, int var4, int var5) { }
	public void fillOval(int var0, int var1, int var2, int var3) { }
	public void fillPolygon(int[] var0, int[] var1, int var2) { }
	public void fillRect(int var0, int var1, int var2, int var3) { }
	public void fillRoundRect(int var0, int var1, int var2, int var3, int var4, int var5) { }
	public static java.awt.Color flashColor() { return null; }
	public static int flashCount() { return 0; }
	public static int flashTime() { return 0; }
	public java.awt.Shape getClip() { return null; }
	public java.awt.Rectangle getClipBounds() { return null; }
	public java.awt.Color getColor() { return null; }
	public int getDebugOptions() { return 0; }
	public java.awt.Font getFont() { return null; }
	public java.awt.FontMetrics getFontMetrics(java.awt.Font var0) { return null; }
	public boolean isDrawingBuffer() { return false; }
	public static java.io.PrintStream logStream() { return null; }
	public void setClip(int var0, int var1, int var2, int var3) { }
	public void setClip(java.awt.Shape var0) { }
	public void setColor(java.awt.Color var0) { }
	public void setDebugOptions(int var0) { }
	public static void setFlashColor(java.awt.Color var0) { }
	public static void setFlashCount(int var0) { }
	public static void setFlashTime(int var0) { }
	public void setFont(java.awt.Font var0) { }
	public static void setLogStream(java.io.PrintStream var0) { }
	public void setPaintMode() { }
	public void setXORMode(java.awt.Color var0) { }
	public void translate(int var0, int var1) { }
	public final static int BUFFERED_OPTION = 4;
	public final static int FLASH_OPTION = 2;
	public final static int LOG_OPTION = 1;
	public final static int NONE_OPTION = -1;
}

