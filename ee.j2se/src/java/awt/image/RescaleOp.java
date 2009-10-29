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

package java.awt.image;
public class RescaleOp implements java.awt.image.BufferedImageOp, java.awt.image.RasterOp {
	public RescaleOp(float var0, float var1, java.awt.RenderingHints var2) { }
	public RescaleOp(float[] var0, float[] var1, java.awt.RenderingHints var2) { }
	public java.awt.image.BufferedImage createCompatibleDestImage(java.awt.image.BufferedImage var0, java.awt.image.ColorModel var1) { return null; }
	public java.awt.image.WritableRaster createCompatibleDestRaster(java.awt.image.Raster var0) { return null; }
	public final java.awt.image.BufferedImage filter(java.awt.image.BufferedImage var0, java.awt.image.BufferedImage var1) { return null; }
	public final java.awt.image.WritableRaster filter(java.awt.image.Raster var0, java.awt.image.WritableRaster var1) { return null; }
	public final java.awt.geom.Rectangle2D getBounds2D(java.awt.image.BufferedImage var0) { return null; }
	public final java.awt.geom.Rectangle2D getBounds2D(java.awt.image.Raster var0) { return null; }
	public final int getNumFactors() { return 0; }
	public final float[] getOffsets(float[] var0) { return null; }
	public final java.awt.geom.Point2D getPoint2D(java.awt.geom.Point2D var0, java.awt.geom.Point2D var1) { return null; }
	public final java.awt.RenderingHints getRenderingHints() { return null; }
	public final float[] getScaleFactors(float[] var0) { return null; }
}

