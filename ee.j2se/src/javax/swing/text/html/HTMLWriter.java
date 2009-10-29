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

package javax.swing.text.html;
public class HTMLWriter extends javax.swing.text.AbstractWriter {
	public HTMLWriter(java.io.Writer var0, javax.swing.text.html.HTMLDocument var1) { super((java.io.Writer) null, (javax.swing.text.Element) null, 0, 0); }
	public HTMLWriter(java.io.Writer var0, javax.swing.text.html.HTMLDocument var1, int var2, int var3) { super((java.io.Writer) null, (javax.swing.text.Element) null, 0, 0); }
	protected void closeOutUnwantedEmbeddedTags(javax.swing.text.AttributeSet var0) throws java.io.IOException { }
	protected void comment(javax.swing.text.Element var0) throws java.io.IOException, javax.swing.text.BadLocationException { }
	protected void emptyTag(javax.swing.text.Element var0) throws java.io.IOException, javax.swing.text.BadLocationException { }
	protected void endTag(javax.swing.text.Element var0) throws java.io.IOException { }
	protected boolean isBlockTag(javax.swing.text.AttributeSet var0) { return false; }
	protected boolean matchNameAttribute(javax.swing.text.AttributeSet var0, javax.swing.text.html.HTML.Tag var1) { return false; }
	protected void selectContent(javax.swing.text.AttributeSet var0) throws java.io.IOException { }
	protected void startTag(javax.swing.text.Element var0) throws java.io.IOException, javax.swing.text.BadLocationException { }
	protected boolean synthesizedElement(javax.swing.text.Element var0) { return false; }
	protected void textAreaContent(javax.swing.text.AttributeSet var0) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void write() throws java.io.IOException, javax.swing.text.BadLocationException { }
	protected void writeEmbeddedTags(javax.swing.text.AttributeSet var0) throws java.io.IOException { }
	protected void writeOption(javax.swing.text.html.Option var0) throws java.io.IOException { }
}

