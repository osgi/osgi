/*
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
public class PlainView extends javax.swing.text.View implements javax.swing.text.TabExpander {
	public PlainView(javax.swing.text.Element var0) { super((javax.swing.text.Element) null); }
	protected void damageLineRange(int var0, int var1, java.awt.Shape var2, java.awt.Component var3) { }
	protected void drawLine(int var0, java.awt.Graphics var1, int var2, int var3) { }
	protected int drawSelectedText(java.awt.Graphics var0, int var1, int var2, int var3, int var4) throws javax.swing.text.BadLocationException { return 0; }
	protected int drawUnselectedText(java.awt.Graphics var0, int var1, int var2, int var3, int var4) throws javax.swing.text.BadLocationException { return 0; }
	protected final javax.swing.text.Segment getLineBuffer() { return null; }
	public float getPreferredSpan(int var0) { return 0.0f; }
	protected int getTabSize() { return 0; }
	protected java.awt.Rectangle lineToRect(java.awt.Shape var0, int var1) { return null; }
	public java.awt.Shape modelToView(int var0, java.awt.Shape var1, javax.swing.text.Position.Bias var2) throws javax.swing.text.BadLocationException { return null; }
	public float nextTabStop(float var0, int var1) { return 0.0f; }
	public void paint(java.awt.Graphics var0, java.awt.Shape var1) { }
	protected void updateDamage(javax.swing.event.DocumentEvent var0, java.awt.Shape var1, javax.swing.text.ViewFactory var2) { }
	protected void updateMetrics() { }
	public int viewToModel(float var0, float var1, java.awt.Shape var2, javax.swing.text.Position.Bias[] var3) { return 0; }
	protected java.awt.FontMetrics metrics;
}

