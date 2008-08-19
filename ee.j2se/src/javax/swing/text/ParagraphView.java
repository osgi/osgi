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
public class ParagraphView extends javax.swing.text.FlowView implements javax.swing.text.TabExpander {
	public ParagraphView(javax.swing.text.Element var0) { super((javax.swing.text.Element) null, 0); }
	protected void adjustRow(javax.swing.text.ParagraphView.Row var0, int var1, int var2) { }
	public javax.swing.text.View breakView(int var0, float var1, java.awt.Shape var2) { return null; }
	protected javax.swing.text.View createRow() { return null; }
	protected int findOffsetToCharactersInString(char[] var0, int var1) { return 0; }
	public int getBreakWeight(int var0, float var1) { return 0; }
	protected int getClosestPositionTo(int var0, javax.swing.text.Position.Bias var1, java.awt.Shape var2, int var3, javax.swing.text.Position.Bias[] var4, int var5, int var6) throws javax.swing.text.BadLocationException { return 0; }
	protected javax.swing.text.View getLayoutView(int var0) { return null; }
	protected int getLayoutViewCount() { return 0; }
	protected float getPartialSize(int var0, int var1) { return 0.0f; }
	protected float getTabBase() { return 0.0f; }
	protected javax.swing.text.TabSet getTabSet() { return null; }
	public float nextTabStop(float var0, int var1) { return 0.0f; }
	protected void setFirstLineIndent(float var0) { }
	protected void setJustification(int var0) { }
	protected void setLineSpacing(float var0) { }
	protected void setPropertiesFromAttributes() { }
	protected int firstLineIndent;
	class Row extends javax.swing.text.BoxView {
		private Row() { super((javax.swing.text.Element) null, 0); } /* generated constructor to prevent compiler adding default public constructor */
	}
}

