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

package java.awt.peer;
public abstract interface ComponentPeer {
	public abstract boolean canDetermineObscurity();
	public abstract int checkImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3);
	public abstract void coalescePaintEvent(java.awt.event.PaintEvent var0);
	public abstract void createBuffers(int var0, java.awt.BufferCapabilities var1) throws java.awt.AWTException;
	public abstract java.awt.Image createImage(int var0, int var1);
	public abstract java.awt.Image createImage(java.awt.image.ImageProducer var0);
	public abstract java.awt.image.VolatileImage createVolatileImage(int var0, int var1);
	public abstract void destroyBuffers();
	public abstract void disable();
	public abstract void dispose();
	public abstract void enable();
	public abstract void flip(java.awt.BufferCapabilities.FlipContents var0);
	public abstract java.awt.Image getBackBuffer();
	public abstract java.awt.image.ColorModel getColorModel();
	public abstract java.awt.FontMetrics getFontMetrics(java.awt.Font var0);
	public abstract java.awt.Graphics getGraphics();
	public abstract java.awt.GraphicsConfiguration getGraphicsConfiguration();
	public abstract java.awt.Point getLocationOnScreen();
	public abstract java.awt.Dimension getMinimumSize();
	public abstract java.awt.Dimension getPreferredSize();
	public abstract java.awt.Toolkit getToolkit();
	public abstract void handleEvent(java.awt.AWTEvent var0);
	public abstract boolean handlesWheelScrolling();
	public abstract void hide();
	public abstract boolean isFocusable();
	public abstract boolean isObscured();
	public abstract java.awt.Dimension minimumSize();
	public abstract void paint(java.awt.Graphics var0);
	public abstract java.awt.Dimension preferredSize();
	public abstract boolean prepareImage(java.awt.Image var0, int var1, int var2, java.awt.image.ImageObserver var3);
	public abstract void print(java.awt.Graphics var0);
	public abstract void repaint(long var0, int var1, int var2, int var3, int var4);
	public abstract boolean requestFocus(java.awt.Component var0, boolean var1, boolean var2, long var3);
	public abstract void reshape(int var0, int var1, int var2, int var3);
	public abstract void setBackground(java.awt.Color var0);
	public abstract void setBounds(int var0, int var1, int var2, int var3);
	public abstract void setEnabled(boolean var0);
	public abstract void setFont(java.awt.Font var0);
	public abstract void setForeground(java.awt.Color var0);
	public abstract void setVisible(boolean var0);
	public abstract void show();
	public abstract void updateCursorImmediately();
}

