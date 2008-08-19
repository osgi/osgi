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
public abstract interface Highlighter {
	public abstract java.lang.Object addHighlight(int var0, int var1, javax.swing.text.Highlighter.HighlightPainter var2) throws javax.swing.text.BadLocationException;
	public abstract void changeHighlight(java.lang.Object var0, int var1, int var2) throws javax.swing.text.BadLocationException;
	public abstract void deinstall(javax.swing.text.JTextComponent var0);
	public abstract javax.swing.text.Highlighter.Highlight[] getHighlights();
	public abstract void install(javax.swing.text.JTextComponent var0);
	public abstract void paint(java.awt.Graphics var0);
	public abstract void removeAllHighlights();
	public abstract void removeHighlight(java.lang.Object var0);
	public static abstract interface Highlight {
		public abstract int getEndOffset();
		public abstract javax.swing.text.Highlighter.HighlightPainter getPainter();
		public abstract int getStartOffset();
	}
	public static abstract interface HighlightPainter {
		public abstract void paint(java.awt.Graphics var0, int var1, int var2, java.awt.Shape var3, javax.swing.text.JTextComponent var4);
	}
}

