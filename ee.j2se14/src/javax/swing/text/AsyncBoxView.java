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

package javax.swing.text;
public class AsyncBoxView extends javax.swing.text.View {
	public AsyncBoxView(javax.swing.text.Element var0, int var1) { super((javax.swing.text.Element) null); }
	protected javax.swing.text.AsyncBoxView.ChildState createChildState(javax.swing.text.View var0) { return null; }
	protected void flushRequirementChanges() { }
	public float getBottomInset() { return 0.0f; }
	protected javax.swing.text.AsyncBoxView.ChildState getChildState(int var0) { return null; }
	protected boolean getEstimatedMajorSpan() { return false; }
	protected float getInsetSpan(int var0) { return 0.0f; }
	protected javax.swing.text.LayoutQueue getLayoutQueue() { return null; }
	public float getLeftInset() { return 0.0f; }
	public int getMajorAxis() { return 0; }
	public int getMinorAxis() { return 0; }
	public float getPreferredSpan(int var0) { return 0.0f; }
	public float getRightInset() { return 0.0f; }
	public float getTopInset() { return 0.0f; }
	protected int getViewIndexAtPosition(int var0, javax.swing.text.Position.Bias var1) { return 0; }
	protected void loadChildren(javax.swing.text.ViewFactory var0) { }
	protected void majorRequirementChange(javax.swing.text.AsyncBoxView.ChildState var0, float var1) { }
	protected void minorRequirementChange(javax.swing.text.AsyncBoxView.ChildState var0) { }
	public java.awt.Shape modelToView(int var0, java.awt.Shape var1, javax.swing.text.Position.Bias var2) throws javax.swing.text.BadLocationException { return null; }
	public void paint(java.awt.Graphics var0, java.awt.Shape var1) { }
	public void preferenceChanged(javax.swing.text.View var0, boolean var1, boolean var2) { }
	public void setBottomInset(float var0) { }
	protected void setEstimatedMajorSpan(boolean var0) { }
	public void setLeftInset(float var0) { }
	public void setRightInset(float var0) { }
	public void setTopInset(float var0) { }
	public int viewToModel(float var0, float var1, java.awt.Shape var2, javax.swing.text.Position.Bias[] var3) { return 0; }
	protected javax.swing.text.AsyncBoxView.ChildLocator locator;
	public class ChildLocator {
		public ChildLocator() { }
		public void childChanged(javax.swing.text.AsyncBoxView.ChildState var0) { }
		protected java.awt.Shape getChildAllocation(int var0) { return null; }
		public java.awt.Shape getChildAllocation(int var0, java.awt.Shape var1) { return null; }
		public int getViewIndexAtPoint(float var0, float var1, java.awt.Shape var2) { return 0; }
		protected int getViewIndexAtVisualOffset(float var0) { return 0; }
		public void paintChildren(java.awt.Graphics var0) { }
		protected void setAllocation(java.awt.Shape var0) { }
		protected java.awt.Rectangle childAlloc;
		protected java.awt.Rectangle lastAlloc;
		protected javax.swing.text.AsyncBoxView.ChildState lastValidOffset;
	}
	public class ChildState implements java.lang.Runnable {
		public ChildState(javax.swing.text.View var0) { }
		public javax.swing.text.View getChildView() { return null; }
		public float getMajorOffset() { return 0.0f; }
		public float getMajorSpan() { return 0.0f; }
		public float getMinorOffset() { return 0.0f; }
		public float getMinorSpan() { return 0.0f; }
		public boolean isLayoutValid() { return false; }
		public void preferenceChanged(boolean var0, boolean var1) { }
		public void run() { }
		public void setMajorOffset(float var0) { }
	}
}

