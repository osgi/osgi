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
public final class AttributeList implements java.io.Serializable, javax.swing.text.html.parser.DTDConstants {
	public AttributeList(java.lang.String var0) { }
	public AttributeList(java.lang.String var0, int var1, int var2, java.lang.String var3, java.util.Vector var4, javax.swing.text.html.parser.AttributeList var5) { }
	public int getModifier() { return 0; }
	public java.lang.String getName() { return null; }
	public javax.swing.text.html.parser.AttributeList getNext() { return null; }
	public int getType() { return 0; }
	public java.lang.String getValue() { return null; }
	public java.util.Enumeration getValues() { return null; }
	public static int name2type(java.lang.String var0) { return 0; }
	public static java.lang.String type2name(int var0) { return null; }
	public int modifier;
	public java.lang.String name;
	public javax.swing.text.html.parser.AttributeList next;
	public int type;
	public java.lang.String value;
	public java.util.Vector values;
}

