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

package javax.swing.text.html.parser;
public class DTD implements javax.swing.text.html.parser.DTDConstants {
	protected DTD(java.lang.String var0) { }
	protected javax.swing.text.html.parser.AttributeList defAttributeList(java.lang.String var0, int var1, int var2, java.lang.String var3, java.lang.String var4, javax.swing.text.html.parser.AttributeList var5) { return null; }
	protected javax.swing.text.html.parser.ContentModel defContentModel(int var0, java.lang.Object var1, javax.swing.text.html.parser.ContentModel var2) { return null; }
	protected javax.swing.text.html.parser.Element defElement(java.lang.String var0, int var1, boolean var2, boolean var3, javax.swing.text.html.parser.ContentModel var4, java.lang.String[] var5, java.lang.String[] var6, javax.swing.text.html.parser.AttributeList var7) { return null; }
	public javax.swing.text.html.parser.Entity defEntity(java.lang.String var0, int var1, int var2) { return null; }
	protected javax.swing.text.html.parser.Entity defEntity(java.lang.String var0, int var1, java.lang.String var2) { return null; }
	public void defineAttributes(java.lang.String var0, javax.swing.text.html.parser.AttributeList var1) { }
	public javax.swing.text.html.parser.Element defineElement(java.lang.String var0, int var1, boolean var2, boolean var3, javax.swing.text.html.parser.ContentModel var4, java.util.BitSet var5, java.util.BitSet var6, javax.swing.text.html.parser.AttributeList var7) { return null; }
	public javax.swing.text.html.parser.Entity defineEntity(java.lang.String var0, int var1, char[] var2) { return null; }
	public static javax.swing.text.html.parser.DTD getDTD(java.lang.String var0) throws java.io.IOException { return null; }
	public javax.swing.text.html.parser.Element getElement(int var0) { return null; }
	public javax.swing.text.html.parser.Element getElement(java.lang.String var0) { return null; }
	public javax.swing.text.html.parser.Entity getEntity(int var0) { return null; }
	public javax.swing.text.html.parser.Entity getEntity(java.lang.String var0) { return null; }
	public java.lang.String getName() { return null; }
	public static void putDTDHash(java.lang.String var0, javax.swing.text.html.parser.DTD var1) { }
	public void read(java.io.DataInputStream var0) throws java.io.IOException { }
	public static int FILE_VERSION;
	public final javax.swing.text.html.parser.Element applet = null;
	public final javax.swing.text.html.parser.Element base = null;
	public final javax.swing.text.html.parser.Element body = null;
	public java.util.Hashtable elementHash;
	public java.util.Vector elements;
	public java.util.Hashtable entityHash;
	public final javax.swing.text.html.parser.Element head = null;
	public final javax.swing.text.html.parser.Element html = null;
	public final javax.swing.text.html.parser.Element isindex = null;
	public final javax.swing.text.html.parser.Element meta = null;
	public java.lang.String name;
	public final javax.swing.text.html.parser.Element p = null;
	public final javax.swing.text.html.parser.Element param = null;
	public final javax.swing.text.html.parser.Element pcdata = null;
	public final javax.swing.text.html.parser.Element title = null;
}

