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

package javax.imageio;
public abstract class ImageReader {
	protected ImageReader(javax.imageio.spi.ImageReaderSpi var0) { }
	public void abort() { }
	protected boolean abortRequested() { return false; }
	public void addIIOReadProgressListener(javax.imageio.event.IIOReadProgressListener var0) { }
	public void addIIOReadUpdateListener(javax.imageio.event.IIOReadUpdateListener var0) { }
	public void addIIOReadWarningListener(javax.imageio.event.IIOReadWarningListener var0) { }
	public boolean canReadRaster() { return false; }
	protected static void checkReadParamBandSettings(javax.imageio.ImageReadParam var0, int var1, int var2) { }
	protected void clearAbortRequest() { }
	protected static void computeRegions(javax.imageio.ImageReadParam var0, int var1, int var2, java.awt.image.BufferedImage var3, java.awt.Rectangle var4, java.awt.Rectangle var5) { }
	public void dispose() { }
	public float getAspectRatio(int var0) throws java.io.IOException { return 0.0f; }
	public java.util.Locale[] getAvailableLocales() { return null; }
	public javax.imageio.ImageReadParam getDefaultReadParam() { return null; }
	protected static java.awt.image.BufferedImage getDestination(javax.imageio.ImageReadParam var0, java.util.Iterator var1, int var2, int var3) throws javax.imageio.IIOException { return null; }
	public java.lang.String getFormatName() throws java.io.IOException { return null; }
	public abstract int getHeight(int var0) throws java.io.IOException;
	public abstract javax.imageio.metadata.IIOMetadata getImageMetadata(int var0) throws java.io.IOException;
	public javax.imageio.metadata.IIOMetadata getImageMetadata(int var0, java.lang.String var1, java.util.Set var2) throws java.io.IOException { return null; }
	public abstract java.util.Iterator getImageTypes(int var0) throws java.io.IOException;
	public java.lang.Object getInput() { return null; }
	public java.util.Locale getLocale() { return null; }
	public int getMinIndex() { return 0; }
	public abstract int getNumImages(boolean var0) throws java.io.IOException;
	public int getNumThumbnails(int var0) throws java.io.IOException { return 0; }
	public javax.imageio.spi.ImageReaderSpi getOriginatingProvider() { return null; }
	public javax.imageio.ImageTypeSpecifier getRawImageType(int var0) throws java.io.IOException { return null; }
	protected static java.awt.Rectangle getSourceRegion(javax.imageio.ImageReadParam var0, int var1, int var2) { return null; }
	public abstract javax.imageio.metadata.IIOMetadata getStreamMetadata() throws java.io.IOException;
	public javax.imageio.metadata.IIOMetadata getStreamMetadata(java.lang.String var0, java.util.Set var1) throws java.io.IOException { return null; }
	public int getThumbnailHeight(int var0, int var1) throws java.io.IOException { return 0; }
	public int getThumbnailWidth(int var0, int var1) throws java.io.IOException { return 0; }
	public int getTileGridXOffset(int var0) throws java.io.IOException { return 0; }
	public int getTileGridYOffset(int var0) throws java.io.IOException { return 0; }
	public int getTileHeight(int var0) throws java.io.IOException { return 0; }
	public int getTileWidth(int var0) throws java.io.IOException { return 0; }
	public abstract int getWidth(int var0) throws java.io.IOException;
	public boolean hasThumbnails(int var0) throws java.io.IOException { return false; }
	public boolean isIgnoringMetadata() { return false; }
	public boolean isImageTiled(int var0) throws java.io.IOException { return false; }
	public boolean isRandomAccessEasy(int var0) throws java.io.IOException { return false; }
	public boolean isSeekForwardOnly() { return false; }
	protected void processImageComplete() { }
	protected void processImageProgress(float var0) { }
	protected void processImageStarted(int var0) { }
	protected void processImageUpdate(java.awt.image.BufferedImage var0, int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) { }
	protected void processPassComplete(java.awt.image.BufferedImage var0) { }
	protected void processPassStarted(java.awt.image.BufferedImage var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int[] var8) { }
	protected void processReadAborted() { }
	protected void processSequenceComplete() { }
	protected void processSequenceStarted(int var0) { }
	protected void processThumbnailComplete() { }
	protected void processThumbnailPassComplete(java.awt.image.BufferedImage var0) { }
	protected void processThumbnailPassStarted(java.awt.image.BufferedImage var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int[] var8) { }
	protected void processThumbnailProgress(float var0) { }
	protected void processThumbnailStarted(int var0, int var1) { }
	protected void processThumbnailUpdate(java.awt.image.BufferedImage var0, int var1, int var2, int var3, int var4, int var5, int var6, int[] var7) { }
	protected void processWarningOccurred(java.lang.String var0) { }
	protected void processWarningOccurred(java.lang.String var0, java.lang.String var1) { }
	public java.awt.image.BufferedImage read(int var0) throws java.io.IOException { return null; }
	public abstract java.awt.image.BufferedImage read(int var0, javax.imageio.ImageReadParam var1) throws java.io.IOException;
	public javax.imageio.IIOImage readAll(int var0, javax.imageio.ImageReadParam var1) throws java.io.IOException { return null; }
	public java.util.Iterator readAll(java.util.Iterator var0) throws java.io.IOException { return null; }
	public java.awt.image.RenderedImage readAsRenderedImage(int var0, javax.imageio.ImageReadParam var1) throws java.io.IOException { return null; }
	public java.awt.image.Raster readRaster(int var0, javax.imageio.ImageReadParam var1) throws java.io.IOException { return null; }
	public java.awt.image.BufferedImage readThumbnail(int var0, int var1) throws java.io.IOException { return null; }
	public java.awt.image.BufferedImage readTile(int var0, int var1, int var2) throws java.io.IOException { return null; }
	public java.awt.image.Raster readTileRaster(int var0, int var1, int var2) throws java.io.IOException { return null; }
	public boolean readerSupportsThumbnails() { return false; }
	public void removeAllIIOReadProgressListeners() { }
	public void removeAllIIOReadUpdateListeners() { }
	public void removeAllIIOReadWarningListeners() { }
	public void removeIIOReadProgressListener(javax.imageio.event.IIOReadProgressListener var0) { }
	public void removeIIOReadUpdateListener(javax.imageio.event.IIOReadUpdateListener var0) { }
	public void removeIIOReadWarningListener(javax.imageio.event.IIOReadWarningListener var0) { }
	public void reset() { }
	public void setInput(java.lang.Object var0) { }
	public void setInput(java.lang.Object var0, boolean var1) { }
	public void setInput(java.lang.Object var0, boolean var1, boolean var2) { }
	public void setLocale(java.util.Locale var0) { }
	protected java.util.Locale[] availableLocales;
	protected boolean ignoreMetadata;
	protected java.lang.Object input;
	protected java.util.Locale locale;
	protected int minIndex;
	protected javax.imageio.spi.ImageReaderSpi originatingProvider;
	protected java.util.List progressListeners;
	protected boolean seekForwardOnly;
	protected java.util.List updateListeners;
	protected java.util.List warningListeners;
	protected java.util.List warningLocales;
}

