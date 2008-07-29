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
public abstract class View implements javax.swing.SwingConstants {
	public View(javax.swing.text.Element var0) { }
	public void append(javax.swing.text.View var0) { }
	public javax.swing.text.View breakView(int var0, int var1, float var2, float var3) { return null; }
	public void changedUpdate(javax.swing.event.DocumentEvent var0, java.awt.Shape var1, javax.swing.text.ViewFactory var2) { }
	public javax.swing.text.View createFragment(int var0, int var1) { return null; }
	protected void forwardUpdate(javax.swing.event.DocumentEvent.ElementChange var0, javax.swing.event.DocumentEvent var1, java.awt.Shape var2, javax.swing.text.ViewFactory var3) { }
	protected void forwardUpdateToView(javax.swing.text.View var0, javax.swing.event.DocumentEvent var1, java.awt.Shape var2, javax.swing.text.ViewFactory var3) { }
	public float getAlignment(int var0) { return 0.0f; }
	public javax.swing.text.AttributeSet getAttributes() { return null; }
	public int getBreakWeight(int var0, float var1, float var2) { return 0; }
	public java.awt.Shape getChildAllocation(int var0, java.awt.Shape var1) { return null; }
	public java.awt.Container getContainer() { return null; }
	public javax.swing.text.Document getDocument() { return null; }
	public javax.swing.text.Element getElement() { return null; }
	public int getEndOffset() { return 0; }
	public java.awt.Graphics getGraphics() { return null; }
	public float getMaximumSpan(int var0) { return 0.0f; }
	public float getMinimumSpan(int var0) { return 0.0f; }
	public int getNextVisualPositionFrom(int var0, javax.swing.text.Position.Bias var1, java.awt.Shape var2, int var3, javax.swing.text.Position.Bias[] var4) throws javax.swing.text.BadLocationException { return 0; }
	public javax.swing.text.View getParent() { return null; }
	public abstract float getPreferredSpan(int var0);
	public int getResizeWeight(int var0) { return 0; }
	public int getStartOffset() { return 0; }
	public java.lang.String getToolTipText(float var0, float var1, java.awt.Shape var2) { return null; }
	public javax.swing.text.View getView(int var0) { return null; }
	public int getViewCount() { return 0; }
	public javax.swing.text.ViewFactory getViewFactory() { return null; }
	public int getViewIndex(float var0, float var1, java.awt.Shape var2) { return 0; }
	public int getViewIndex(int var0, javax.swing.text.Position.Bias var1) { return 0; }
	public void insert(int var0, javax.swing.text.View var1) { }
	public void insertUpdate(javax.swing.event.DocumentEvent var0, java.awt.Shape var1, javax.swing.text.ViewFactory var2) { }
	public boolean isVisible() { return false; }
	/** @deprecated */ public java.awt.Shape modelToView(int var0, java.awt.Shape var1) throws javax.swing.text.BadLocationException { return null; }
	public abstract java.awt.Shape modelToView(int var0, java.awt.Shape var1, javax.swing.text.Position.Bias var2) throws javax.swing.text.BadLocationException;
	public java.awt.Shape modelToView(int var0, javax.swing.text.Position.Bias var1, int var2, javax.swing.text.Position.Bias var3, java.awt.Shape var4) throws javax.swing.text.BadLocationException { return null; }
	public abstract void paint(java.awt.Graphics var0, java.awt.Shape var1);
	public void preferenceChanged(javax.swing.text.View var0, boolean var1, boolean var2) { }
	public void remove(int var0) { }
	public void removeAll() { }
	public void removeUpdate(javax.swing.event.DocumentEvent var0, java.awt.Shape var1, javax.swing.text.ViewFactory var2) { }
	public void replace(int var0, int var1, javax.swing.text.View[] var2) { }
	public void setParent(javax.swing.text.View var0) { }
	public void setSize(float var0, float var1) { }
	protected boolean updateChildren(javax.swing.event.DocumentEvent.ElementChange var0, javax.swing.event.DocumentEvent var1, javax.swing.text.ViewFactory var2) { return false; }
	protected void updateLayout(javax.swing.event.DocumentEvent.ElementChange var0, javax.swing.event.DocumentEvent var1, java.awt.Shape var2) { }
	/** @deprecated */ public int viewToModel(float var0, float var1, java.awt.Shape var2) { return 0; }
	public abstract int viewToModel(float var0, float var1, java.awt.Shape var2, javax.swing.text.Position.Bias[] var3);
	public final static int BadBreakWeight = 0;
	public final static int ExcellentBreakWeight = 2000;
	public final static int ForcedBreakWeight = 3000;
	public final static int GoodBreakWeight = 1000;
	public final static int X_AXIS = 0;
	public final static int Y_AXIS = 1;
}

