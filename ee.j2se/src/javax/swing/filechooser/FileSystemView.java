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

package javax.swing.filechooser;
public abstract class FileSystemView {
	public FileSystemView() { }
	public java.io.File createFileObject(java.io.File var0, java.lang.String var1) { return null; }
	public java.io.File createFileObject(java.lang.String var0) { return null; }
	protected java.io.File createFileSystemRoot(java.io.File var0) { return null; }
	public abstract java.io.File createNewFolder(java.io.File var0) throws java.io.IOException;
	public java.io.File getChild(java.io.File var0, java.lang.String var1) { return null; }
	public java.io.File getDefaultDirectory() { return null; }
	public static javax.swing.filechooser.FileSystemView getFileSystemView() { return null; }
	public java.io.File[] getFiles(java.io.File var0, boolean var1) { return null; }
	public java.io.File getHomeDirectory() { return null; }
	public java.io.File getParentDirectory(java.io.File var0) { return null; }
	public java.io.File[] getRoots() { return null; }
	public java.lang.String getSystemDisplayName(java.io.File var0) { return null; }
	public javax.swing.Icon getSystemIcon(java.io.File var0) { return null; }
	public java.lang.String getSystemTypeDescription(java.io.File var0) { return null; }
	public boolean isComputerNode(java.io.File var0) { return false; }
	public boolean isDrive(java.io.File var0) { return false; }
	public boolean isFileSystem(java.io.File var0) { return false; }
	public boolean isFileSystemRoot(java.io.File var0) { return false; }
	public boolean isFloppyDrive(java.io.File var0) { return false; }
	public boolean isHiddenFile(java.io.File var0) { return false; }
	public boolean isParent(java.io.File var0, java.io.File var1) { return false; }
	public boolean isRoot(java.io.File var0) { return false; }
	public java.lang.Boolean isTraversable(java.io.File var0) { return null; }
}

