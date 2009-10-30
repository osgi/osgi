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

package javax.imageio;
public final class ImageIO {
	public static javax.imageio.stream.ImageInputStream createImageInputStream(java.lang.Object var0) throws java.io.IOException { return null; }
	public static javax.imageio.stream.ImageOutputStream createImageOutputStream(java.lang.Object var0) throws java.io.IOException { return null; }
	public static java.io.File getCacheDirectory() { return null; }
	public static javax.imageio.ImageReader getImageReader(javax.imageio.ImageWriter var0) { return null; }
	public static java.util.Iterator<javax.imageio.ImageReader> getImageReaders(java.lang.Object var0) { return null; }
	public static java.util.Iterator<javax.imageio.ImageReader> getImageReadersByFormatName(java.lang.String var0) { return null; }
	public static java.util.Iterator<javax.imageio.ImageReader> getImageReadersByMIMEType(java.lang.String var0) { return null; }
	public static java.util.Iterator<javax.imageio.ImageReader> getImageReadersBySuffix(java.lang.String var0) { return null; }
	public static java.util.Iterator<javax.imageio.ImageTranscoder> getImageTranscoders(javax.imageio.ImageReader var0, javax.imageio.ImageWriter var1) { return null; }
	public static javax.imageio.ImageWriter getImageWriter(javax.imageio.ImageReader var0) { return null; }
	public static java.util.Iterator<javax.imageio.ImageWriter> getImageWriters(javax.imageio.ImageTypeSpecifier var0, java.lang.String var1) { return null; }
	public static java.util.Iterator<javax.imageio.ImageWriter> getImageWritersByFormatName(java.lang.String var0) { return null; }
	public static java.util.Iterator<javax.imageio.ImageWriter> getImageWritersByMIMEType(java.lang.String var0) { return null; }
	public static java.util.Iterator<javax.imageio.ImageWriter> getImageWritersBySuffix(java.lang.String var0) { return null; }
	public static java.lang.String[] getReaderFormatNames() { return null; }
	public static java.lang.String[] getReaderMIMETypes() { return null; }
	public static boolean getUseCache() { return false; }
	public static java.lang.String[] getWriterFormatNames() { return null; }
	public static java.lang.String[] getWriterMIMETypes() { return null; }
	public static java.awt.image.BufferedImage read(java.io.File var0) throws java.io.IOException { return null; }
	public static java.awt.image.BufferedImage read(java.io.InputStream var0) throws java.io.IOException { return null; }
	public static java.awt.image.BufferedImage read(java.net.URL var0) throws java.io.IOException { return null; }
	public static java.awt.image.BufferedImage read(javax.imageio.stream.ImageInputStream var0) throws java.io.IOException { return null; }
	public static void scanForPlugins() { }
	public static void setCacheDirectory(java.io.File var0) { }
	public static void setUseCache(boolean var0) { }
	public static boolean write(java.awt.image.RenderedImage var0, java.lang.String var1, java.io.File var2) throws java.io.IOException { return false; }
	public static boolean write(java.awt.image.RenderedImage var0, java.lang.String var1, java.io.OutputStream var2) throws java.io.IOException { return false; }
	public static boolean write(java.awt.image.RenderedImage var0, java.lang.String var1, javax.imageio.stream.ImageOutputStream var2) throws java.io.IOException { return false; }
	private ImageIO() { } /* generated constructor to prevent compiler adding default public constructor */
}

