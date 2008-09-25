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
public abstract class ImageOutputStreamSpi extends javax.imageio.spi.IIOServiceProvider {
	protected ImageOutputStreamSpi() { }
	public ImageOutputStreamSpi(java.lang.String var0, java.lang.String var1, java.lang.Class var2) { }
	public boolean canUseCacheFile() { return false; }
	public javax.imageio.stream.ImageOutputStream createOutputStreamInstance(java.lang.Object var0) throws java.io.IOException { return null; }
	public abstract javax.imageio.stream.ImageOutputStream createOutputStreamInstance(java.lang.Object var0, boolean var1, java.io.File var2) throws java.io.IOException;
	public java.lang.Class getOutputClass() { return null; }
	public boolean needsCacheFile() { return false; }
	protected java.lang.Class outputClass;
}

