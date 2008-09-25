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

package javax.swing.plaf;
public abstract class FileChooserUI extends javax.swing.plaf.ComponentUI {
	public FileChooserUI() { }
	public abstract void ensureFileIsVisible(javax.swing.JFileChooser var0, java.io.File var1);
	public abstract javax.swing.filechooser.FileFilter getAcceptAllFileFilter(javax.swing.JFileChooser var0);
	public abstract java.lang.String getApproveButtonText(javax.swing.JFileChooser var0);
	public abstract java.lang.String getDialogTitle(javax.swing.JFileChooser var0);
	public abstract javax.swing.filechooser.FileView getFileView(javax.swing.JFileChooser var0);
	public abstract void rescanCurrentDirectory(javax.swing.JFileChooser var0);
}

