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
public abstract class Graphics {
	protected Graphics() { } 
	public abstract void clearRect(int var0, int var1, int var2, int var3);
	public abstract void clipRect(int var0, int var1, int var2, int var3);
	public abstract void copyArea(int var0, int var1, int var2, int var3, int var4, int var5);
	public abstract java.awt.Graphics create();
	public java.awt.Graphics create(int var0, int var1, int var2, int var3) { return null; }
	public abstract void dispose();
	public void draw3DRect(int var0, int var1, int var2, int var3, boolean var4) { }
	public abstract void drawArc(int var0, int var1, int var2, int var3, int var4, int var5);
	public void drawBytes(byte[] var0, int var1, int var2, int var3, int var4) { }
	public void drawChars(char[] var0, int var1, int var2, int var3, int var4) { }
	public abstract boolean drawImage(java.awt.Image var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, java.awt.Color var9, java.awt.image.ImageObserver var10);
	public abstract boolean drawImage(java.awt.Image var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, java.awt.image.ImageObserver var9);
	public abstract boolean drawImage(java.awt.Image var0, int var1, int var2, int var3, int var4, java.awt.Color var5, java.awt.image.ImageObserver var6);
	public abstract boolean drawImage(java.awt.Image var0, int var1, int var2, int var3, int var4, java.awt.image.ImageObserver var5);
	public abstract boolean drawImage(java.awt.Image var0, int var1, int var2, java.awt.Color var3, java.awt.image.ImageObserver var4);
	public abstract boolean drawImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3);
	public abstract void drawLine(int var0, int var1, int var2, int var3);
	public abstract void drawOval(int var0, int var1, int var2, int var3);
	public void drawPolygon(java.awt.Polygon var0) { }
	public abstract void drawPolygon(int[] var0, int[] var1, int var2);
	public abstract void drawPolyline(int[] var0, int[] var1, int var2);
	public void drawRect(int var0, int var1, int var2, int var3) { }
	public abstract void drawRoundRect(int var0, int var1, int var2, int var3, int var4, int var5);
	public abstract void drawString(java.lang.String var0, int var1, int var2);
	public abstract void drawString(java.text.AttributedCharacterIterator var0, int var1, int var2);
	public void fill3DRect(int var0, int var1, int var2, int var3, boolean var4) { }
	public abstract void fillArc(int var0, int var1, int var2, int var3, int var4, int var5);
	public abstract void fillOval(int var0, int var1, int var2, int var3);
	public void fillPolygon(java.awt.Polygon var0) { }
	public abstract void fillPolygon(int[] var0, int[] var1, int var2);
	public abstract void fillRect(int var0, int var1, int var2, int var3);
	public abstract void fillRoundRect(int var0, int var1, int var2, int var3, int var4, int var5);
	public void finalize() { }
	public abstract java.awt.Shape getClip();
	public abstract java.awt.Rectangle getClipBounds();
	public java.awt.Rectangle getClipBounds(java.awt.Rectangle var0) { return null; }
	/** @deprecated */ public java.awt.Rectangle getClipRect() { return null; }
	public abstract java.awt.Color getColor();
	public abstract java.awt.Font getFont();
	public java.awt.FontMetrics getFontMetrics() { return null; }
	public abstract java.awt.FontMetrics getFontMetrics(java.awt.Font var0);
	public boolean hitClip(int var0, int var1, int var2, int var3) { return false; }
	public abstract void setClip(int var0, int var1, int var2, int var3);
	public abstract void setClip(java.awt.Shape var0);
	public abstract void setColor(java.awt.Color var0);
	public abstract void setFont(java.awt.Font var0);
	public abstract void setPaintMode();
	public abstract void setXORMode(java.awt.Color var0);
	public abstract void translate(int var0, int var1);
}

