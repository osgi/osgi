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

package javax.swing.text.html.parser;
public class Parser implements javax.swing.text.html.parser.DTDConstants {
	public Parser(javax.swing.text.html.parser.DTD var0) { }
	protected void endTag(boolean var0) { }
	protected void error(java.lang.String var0) { }
	protected void error(java.lang.String var0, java.lang.String var1) { }
	protected void error(java.lang.String var0, java.lang.String var1, java.lang.String var2) { }
	protected void error(java.lang.String var0, java.lang.String var1, java.lang.String var2, java.lang.String var3) { }
	protected void flushAttributes() { }
	protected javax.swing.text.SimpleAttributeSet getAttributes() { return null; }
	protected int getCurrentLine() { return 0; }
	protected int getCurrentPos() { return 0; }
	protected void handleComment(char[] var0) { }
	protected void handleEOFInComment() { }
	protected void handleEmptyTag(javax.swing.text.html.parser.TagElement var0) throws javax.swing.text.ChangedCharSetException { }
	protected void handleEndTag(javax.swing.text.html.parser.TagElement var0) { }
	protected void handleError(int var0, java.lang.String var1) { }
	protected void handleStartTag(javax.swing.text.html.parser.TagElement var0) { }
	protected void handleText(char[] var0) { }
	protected void handleTitle(char[] var0) { }
	protected javax.swing.text.html.parser.TagElement makeTag(javax.swing.text.html.parser.Element var0) { return null; }
	protected javax.swing.text.html.parser.TagElement makeTag(javax.swing.text.html.parser.Element var0, boolean var1) { return null; }
	protected void markFirstTime(javax.swing.text.html.parser.Element var0) { }
	public void parse(java.io.Reader var0) throws java.io.IOException { }
	public java.lang.String parseDTDMarkup() throws java.io.IOException { return null; }
	protected boolean parseMarkupDeclarations(java.lang.StringBuffer var0) throws java.io.IOException { return false; }
	protected void startTag(javax.swing.text.html.parser.TagElement var0) throws javax.swing.text.ChangedCharSetException { }
	protected javax.swing.text.html.parser.DTD dtd;
	protected boolean strict;
}

