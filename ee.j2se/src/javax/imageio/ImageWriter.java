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
public abstract class ImageWriter implements javax.imageio.ImageTranscoder {
	protected ImageWriter(javax.imageio.spi.ImageWriterSpi var0) { }
	public void abort() { }
	protected boolean abortRequested() { return false; }
	public void addIIOWriteProgressListener(javax.imageio.event.IIOWriteProgressListener var0) { }
	public void addIIOWriteWarningListener(javax.imageio.event.IIOWriteWarningListener var0) { }
	public boolean canInsertEmpty(int var0) throws java.io.IOException { return false; }
	public boolean canInsertImage(int var0) throws java.io.IOException { return false; }
	public boolean canRemoveImage(int var0) throws java.io.IOException { return false; }
	public boolean canReplaceImageMetadata(int var0) throws java.io.IOException { return false; }
	public boolean canReplacePixels(int var0) throws java.io.IOException { return false; }
	public boolean canReplaceStreamMetadata() throws java.io.IOException { return false; }
	public boolean canWriteEmpty() throws java.io.IOException { return false; }
	public boolean canWriteRasters() { return false; }
	public boolean canWriteSequence() { return false; }
	protected void clearAbortRequest() { }
	public void dispose() { }
	public void endInsertEmpty() throws java.io.IOException { }
	public void endReplacePixels() throws java.io.IOException { }
	public void endWriteEmpty() throws java.io.IOException { }
	public void endWriteSequence() throws java.io.IOException { }
	public java.util.Locale[] getAvailableLocales() { return null; }
	public abstract javax.imageio.metadata.IIOMetadata getDefaultImageMetadata(javax.imageio.ImageTypeSpecifier var0, javax.imageio.ImageWriteParam var1);
	public abstract javax.imageio.metadata.IIOMetadata getDefaultStreamMetadata(javax.imageio.ImageWriteParam var0);
	public javax.imageio.ImageWriteParam getDefaultWriteParam() { return null; }
	public java.util.Locale getLocale() { return null; }
	public int getNumThumbnailsSupported(javax.imageio.ImageTypeSpecifier var0, javax.imageio.ImageWriteParam var1, javax.imageio.metadata.IIOMetadata var2, javax.imageio.metadata.IIOMetadata var3) { return 0; }
	public javax.imageio.spi.ImageWriterSpi getOriginatingProvider() { return null; }
	public java.lang.Object getOutput() { return null; }
	public java.awt.Dimension[] getPreferredThumbnailSizes(javax.imageio.ImageTypeSpecifier var0, javax.imageio.ImageWriteParam var1, javax.imageio.metadata.IIOMetadata var2, javax.imageio.metadata.IIOMetadata var3) { return null; }
	public void prepareInsertEmpty(int var0, javax.imageio.ImageTypeSpecifier var1, int var2, int var3, javax.imageio.metadata.IIOMetadata var4, java.util.List var5, javax.imageio.ImageWriteParam var6) throws java.io.IOException { }
	public void prepareReplacePixels(int var0, java.awt.Rectangle var1) throws java.io.IOException { }
	public void prepareWriteEmpty(javax.imageio.metadata.IIOMetadata var0, javax.imageio.ImageTypeSpecifier var1, int var2, int var3, javax.imageio.metadata.IIOMetadata var4, java.util.List var5, javax.imageio.ImageWriteParam var6) throws java.io.IOException { }
	public void prepareWriteSequence(javax.imageio.metadata.IIOMetadata var0) throws java.io.IOException { }
	protected void processImageComplete() { }
	protected void processImageProgress(float var0) { }
	protected void processImageStarted(int var0) { }
	protected void processThumbnailComplete() { }
	protected void processThumbnailProgress(float var0) { }
	protected void processThumbnailStarted(int var0, int var1) { }
	protected void processWarningOccurred(int var0, java.lang.String var1) { }
	protected void processWarningOccurred(int var0, java.lang.String var1, java.lang.String var2) { }
	protected void processWriteAborted() { }
	public void removeAllIIOWriteProgressListeners() { }
	public void removeAllIIOWriteWarningListeners() { }
	public void removeIIOWriteProgressListener(javax.imageio.event.IIOWriteProgressListener var0) { }
	public void removeIIOWriteWarningListener(javax.imageio.event.IIOWriteWarningListener var0) { }
	public void removeImage(int var0) throws java.io.IOException { }
	public void replaceImageMetadata(int var0, javax.imageio.metadata.IIOMetadata var1) throws java.io.IOException { }
	public void replacePixels(java.awt.image.Raster var0, javax.imageio.ImageWriteParam var1) throws java.io.IOException { }
	public void replacePixels(java.awt.image.RenderedImage var0, javax.imageio.ImageWriteParam var1) throws java.io.IOException { }
	public void replaceStreamMetadata(javax.imageio.metadata.IIOMetadata var0) throws java.io.IOException { }
	public void reset() { }
	public void setLocale(java.util.Locale var0) { }
	public void setOutput(java.lang.Object var0) { }
	public void write(java.awt.image.RenderedImage var0) throws java.io.IOException { }
	public void write(javax.imageio.IIOImage var0) throws java.io.IOException { }
	public abstract void write(javax.imageio.metadata.IIOMetadata var0, javax.imageio.IIOImage var1, javax.imageio.ImageWriteParam var2) throws java.io.IOException;
	public void writeInsert(int var0, javax.imageio.IIOImage var1, javax.imageio.ImageWriteParam var2) throws java.io.IOException { }
	public void writeToSequence(javax.imageio.IIOImage var0, javax.imageio.ImageWriteParam var1) throws java.io.IOException { }
	protected java.util.Locale[] availableLocales;
	protected java.util.Locale locale;
	protected javax.imageio.spi.ImageWriterSpi originatingProvider;
	protected java.lang.Object output;
	protected java.util.List progressListeners;
	protected java.util.List warningListeners;
	protected java.util.List warningLocales;
}

