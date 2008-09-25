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

package javax.imageio.spi;
public abstract class ImageReaderWriterSpi extends javax.imageio.spi.IIOServiceProvider {
	public ImageReaderWriterSpi() { }
	public ImageReaderWriterSpi(java.lang.String var0, java.lang.String var1, java.lang.String[] var2, java.lang.String[] var3, java.lang.String[] var4, java.lang.String var5, boolean var6, java.lang.String var7, java.lang.String var8, java.lang.String[] var9, java.lang.String[] var10, boolean var11, java.lang.String var12, java.lang.String var13, java.lang.String[] var14, java.lang.String[] var15) { }
	public java.lang.String[] getExtraImageMetadataFormatNames() { return null; }
	public java.lang.String[] getExtraStreamMetadataFormatNames() { return null; }
	public java.lang.String[] getFileSuffixes() { return null; }
	public java.lang.String[] getFormatNames() { return null; }
	public javax.imageio.metadata.IIOMetadataFormat getImageMetadataFormat(java.lang.String var0) { return null; }
	public java.lang.String[] getMIMETypes() { return null; }
	public java.lang.String getNativeImageMetadataFormatName() { return null; }
	public java.lang.String getNativeStreamMetadataFormatName() { return null; }
	public java.lang.String getPluginClassName() { return null; }
	public javax.imageio.metadata.IIOMetadataFormat getStreamMetadataFormat(java.lang.String var0) { return null; }
	public boolean isStandardImageMetadataFormatSupported() { return false; }
	public boolean isStandardStreamMetadataFormatSupported() { return false; }
	protected java.lang.String[] MIMETypes;
	protected java.lang.String[] extraImageMetadataFormatClassNames;
	protected java.lang.String[] extraImageMetadataFormatNames;
	protected java.lang.String[] extraStreamMetadataFormatClassNames;
	protected java.lang.String[] extraStreamMetadataFormatNames;
	protected java.lang.String[] names;
	protected java.lang.String nativeImageMetadataFormatClassName;
	protected java.lang.String nativeImageMetadataFormatName;
	protected java.lang.String nativeStreamMetadataFormatClassName;
	protected java.lang.String nativeStreamMetadataFormatName;
	protected java.lang.String pluginClassName;
	protected java.lang.String[] suffixes;
	protected boolean supportsStandardImageMetadataFormat;
	protected boolean supportsStandardStreamMetadataFormat;
}

