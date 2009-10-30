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

package java.awt;
public class FileDialog extends java.awt.Dialog {
	public final static int LOAD = 0;
	public final static int SAVE = 1;
	public FileDialog(java.awt.Dialog var0)  { super((java.awt.Frame) null, false); } 
	public FileDialog(java.awt.Dialog var0, java.lang.String var1)  { super((java.awt.Frame) null, false); } 
	public FileDialog(java.awt.Dialog var0, java.lang.String var1, int var2)  { super((java.awt.Frame) null, false); } 
	public FileDialog(java.awt.Frame var0)  { super((java.awt.Frame) null, false); } 
	public FileDialog(java.awt.Frame var0, java.lang.String var1)  { super((java.awt.Frame) null, false); } 
	public FileDialog(java.awt.Frame var0, java.lang.String var1, int var2)  { super((java.awt.Frame) null, false); } 
	public java.lang.String getDirectory() { return null; }
	public java.lang.String getFile() { return null; }
	public java.io.FilenameFilter getFilenameFilter() { return null; }
	public int getMode() { return 0; }
	public void setDirectory(java.lang.String var0) { }
	public void setFile(java.lang.String var0) { }
	public void setFilenameFilter(java.io.FilenameFilter var0) { }
	public void setMode(int var0) { }
}

