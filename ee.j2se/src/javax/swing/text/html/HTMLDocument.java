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
public class HTMLDocument extends javax.swing.text.DefaultStyledDocument {
	public HTMLDocument() { }
	public HTMLDocument(javax.swing.text.AbstractDocument.Content var0, javax.swing.text.html.StyleSheet var1) { }
	public HTMLDocument(javax.swing.text.html.StyleSheet var0) { }
	public java.net.URL getBase() { return null; }
	public javax.swing.text.Element getElement(java.lang.String var0) { return null; }
	public javax.swing.text.Element getElement(javax.swing.text.Element var0, java.lang.Object var1, java.lang.Object var2) { return null; }
	public javax.swing.text.html.HTMLDocument.Iterator getIterator(javax.swing.text.html.HTML.Tag var0) { return null; }
	public javax.swing.text.html.HTMLEditorKit.Parser getParser() { return null; }
	public boolean getPreservesUnknownTags() { return false; }
	public javax.swing.text.html.HTMLEditorKit.ParserCallback getReader(int var0) { return null; }
	public javax.swing.text.html.HTMLEditorKit.ParserCallback getReader(int var0, int var1, int var2, javax.swing.text.html.HTML.Tag var3) { return null; }
	public javax.swing.text.html.StyleSheet getStyleSheet() { return null; }
	public int getTokenThreshold() { return 0; }
	public void insertAfterEnd(javax.swing.text.Element var0, java.lang.String var1) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void insertAfterStart(javax.swing.text.Element var0, java.lang.String var1) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void insertBeforeEnd(javax.swing.text.Element var0, java.lang.String var1) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void insertBeforeStart(javax.swing.text.Element var0, java.lang.String var1) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void processHTMLFrameHyperlinkEvent(javax.swing.text.html.HTMLFrameHyperlinkEvent var0) { }
	public void setBase(java.net.URL var0) { }
	public void setInnerHTML(javax.swing.text.Element var0, java.lang.String var1) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void setOuterHTML(javax.swing.text.Element var0, java.lang.String var1) throws java.io.IOException, javax.swing.text.BadLocationException { }
	public void setParser(javax.swing.text.html.HTMLEditorKit.Parser var0) { }
	public void setPreservesUnknownTags(boolean var0) { }
	public void setTokenThreshold(int var0) { }
	public final static java.lang.String AdditionalComments = "AdditionalComments";
	public class BlockElement extends javax.swing.text.AbstractDocument.BranchElement {
		public BlockElement(javax.swing.text.Element var0, javax.swing.text.AttributeSet var1) { super((javax.swing.text.Element) null, (javax.swing.text.AttributeSet) null); }
	}
	public class HTMLReader extends javax.swing.text.html.HTMLEditorKit.ParserCallback {
		public HTMLReader(int var0) { }
		public HTMLReader(int var0, int var1, int var2, javax.swing.text.html.HTML.Tag var3) { }
		protected void addContent(char[] var0, int var1, int var2) { }
		protected void addContent(char[] var0, int var1, int var2, boolean var3) { }
		protected void addSpecialElement(javax.swing.text.html.HTML.Tag var0, javax.swing.text.MutableAttributeSet var1) { }
		protected void blockClose(javax.swing.text.html.HTML.Tag var0) { }
		protected void blockOpen(javax.swing.text.html.HTML.Tag var0, javax.swing.text.MutableAttributeSet var1) { }
		protected void popCharacterStyle() { }
		protected void preContent(char[] var0) { }
		protected void pushCharacterStyle() { }
		protected void registerTag(javax.swing.text.html.HTML.Tag var0, javax.swing.text.html.HTMLDocument.HTMLReader.TagAction var1) { }
		protected void textAreaContent(char[] var0) { }
		protected javax.swing.text.MutableAttributeSet charAttr;
		protected java.util.Vector parseBuffer;
		public class BlockAction extends javax.swing.text.html.HTMLDocument.HTMLReader.TagAction {
			public BlockAction() { }
		}
		public class CharacterAction extends javax.swing.text.html.HTMLDocument.HTMLReader.TagAction {
			public CharacterAction() { }
		}
		public class FormAction extends javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction {
			public FormAction() { }
		}
		public class HiddenAction extends javax.swing.text.html.HTMLDocument.HTMLReader.TagAction {
			public HiddenAction() { }
		}
		public class IsindexAction extends javax.swing.text.html.HTMLDocument.HTMLReader.TagAction {
			public IsindexAction() { }
		}
		public class ParagraphAction extends javax.swing.text.html.HTMLDocument.HTMLReader.BlockAction {
			public ParagraphAction() { }
		}
		public class PreAction extends javax.swing.text.html.HTMLDocument.HTMLReader.BlockAction {
			public PreAction() { }
		}
		public class SpecialAction extends javax.swing.text.html.HTMLDocument.HTMLReader.TagAction {
			public SpecialAction() { }
		}
		public class TagAction {
			public TagAction() { }
			public void end(javax.swing.text.html.HTML.Tag var0) { }
			public void start(javax.swing.text.html.HTML.Tag var0, javax.swing.text.MutableAttributeSet var1) { }
		}
	}
	public static abstract class Iterator {
		public Iterator() { }
		public abstract javax.swing.text.AttributeSet getAttributes();
		public abstract int getEndOffset();
		public abstract int getStartOffset();
		public abstract javax.swing.text.html.HTML.Tag getTag();
		public abstract boolean isValid();
		public abstract void next();
	}
	public class RunElement extends javax.swing.text.AbstractDocument.LeafElement {
		public RunElement(javax.swing.text.Element var0, javax.swing.text.AttributeSet var1, int var2, int var3) { super((javax.swing.text.Element) null, (javax.swing.text.AttributeSet) null, 0, 0); }
	}
}

