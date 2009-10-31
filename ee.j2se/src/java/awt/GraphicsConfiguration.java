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
public abstract class GraphicsConfiguration {
	protected GraphicsConfiguration() { } 
	public abstract java.awt.image.BufferedImage createCompatibleImage(int var0, int var1);
	public java.awt.image.BufferedImage createCompatibleImage(int var0, int var1, int var2) { return null; }
	public java.awt.image.VolatileImage createCompatibleVolatileImage(int var0, int var1) { return null; }
	public java.awt.image.VolatileImage createCompatibleVolatileImage(int var0, int var1, int var2) { return null; }
	public java.awt.image.VolatileImage createCompatibleVolatileImage(int var0, int var1, java.awt.ImageCapabilities var2) throws java.awt.AWTException { return null; }
	public java.awt.image.VolatileImage createCompatibleVolatileImage(int var0, int var1, java.awt.ImageCapabilities var2, int var3) throws java.awt.AWTException { return null; }
	public abstract java.awt.Rectangle getBounds();
	public java.awt.BufferCapabilities getBufferCapabilities() { return null; }
	public abstract java.awt.image.ColorModel getColorModel();
	public abstract java.awt.image.ColorModel getColorModel(int var0);
	public abstract java.awt.geom.AffineTransform getDefaultTransform();
	public abstract java.awt.GraphicsDevice getDevice();
	public java.awt.ImageCapabilities getImageCapabilities() { return null; }
	public abstract java.awt.geom.AffineTransform getNormalizingTransform();
}

