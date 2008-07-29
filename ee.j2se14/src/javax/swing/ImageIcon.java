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

package javax.swing;
public class ImageIcon implements java.io.Serializable, javax.accessibility.Accessible, javax.swing.Icon {
	public ImageIcon() { }
	public ImageIcon(java.awt.Image var0) { }
	public ImageIcon(java.awt.Image var0, java.lang.String var1) { }
	public ImageIcon(java.lang.String var0) { }
	public ImageIcon(java.lang.String var0, java.lang.String var1) { }
	public ImageIcon(java.net.URL var0) { }
	public ImageIcon(java.net.URL var0, java.lang.String var1) { }
	public ImageIcon(byte[] var0) { }
	public ImageIcon(byte[] var0, java.lang.String var1) { }
	public javax.accessibility.AccessibleContext getAccessibleContext() { return null; }
	public java.lang.String getDescription() { return null; }
	public int getIconHeight() { return 0; }
	public int getIconWidth() { return 0; }
	public java.awt.Image getImage() { return null; }
	public int getImageLoadStatus() { return 0; }
	public java.awt.image.ImageObserver getImageObserver() { return null; }
	protected void loadImage(java.awt.Image var0) { }
	public void paintIcon(java.awt.Component var0, java.awt.Graphics var1, int var2, int var3) { }
	public void setDescription(java.lang.String var0) { }
	public void setImage(java.awt.Image var0) { }
	public void setImageObserver(java.awt.image.ImageObserver var0) { }
	protected final static java.awt.Component component; static { component = null; }
	protected final static java.awt.MediaTracker tracker; static { tracker = null; }
	protected class AccessibleImageIcon extends javax.accessibility.AccessibleContext implements java.io.Serializable, javax.accessibility.AccessibleIcon {
		protected AccessibleImageIcon() { }
		public javax.accessibility.Accessible getAccessibleChild(int var0) { return null; }
		public int getAccessibleChildrenCount() { return 0; }
		public java.lang.String getAccessibleIconDescription() { return null; }
		public int getAccessibleIconHeight() { return 0; }
		public int getAccessibleIconWidth() { return 0; }
		public int getAccessibleIndexInParent() { return 0; }
		public javax.accessibility.AccessibleRole getAccessibleRole() { return null; }
		public javax.accessibility.AccessibleStateSet getAccessibleStateSet() { return null; }
		public java.util.Locale getLocale() { return null; }
		public void setAccessibleIconDescription(java.lang.String var0) { }
	}
}

