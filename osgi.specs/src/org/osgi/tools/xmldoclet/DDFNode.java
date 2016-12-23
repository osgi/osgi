/*
 * Copyright (c) OSGi Alliance (2011, 2016). All Rights Reserved.
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

package org.osgi.tools.xmldoclet;

import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;

@SuppressWarnings("javadoc")
public class DDFNode {
	static Map<String, String> DMT_TYPES = new HashMap<String, String>();
	static {
		DMT_TYPES.put(boolean.class.getName(), "boolean");
		DMT_TYPES.put(Boolean.class.getName(), "boolean");
		DMT_TYPES.put(byte.class.getName(), "byte");
		DMT_TYPES.put(Byte.class.getName(), "byte");
		DMT_TYPES.put(int.class.getName(), "integer");
		DMT_TYPES.put(Integer.class.getName(), "integer");
		DMT_TYPES.put(long.class.getName(), "long");
		DMT_TYPES.put(Long.class.getName(), "long");
		DMT_TYPES.put(float.class.getName(), "float");
		DMT_TYPES.put(Float.class.getName(), "float");
		DMT_TYPES.put(Date.class.getName(), "date_time");
		DMT_TYPES.put(String.class.getName(), "string");
		DMT_TYPES.put(URI.class.getName(), "node_uri");
		DMT_TYPES.put("org.osgi.dmt.ddf.base64", "base64");
		DMT_TYPES.put("byte[]", "binary");
	}
	static Pattern	DDF_OPT		= Pattern
			.compile("org.osgi.dmt.ddf.Opt<\\s*(.*)\\s*>");
	static Pattern	DDF_MUTABLE	= Pattern
			.compile("org.osgi.dmt.ddf.Mutable<\\s*(.*)\\s*>");
	static Pattern	DDF_MAP		= Pattern
			.compile("org.osgi.dmt.ddf.(Mutable|Addable)?MAP<\\s*(.+)\\s*,\\s*(.+)\\s*>");
	static Pattern	DDF_LIST	= Pattern
			.compile("org.osgi.dmt.ddf.(Mutable)?LIST<\\s*(.+)\\s*>");

	final DDFNode	parent;
	String			name;
	List<DDFNode>	children	= new ArrayList<DDFNode>();
	boolean			optional	= false;
	boolean			multiple	= false;
	boolean			add			= false;
	boolean			delete		= false;
	boolean			get			= true;
	boolean			replace		= false;
	String			dmtType;
	String			shortName;
	String			scope		= "P";
	boolean			interior	= true;

	String			cardinality	= "1";
	String			mime		= "";

	/**
	 * @param parent
	 * @param name
	 * @param type
	 */
	public DDFNode(DDFNode parent, String name, String typeName) {
		this.parent = parent;
		this.name = name;

		Matcher m = DDF_OPT.matcher(typeName);
		if (m.matches()) {
			cardinality = "0,1";
			typeName = m.group(1);
		}

		m = DDF_MUTABLE.matcher(typeName);
		if (m.matches()) {
			replace = true;
			typeName = m.group(1);
		}

		m = DDF_LIST.matcher(typeName);
		if (m.matches()) {
			multiple = true;
			get = true;
			dmtType = "LIST";
			shortName = "LIST";
			typeName = m.group(2);

			DDFNode child = new DDFNode(this, "[list]", typeName);
			child.add = m.group(1) != null;
			child.delete = m.group(1) != null;
			child.cardinality = "0..*";
			child.scope = "D";
			children.add(child);
			return;
		}

		m = DDF_MAP.matcher(typeName);
		if (m.matches()) {
			multiple = true;
			get = true;
			dmtType = "MAP";
			shortName = "MAP";

			typeName = m.group(3);
			String keyTypeName = m.group(2);

			DDFNode child = new DDFNode(this,
					"[" + shorten(keyTypeName) + "]",
					typeName);
			child.add = m.group(1) != null;
			child.delete = m.group(1) != null && m.group(1).equals("Mutable");
			child.scope = "D";
			child.cardinality = "0..*";
			children.add(child);
			return;
		}

		dmtType = typeName;
		shortName = DMT_TYPES.get(dmtType);
		if (shortName == null) {
			shortName = shorten(dmtType);
		} else
			interior = false;
	}

	/**
	 * @param pw
	 */
	@SuppressWarnings("boxing")
	public void print(PrintWriter pw, String indent) {
		pw.printf(
				"            <ddf name='%s' indent='%s' add='%s' get='%s' replace='%s' delete='%s' longTypeName='%s' shortTypeName='%s' cardinality='%s' scope='%s' interior='%s' mime='%s'/>\n",
				name,// name
				indent, // indent
				add, //
				get, //
				replace, //
				delete, //
				dmtType, //
				shortName,
				cardinality, //
				scope, //
				interior, //
				mime == null ? "NODE" : mime);
		for (DDFNode child : children) {
			child.print(pw, indent + "\u00A0\u00A0");
		}
	}

	private String shorten(String shortTypeName) {
		int n = shortTypeName.lastIndexOf('.');
		if (n < 0)
			return shortTypeName;

		return shortTypeName.substring(n + 1);
	}

	/**
	 * @param clazz
	 * @return if this ClassDoc represents a DDF node
	 */
	public static boolean isDDF(ClassDoc clazz) {
		if (isDDF(clazz.containingPackage().annotations()))
			return true;
		while (clazz != null) {
			if (isDDF(clazz.annotations()))
				return true;

			clazz = clazz.containingClass();
		}
		return false;
	}

	private static boolean isDDF(AnnotationDesc[] annotations) {
		for (AnnotationDesc ad : annotations) {
			if (ad.toString().equals("@org.osgi.dmt.ddf.DDF"))
				return true;
		}
		return false;
	}
}
