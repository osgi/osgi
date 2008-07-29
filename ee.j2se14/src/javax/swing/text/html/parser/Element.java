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
public final class Element implements java.io.Serializable, javax.swing.text.html.parser.DTDConstants {
	public javax.swing.text.html.parser.AttributeList getAttribute(java.lang.String var0) { return null; }
	public javax.swing.text.html.parser.AttributeList getAttributeByValue(java.lang.String var0) { return null; }
	public javax.swing.text.html.parser.AttributeList getAttributes() { return null; }
	public javax.swing.text.html.parser.ContentModel getContent() { return null; }
	public int getIndex() { return 0; }
	public java.lang.String getName() { return null; }
	public int getType() { return 0; }
	public boolean isEmpty() { return false; }
	public static int name2type(java.lang.String var0) { return 0; }
	public boolean omitEnd() { return false; }
	public boolean omitStart() { return false; }
	public javax.swing.text.html.parser.AttributeList atts;
	public javax.swing.text.html.parser.ContentModel content;
	public java.lang.Object data;
	public java.util.BitSet exclusions;
	public java.util.BitSet inclusions;
	public int index;
	public java.lang.String name;
	public boolean oEnd;
	public boolean oStart;
	public int type;
	private Element() { } /* generated constructor to prevent compiler adding default public constructor */
}

