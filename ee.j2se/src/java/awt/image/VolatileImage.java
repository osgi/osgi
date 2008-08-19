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

package java.awt.image;
public abstract class VolatileImage extends java.awt.Image {
	public VolatileImage() { }
	public abstract boolean contentsLost();
	public abstract java.awt.Graphics2D createGraphics();
	public void flush() { }
	public abstract java.awt.ImageCapabilities getCapabilities();
	public java.awt.Graphics getGraphics() { return null; }
	public abstract int getHeight();
	public abstract java.awt.image.BufferedImage getSnapshot();
	public java.awt.image.ImageProducer getSource() { return null; }
	public abstract int getWidth();
	public abstract int validate(java.awt.GraphicsConfiguration var0);
	public final static int IMAGE_INCOMPATIBLE = 2;
	public final static int IMAGE_OK = 0;
	public final static int IMAGE_RESTORED = 1;
}

