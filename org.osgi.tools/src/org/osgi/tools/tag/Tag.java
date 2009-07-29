/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) OSGi 2001 
 */
package org.osgi.tools.tag;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The Tag class represents a minimal XML tree. It consist of a named element
 * with a hashtable of named attributes. Methods are provided to walk the tree
 * and get its constituents. The content of a Tag is a list that contains String
 * objects or other Tag objects.
 */
public class Tag {
	Tag						parent;									// Parent
	// element
	String					name;										// Name
	// of
	// the
	// tag
	Hashtable				attributes	= new Hashtable();				// Attributes
	// name
	// ->
	// value
	Vector					content		= new Vector();				// Content
	// elements
	static SimpleDateFormat	format		= new SimpleDateFormat(
												"yyyyMMddhhmmss.SSS");

	/**
	 * Construct a new Tag with a name.
	 */
	public Tag(String name) {
		this.name = name;
	}

	/**
	 * Construct a new Tag with a name and a set of attributes. The attributes
	 * are given as ( name, value ) ...
	 */
	public Tag(String name, String[] attributes) {
		this.name = name;
		for (int i = 0; i < attributes.length; i += 2)
			addAttribute(attributes[i], attributes[i + 1]);
	}

	/**
	 * Construct a new Tag with a single string as content.
	 */
	public Tag(String name, String content) {
		this.name = name;
		addContent(content);
	}

	/**
	 * Add a new attribute.
	 */
	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}

	/**
	 * Add a new date attribute. The date is formatted as the SimpleDateFormat
	 * describes at the top of this class.
	 */
	public void addAttribute(String key, Date value) {
		attributes.put(key, format.format(value));
	}

	/**
	 * Add a new content string.
	 */
	public void addContent(String string) {
		content.addElement(string);
	}

	/**
	 * Add a new content tag.
	 */
	public void addContent(Tag tag) {
		content.addElement(tag);
	}

	/**
	 * Return the name of the tag.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the attribute value.
	 */
	public String getAttribute(String key) {
		return (String) attributes.get(key);
	}

	/**
	 * Return the attribute value or a default if not defined.
	 */
	public String getAttribute(String key, String deflt) {
		String answer = getAttribute(key);
		return answer == null ? deflt : answer;
	}

	/**
	 * Answer the attributes as a Dictionary object.
	 */
	public Dictionary getAttributes() {
		return attributes;
	}

	/**
	 * Return the contents.
	 */
	public Vector getContents() {
		return content;
	}

	/**
	 * Return a string representation of this Tag and all its children
	 * recursively.
	 */
	public String toString() {
		StringWriter sw = new StringWriter();
		print(0, new PrintWriter(sw));
		return sw.toString();
	}

	/**
	 * Return only the tags of the first level of descendants that match the
	 * name.
	 */
	public Vector getContents(String tag) {
		Vector out = new Vector();
		for (Enumeration e = content.elements(); e.hasMoreElements();) {
			Object o = e.nextElement();
			if (o instanceof Tag && ((Tag) o).getName().equals(tag))
				out.addElement(o);
		}
		return out;
	}

	/**
	 * Return the whole contents as a String (no tag info and attributes).
	 */
	public String getContentsAsString() {
		StringBuffer sb = new StringBuffer();
		getContentsAsString(sb);
		return sb.toString();
	}

	/**
	 * convenient method to get the contents in a StringBuffer.
	 */
	public void getContentsAsString(StringBuffer sb) {
		for (Enumeration e = content.elements(); e.hasMoreElements();) {
			Object o = e.nextElement();
			if (o instanceof Tag)
				((Tag) o).getContentsAsString(sb);
			else
				sb.append(o.toString());
		}
	}

	/**
	 * Print the tag formatted to a PrintWriter.
	 */
	public void print(int indent, PrintWriter pw) {
		pw.print("\n");
		spaces(pw, indent);
		pw.print('<');
		pw.print(name);
		for (Enumeration e = attributes.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = escape((String) attributes.get(key));
			pw.print(' ');
			pw.print(key);
			pw.print("=\"");
			pw.print(value);
			pw.print('"');
		}
		if (content.size() == 0)
			pw.print('/');
		else {
			pw.print('>');
			for (Enumeration e = content.elements(); e.hasMoreElements();) {
				Object content = e.nextElement();
				if (content instanceof String) {
					formatted(pw, indent + 2, 60, escape((String) content));
				}
				else
					if (content instanceof Tag) {
						Tag tag = (Tag) content;
						tag.print(indent + 2, pw);
					}
			}
			pw.print("\n");
			spaces(pw, indent);
			pw.print("</");
			pw.print(name);
		}
		pw.print('>');
	}

	/**
	 * Convenience method to print a string nicely and does character conversion
	 * to entities.
	 */
	void formatted(PrintWriter pw, int left, int width, String s) {
		int pos = width + 1;
		s = s.trim();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (i == 0 || (Character.isWhitespace(c) && pos > width - 3)) {
				pw.print("\n");
				spaces(pw, left);
				pos = 0;
			}
			switch (c) {
				case '<' :
					pw.print("&lt;");
					pos += 4;
					break;
				case '>' :
					pw.print("&gt;");
					pos += 4;
					break;
				case '&' :
					pw.print("&amp;");
					pos += 5;
					break;
				default :
					pw.print(c);
					pos++;
					break;
			}
		}
	}

	/**
	 * Escape a string, do entity conversion.
	 */
	String escape(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '<' :
					sb.append("&lt;");
					break;
				case '>' :
					sb.append("&gt;");
					break;
				case '&' :
					sb.append("&amp;");
					break;
				default :
					sb.append(c);
					break;
			}
		}
		return sb.toString();
	}

	/**
	 * Make spaces.
	 */
	void spaces(PrintWriter pw, int n) {
		while (n-- > 0)
			pw.print(' ');
	}
}
