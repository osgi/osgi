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
public class DefaultHighlighter extends javax.swing.text.LayeredHighlighter {
	public DefaultHighlighter() { }
	public java.lang.Object addHighlight(int var0, int var1, javax.swing.text.Highlighter.HighlightPainter var2) throws javax.swing.text.BadLocationException { return null; }
	public void changeHighlight(java.lang.Object var0, int var1, int var2) throws javax.swing.text.BadLocationException { }
	public void deinstall(javax.swing.text.JTextComponent var0) { }
	public boolean getDrawsLayeredHighlights() { return false; }
	public javax.swing.text.Highlighter.Highlight[] getHighlights() { return null; }
	public void install(javax.swing.text.JTextComponent var0) { }
	public void paint(java.awt.Graphics var0) { }
	public void paintLayeredHighlights(java.awt.Graphics var0, int var1, int var2, java.awt.Shape var3, javax.swing.text.JTextComponent var4, javax.swing.text.View var5) { }
	public void removeAllHighlights() { }
	public void removeHighlight(java.lang.Object var0) { }
	public void setDrawsLayeredHighlights(boolean var0) { }
	public final static javax.swing.text.LayeredHighlighter.LayerPainter DefaultPainter; static { DefaultPainter = null; }
	public static class DefaultHighlightPainter extends javax.swing.text.LayeredHighlighter.LayerPainter {
		public DefaultHighlightPainter(java.awt.Color var0) { }
		public java.awt.Color getColor() { return null; }
		public void paint(java.awt.Graphics var0, int var1, int var2, java.awt.Shape var3, javax.swing.text.JTextComponent var4) { }
		public java.awt.Shape paintLayer(java.awt.Graphics var0, int var1, int var2, java.awt.Shape var3, javax.swing.text.JTextComponent var4, javax.swing.text.View var5) { return null; }
	}
}

