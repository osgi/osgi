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

package javax.imageio.spi;
public abstract class ImageReaderSpi extends javax.imageio.spi.ImageReaderWriterSpi {
	public final static java.lang.Class[] STANDARD_INPUT_TYPE; static { STANDARD_INPUT_TYPE = null; }
	protected java.lang.Class[] inputTypes;
	protected java.lang.String[] writerSpiNames;
	protected ImageReaderSpi() { } 
	public ImageReaderSpi(java.lang.String var0, java.lang.String var1, java.lang.String[] var2, java.lang.String[] var3, java.lang.String[] var4, java.lang.String var5, java.lang.Class[] var6, java.lang.String[] var7, boolean var8, java.lang.String var9, java.lang.String var10, java.lang.String[] var11, java.lang.String[] var12, boolean var13, java.lang.String var14, java.lang.String var15, java.lang.String[] var16, java.lang.String[] var17) { } 
	public abstract boolean canDecodeInput(java.lang.Object var0) throws java.io.IOException;
	public javax.imageio.ImageReader createReaderInstance() throws java.io.IOException { return null; }
	public abstract javax.imageio.ImageReader createReaderInstance(java.lang.Object var0) throws java.io.IOException;
	public java.lang.String[] getImageWriterSpiNames() { return null; }
	public java.lang.Class[] getInputTypes() { return null; }
	public boolean isOwnReader(javax.imageio.ImageReader var0) { return false; }
}

