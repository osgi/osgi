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

package java.util.zip;
public class ZipEntry implements java.lang.Cloneable, java.util.zip.ZipConstants {
	public ZipEntry(java.lang.String var0) { }
	public ZipEntry(java.util.zip.ZipEntry var0) { }
	public java.lang.Object clone() { return null; }
	public java.lang.String getComment() { return null; }
	public long getCompressedSize() { return 0l; }
	public long getCrc() { return 0l; }
	public byte[] getExtra() { return null; }
	public int getMethod() { return 0; }
	public java.lang.String getName() { return null; }
	public long getSize() { return 0l; }
	public long getTime() { return 0l; }
	public int hashCode() { return 0; }
	public boolean isDirectory() { return false; }
	public void setComment(java.lang.String var0) { }
	public void setCompressedSize(long var0) { }
	public void setCrc(long var0) { }
	public void setExtra(byte[] var0) { }
	public void setMethod(int var0) { }
	public void setSize(long var0) { }
	public void setTime(long var0) { }
	public final static int DEFLATED = 8;
	public final static int STORED = 0;
}

