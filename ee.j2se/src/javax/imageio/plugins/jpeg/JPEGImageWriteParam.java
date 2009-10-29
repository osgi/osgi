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

package javax.imageio.plugins.jpeg;
public class JPEGImageWriteParam extends javax.imageio.ImageWriteParam {
	public JPEGImageWriteParam(java.util.Locale var0) { }
	public boolean areTablesSet() { return false; }
	public javax.imageio.plugins.jpeg.JPEGHuffmanTable[] getACHuffmanTables() { return null; }
	public javax.imageio.plugins.jpeg.JPEGHuffmanTable[] getDCHuffmanTables() { return null; }
	public boolean getOptimizeHuffmanTables() { return false; }
	public javax.imageio.plugins.jpeg.JPEGQTable[] getQTables() { return null; }
	public void setEncodeTables(javax.imageio.plugins.jpeg.JPEGQTable[] var0, javax.imageio.plugins.jpeg.JPEGHuffmanTable[] var1, javax.imageio.plugins.jpeg.JPEGHuffmanTable[] var2) { }
	public void setOptimizeHuffmanTables(boolean var0) { }
	public void unsetEncodeTables() { }
}

