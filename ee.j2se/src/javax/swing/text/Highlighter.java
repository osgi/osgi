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

package javax.swing.text;
public interface Highlighter {
	public interface Highlight {
		int getEndOffset();
		javax.swing.text.Highlighter.HighlightPainter getPainter();
		int getStartOffset();
	}
	public interface HighlightPainter {
		void paint(java.awt.Graphics var0, int var1, int var2, java.awt.Shape var3, javax.swing.text.JTextComponent var4);
	}
	java.lang.Object addHighlight(int var0, int var1, javax.swing.text.Highlighter.HighlightPainter var2) throws javax.swing.text.BadLocationException;
	void changeHighlight(java.lang.Object var0, int var1, int var2) throws javax.swing.text.BadLocationException;
	void deinstall(javax.swing.text.JTextComponent var0);
	javax.swing.text.Highlighter.Highlight[] getHighlights();
	void install(javax.swing.text.JTextComponent var0);
	void paint(java.awt.Graphics var0);
	void removeAllHighlights();
	void removeHighlight(java.lang.Object var0);
}

