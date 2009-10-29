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

package javax.imageio.spi;
public abstract class ImageWriterSpi extends javax.imageio.spi.ImageReaderWriterSpi {
	protected ImageWriterSpi() { }
	public ImageWriterSpi(java.lang.String var0, java.lang.String var1, java.lang.String[] var2, java.lang.String[] var3, java.lang.String[] var4, java.lang.String var5, java.lang.Class[] var6, java.lang.String[] var7, boolean var8, java.lang.String var9, java.lang.String var10, java.lang.String[] var11, java.lang.String[] var12, boolean var13, java.lang.String var14, java.lang.String var15, java.lang.String[] var16, java.lang.String[] var17) { }
	public boolean canEncodeImage(java.awt.image.RenderedImage var0) { return false; }
	public abstract boolean canEncodeImage(javax.imageio.ImageTypeSpecifier var0);
	public javax.imageio.ImageWriter createWriterInstance() throws java.io.IOException { return null; }
	public abstract javax.imageio.ImageWriter createWriterInstance(java.lang.Object var0) throws java.io.IOException;
	public java.lang.String[] getImageReaderSpiNames() { return null; }
	public java.lang.Class[] getOutputTypes() { return null; }
	public boolean isFormatLossless() { return false; }
	public boolean isOwnWriter(javax.imageio.ImageWriter var0) { return false; }
	public final static java.lang.Class[] STANDARD_OUTPUT_TYPE; static { STANDARD_OUTPUT_TYPE = null; }
	protected java.lang.Class[] outputTypes;
	protected java.lang.String[] readerSpiNames;
}

