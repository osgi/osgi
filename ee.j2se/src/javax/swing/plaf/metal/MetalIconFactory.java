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

package javax.swing.plaf.metal;
public class MetalIconFactory implements java.io.Serializable {
	public MetalIconFactory() { }
	public static javax.swing.Icon getCheckBoxIcon() { return null; }
	public static javax.swing.Icon getCheckBoxMenuItemIcon() { return null; }
	public static javax.swing.Icon getFileChooserDetailViewIcon() { return null; }
	public static javax.swing.Icon getFileChooserHomeFolderIcon() { return null; }
	public static javax.swing.Icon getFileChooserListViewIcon() { return null; }
	public static javax.swing.Icon getFileChooserNewFolderIcon() { return null; }
	public static javax.swing.Icon getFileChooserUpFolderIcon() { return null; }
	public static javax.swing.Icon getHorizontalSliderThumbIcon() { return null; }
	public static javax.swing.Icon getInternalFrameAltMaximizeIcon(int var0) { return null; }
	public static javax.swing.Icon getInternalFrameCloseIcon(int var0) { return null; }
	public static javax.swing.Icon getInternalFrameDefaultMenuIcon() { return null; }
	public static javax.swing.Icon getInternalFrameMaximizeIcon(int var0) { return null; }
	public static javax.swing.Icon getInternalFrameMinimizeIcon(int var0) { return null; }
	public static javax.swing.Icon getMenuArrowIcon() { return null; }
	public static javax.swing.Icon getMenuItemArrowIcon() { return null; }
	public static javax.swing.Icon getMenuItemCheckIcon() { return null; }
	public static javax.swing.Icon getRadioButtonIcon() { return null; }
	public static javax.swing.Icon getRadioButtonMenuItemIcon() { return null; }
	public static javax.swing.Icon getTreeComputerIcon() { return null; }
	public static javax.swing.Icon getTreeControlIcon(boolean var0) { return null; }
	public static javax.swing.Icon getTreeFloppyDriveIcon() { return null; }
	public static javax.swing.Icon getTreeFolderIcon() { return null; }
	public static javax.swing.Icon getTreeHardDriveIcon() { return null; }
	public static javax.swing.Icon getTreeLeafIcon() { return null; }
	public static javax.swing.Icon getVerticalSliderThumbIcon() { return null; }
	public final static boolean DARK = false;
	public final static boolean LIGHT = true;
	public static class FileIcon16 implements java.io.Serializable, javax.swing.Icon {
		public FileIcon16() { }
		public int getAdditionalHeight() { return 0; }
		public int getIconHeight() { return 0; }
		public int getIconWidth() { return 0; }
		public int getShift() { return 0; }
		public void paintIcon(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3) { }
	}
	public static class FolderIcon16 implements java.io.Serializable, javax.swing.Icon {
		public FolderIcon16() { }
		public int getAdditionalHeight() { return 0; }
		public int getIconHeight() { return 0; }
		public int getIconWidth() { return 0; }
		public int getShift() { return 0; }
		public void paintIcon(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3) { }
	}
	public static class PaletteCloseIcon implements java.io.Serializable, javax.swing.Icon, javax.swing.plaf.UIResource {
		public PaletteCloseIcon() { }
		public int getIconHeight() { return 0; }
		public int getIconWidth() { return 0; }
		public void paintIcon(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3) { }
	}
	public static class TreeControlIcon implements java.io.Serializable, javax.swing.Icon {
		public TreeControlIcon(boolean var0) { }
		public int getIconHeight() { return 0; }
		public int getIconWidth() { return 0; }
		public void paintIcon(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3) { }
		public void paintMe(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3) { }
		protected boolean isLight;
	}
	public static class TreeFolderIcon extends javax.swing.plaf.metal.MetalIconFactory.FolderIcon16 {
		public TreeFolderIcon() { }
	}
	public static class TreeLeafIcon extends javax.swing.plaf.metal.MetalIconFactory.FileIcon16 {
		public TreeLeafIcon() { }
	}
}

