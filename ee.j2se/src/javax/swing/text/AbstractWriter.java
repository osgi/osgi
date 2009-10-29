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
public abstract class AbstractWriter {
	protected AbstractWriter(java.io.Writer var0, javax.swing.text.Document var1) { }
	protected AbstractWriter(java.io.Writer var0, javax.swing.text.Document var1, int var2, int var3) { }
	protected AbstractWriter(java.io.Writer var0, javax.swing.text.Element var1) { }
	protected AbstractWriter(java.io.Writer var0, javax.swing.text.Element var1, int var2, int var3) { }
	protected void decrIndent() { }
	protected boolean getCanWrapLines() { return false; }
	protected int getCurrentLineLength() { return 0; }
	protected javax.swing.text.Document getDocument() { return null; }
	protected javax.swing.text.ElementIterator getElementIterator() { return null; }
	public int getEndOffset() { return 0; }
	protected int getIndentLevel() { return 0; }
	protected int getIndentSpace() { return 0; }
	protected int getLineLength() { return 0; }
	public java.lang.String getLineSeparator() { return null; }
	public int getStartOffset() { return 0; }
	protected java.lang.String getText(javax.swing.text.Element var0) throws javax.swing.text.BadLocationException { return null; }
	protected java.io.Writer getWriter() { return null; }
	protected boolean inRange(javax.swing.text.Element var0) { return false; }
	protected void incrIndent() { }
	protected void indent() throws java.io.IOException { }
	protected boolean isLineEmpty() { return false; }
	protected void output(char[] var0, int var1, int var2) throws java.io.IOException { }
	protected void setCanWrapLines(boolean var0) { }
	protected void setCurrentLineLength(int var0) { }
	protected void setIndentSpace(int var0) { }
	protected void setLineLength(int var0) { }
	public void setLineSeparator(java.lang.String var0) { }
	protected void text(javax.swing.text.Element var0) throws java.io.IOException, javax.swing.text.BadLocationException { }
	protected abstract void write() throws java.io.IOException, javax.swing.text.BadLocationException;
	protected void write(char var0) throws java.io.IOException { }
	protected void write(java.lang.String var0) throws java.io.IOException { }
	protected void write(char[] var0, int var1, int var2) throws java.io.IOException { }
	protected void writeAttributes(javax.swing.text.AttributeSet var0) throws java.io.IOException { }
	protected void writeLineSeparator() throws java.io.IOException { }
	protected final static char NEWLINE = 10;
}

