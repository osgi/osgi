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

package javax.swing.text;
public abstract class CompositeView extends javax.swing.text.View {
	public CompositeView(javax.swing.text.Element var0) { super((javax.swing.text.Element) null); }
	protected abstract void childAllocation(int var0, java.awt.Rectangle var1);
	protected boolean flipEastAndWestAtEnds(int var0, javax.swing.text.Position.Bias var1) { return false; }
	protected short getBottomInset() { return 0; }
	protected java.awt.Rectangle getInsideAllocation(java.awt.Shape var0) { return null; }
	protected short getLeftInset() { return 0; }
	protected int getNextEastWestVisualPositionFrom(int var0, javax.swing.text.Position.Bias var1, java.awt.Shape var2, int var3, javax.swing.text.Position.Bias[] var4) throws javax.swing.text.BadLocationException { return 0; }
	protected int getNextNorthSouthVisualPositionFrom(int var0, javax.swing.text.Position.Bias var1, java.awt.Shape var2, int var3, javax.swing.text.Position.Bias[] var4) throws javax.swing.text.BadLocationException { return 0; }
	protected short getRightInset() { return 0; }
	protected short getTopInset() { return 0; }
	protected abstract javax.swing.text.View getViewAtPoint(int var0, int var1, java.awt.Rectangle var2);
	protected javax.swing.text.View getViewAtPosition(int var0, java.awt.Rectangle var1) { return null; }
	protected int getViewIndexAtPosition(int var0) { return 0; }
	protected abstract boolean isAfter(int var0, int var1, java.awt.Rectangle var2);
	protected abstract boolean isBefore(int var0, int var1, java.awt.Rectangle var2);
	protected void loadChildren(javax.swing.text.ViewFactory var0) { }
	public java.awt.Shape modelToView(int var0, java.awt.Shape var1, javax.swing.text.Position.Bias var2) throws javax.swing.text.BadLocationException { return null; }
	protected void setInsets(short var0, short var1, short var2, short var3) { }
	protected void setParagraphInsets(javax.swing.text.AttributeSet var0) { }
	public int viewToModel(float var0, float var1, java.awt.Shape var2, javax.swing.text.Position.Bias[] var3) { return 0; }
}

