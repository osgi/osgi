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

package javax.swing;
public class ProgressMonitor implements javax.accessibility.Accessible {
	protected class AccessibleProgressMonitor extends javax.accessibility.AccessibleContext implements java.beans.PropertyChangeListener, javax.accessibility.AccessibleText, javax.swing.event.ChangeListener {
		protected AccessibleProgressMonitor() { } 
		public javax.accessibility.Accessible getAccessibleChild(int var0) { return null; }
		public int getAccessibleChildrenCount() { return 0; }
		public int getAccessibleIndexInParent() { return 0; }
		public javax.accessibility.AccessibleRole getAccessibleRole() { return null; }
		public javax.accessibility.AccessibleStateSet getAccessibleStateSet() { return null; }
		public java.lang.String getAfterIndex(int var0, int var1) { return null; }
		public java.lang.String getAtIndex(int var0, int var1) { return null; }
		public java.lang.String getBeforeIndex(int var0, int var1) { return null; }
		public int getCaretPosition() { return 0; }
		public int getCharCount() { return 0; }
		public javax.swing.text.AttributeSet getCharacterAttribute(int var0) { return null; }
		public java.awt.Rectangle getCharacterBounds(int var0) { return null; }
		public int getIndexAtPoint(java.awt.Point var0) { return 0; }
		public java.util.Locale getLocale() { return null; }
		public java.lang.String getSelectedText() { return null; }
		public int getSelectionEnd() { return 0; }
		public int getSelectionStart() { return 0; }
		public void propertyChange(java.beans.PropertyChangeEvent var0) { }
		public void stateChanged(javax.swing.event.ChangeEvent var0) { }
	}
	protected javax.accessibility.AccessibleContext accessibleContext;
	public ProgressMonitor(java.awt.Component var0, java.lang.Object var1, java.lang.String var2, int var3, int var4) { } 
	public void close() { }
	public javax.accessibility.AccessibleContext getAccessibleContext() { return null; }
	public int getMaximum() { return 0; }
	public int getMillisToDecideToPopup() { return 0; }
	public int getMillisToPopup() { return 0; }
	public int getMinimum() { return 0; }
	public java.lang.String getNote() { return null; }
	public boolean isCanceled() { return false; }
	public void setMaximum(int var0) { }
	public void setMillisToDecideToPopup(int var0) { }
	public void setMillisToPopup(int var0) { }
	public void setMinimum(int var0) { }
	public void setNote(java.lang.String var0) { }
	public void setProgress(int var0) { }
}

