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

package javax.swing.text.html;
public class MinimalHTMLWriter extends javax.swing.text.AbstractWriter {
	public MinimalHTMLWriter(java.io.Writer var0, javax.swing.text.StyledDocument var1) { super((java.io.Writer) null, (javax.swing.text.Element) null, 0, 0); }
	public MinimalHTMLWriter(java.io.Writer var0, javax.swing.text.StyledDocument var1, int var2, int var3) { super((java.io.Writer) null, (javax.swing.text.Element) null, 0, 0); }
	protected void endFontTag() throws java.io.IOException { }
	protected boolean inFontTag() { return false; }
	protected boolean isText(javax.swing.text.Element var0) { return false; }
	protected void startFontTag(java.lang.String var0) throws java.io.IOException { }
	public void write() throws java.io.IOException, javax.swing.text.BadLocationException { }
	protected void writeBody() throws java.io.IOException, javax.swing.text.BadLocationException { }
	protected void writeComponent(javax.swing.text.Element var0) throws java.io.IOException { }
	protected void writeContent(javax.swing.text.Element var0, boolean var1) throws java.io.IOException, javax.swing.text.BadLocationException { }
	protected void writeEndParagraph() throws java.io.IOException { }
	protected void writeEndTag(java.lang.String var0) throws java.io.IOException { }
	protected void writeHTMLTags(javax.swing.text.AttributeSet var0) throws java.io.IOException { }
	protected void writeHeader() throws java.io.IOException { }
	protected void writeImage(javax.swing.text.Element var0) throws java.io.IOException { }
	protected void writeLeaf(javax.swing.text.Element var0) throws java.io.IOException { }
	protected void writeNonHTMLAttributes(javax.swing.text.AttributeSet var0) throws java.io.IOException { }
	protected void writeStartParagraph(javax.swing.text.Element var0) throws java.io.IOException { }
	protected void writeStartTag(java.lang.String var0) throws java.io.IOException { }
	protected void writeStyles() throws java.io.IOException { }
}

