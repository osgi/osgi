/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.condpermadmin;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Condition representation used by the Conditional Permission Admin service.
 * 
 * <p>
 * This class encapsulates two pieces of information: a Condition <i>type</i>
 * (class name), which must implement <tt>Condition</tt>, and the arguments
 * passed to its constructor.
 * 
 * <p>
 * In order for a Condition represented by a <tt>ConditionInfo</tt> to be
 * instantiated and considered during a permission check, its Condition class
 * must be available from the system classpath.
 * 
 */

public class ConditionInfo {

	private String type;

	private String args[];

	/**
	 * Constructs a <tt>ConditionInfo</tt> from the given type and args.
	 * 
	 * @param type
	 *            The fully qualified class name of the condition represented by
	 *            this <tt>ConditionInfo</tt>. The class must implement
	 *            <tt>Condition</tt> and must define a constructor that takes
	 *            a <tt>Bundle</tt> and the correct number of argument
	 *            strings.
	 * 
	 * @param args
	 *            The arguments that will be passed to the constructor of the
	 *            <tt>Conditon</tt> class identified by <tt>type</tt>.
	 * 
	 * @exception java.lang.NullPointerException
	 *                if <tt>type</tt> is <tt>null</tt>.
	 */
	public ConditionInfo(String type, String args[]) {
		this.type = type;
		this.args = args != null ? args : new String[0];
		if (type == null) {
			throw new NullPointerException("type is null");
		}
	}

	/**
	 * Constructs a <tt>ConditionInfo</tt> object from the given encoded
	 * <tt>ConditionInfo</tt> string.
	 * 
	 * @param encodedCondition
	 *            The encoded <tt>ConditionInfo</tt>.
	 * @see #getEncoded
	 * @exception java.lang.IllegalArgumentException
	 *                if <tt>encodedCondition</tt> is not properly formatted.
	 */
	public ConditionInfo(String encodedCondition) {
		if (encodedCondition == null) {
			throw new NullPointerException("missing encoded condition");
		}
		if (encodedCondition.length() == 0) {
			throw new IllegalArgumentException("empty encoded condition");
		}
		try {
			char[] encoded = encodedCondition.toCharArray();
			int length = encoded.length;
			int pos = 0;
			
			/* skip whitespace */
			while (Character.isWhitespace(encoded[pos])) {
				pos++;
			}
			
			/* the first character must be '[' */
			if (encoded[pos] != '[') {
				throw new IllegalArgumentException(
						"expecting open bracket");
			}
			pos++;

			/* skip whitespace */
			while (Character.isWhitespace(encoded[pos])) {
				pos++;
			}
			
			/* type is not quoted or encoded */
			int begin = pos;
			while (!Character.isWhitespace(encoded[pos]) && (encoded[pos] != ']')) {
				pos++;
			}
			if (pos == begin || encoded[begin] == '"') {
				throw new IllegalArgumentException("expecting type");
			}
			this.type = new String(encoded, begin, pos - begin);
			
			/* skip whitespace */
			while (Character.isWhitespace(encoded[pos])) {
				pos++;
			}
			
			/* type may be followed by args which are quoted and encoded */
			ArrayList argsList = new ArrayList();
			while (encoded[pos] == '"') {
				pos++;
				begin = pos;
				while (encoded[pos] != '"') {
					if (encoded[pos] == '\\') {
						pos++;
					}
					pos++;
				}
				argsList.add(unescapeString(encoded, begin, pos));
				pos++;

				if (Character.isWhitespace(encoded[pos])) {
					/* skip whitespace */
					while (Character.isWhitespace(encoded[pos])) {
						pos++;
					}
				}
			}
			this.args = (String[]) argsList.toArray(new String[argsList.size()]);
			
			/* the final character must be ')' */
			char c = encoded[pos];
			pos++;
			while ((pos < length) && Character.isWhitespace(encoded[pos])) {
				pos++;
			}
			if ((c != ']') || (pos != length)) {
				throw new IllegalArgumentException("expecting close bracket");
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("parsing terminated abruptly");
		}
	}

	/**
	 * Returns the string encoding of this <tt>ConditionInfo</tt> in a form
	 * suitable for restoring this <tt>ConditionInfo</tt>.
	 * 
	 * <p>
	 * The encoding format is:
	 * 
	 * <pre>
	 * 
	 *  [type &quot;arg0&quot; &quot;arg1&quot; ...]
	 *  
	 * </pre>
	 * 
	 * where <i>argX</i> are strings that are encoded for proper parsing.
	 * Specifically, the <tt>"</tt>, <tt>\</tt>, carriage return, and
	 * linefeed characters are escaped using <tt>\"</tt>, <tt>\\</tt>,
	 * <tt>\r</tt>, and <tt>\n</tt>, respectively.
	 * 
	 * <p>
	 * The encoded string must contain no leading or trailing whitespace
	 * characters. A single space character must be used between type and "<i>arg0</i>"
	 * and between all arguments.
	 * 
	 * @return The string encoding of this <tt>ConditionInfo</tt>.
	 */
	public final String getEncoded() {
		StringBuffer output = new StringBuffer();
		output.append('[');
		output.append(type);

		for (int i = 0; i < args.length; i++) {
			output.append(" \"");
			escapeString(args[i], output);
			output.append('\"');
		}

		output.append(']');

		return (output.toString());
	}

	/**
	 * Returns the string representation of this <tt>ConditionInfo</tt>. The
	 * string is created by calling the <tt>getEncoded</tt> method on this
	 * <tt>ConditionInfo</tt>.
	 * 
	 * @return The string representation of this <tt>ConditionInfo</tt>.
	 */
	public String toString() {
		return (getEncoded());
	}

	/**
	 * Returns the fully qualified class name of the condition represented by
	 * this <tt>ConditionInfo</tt>.
	 * 
	 * @return The fully qualified class name of the condition represented by
	 *         this <tt>ConditionInfo</tt>.
	 */
	public final String getType() {
		return (type);
	}

	/**
	 * Returns arguments of this <tt>ConditionInfo</tt>.
	 * 
	 * @return The arguments of this <tt>ConditionInfo</tt>.  An empty array
	 * is returned if the <tt>ConditionInfo</tt> has no arguments.
	 */
	public final String[] getArgs() {
		return (args);
	}

	/**
	 * Determines the equality of two <tt>ConditionInfo</tt> objects.
	 * 
	 * This method checks that specified object has the same type and args as
	 * this <tt>ConditionInfo</tt> object.
	 * 
	 * @param obj
	 *            The object to test for equality with this
	 *            <tt>ConditionInfo</tt> object.
	 * @return <tt>true</tt> if <tt>obj</tt> is a <tt>ConditionInfo</tt>,
	 *         and has the same type and args as this <tt>ConditionInfo</tt>
	 *         object; <tt>false</tt> otherwise.
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return (true);
		}

		if (!(obj instanceof ConditionInfo)) {
			return (false);
		}

		ConditionInfo other = (ConditionInfo) obj;

		if (!type.equals(other.type) || args.length != other.args.length)
			return false;

		for (int i = 0; i < args.length; i++) {
			if (!args[i].equals(other.args[i]))
				return false;
		}
		return true;
	}

	/**
	 * Returns the hash code value for this object.
	 * 
	 * @return A hash code value for this object.
	 */

	public int hashCode() {
		int hash = type.hashCode();

		for (int i = 0; i < args.length; i++) {
			hash ^= args[i].hashCode();
		}
		return (hash);
	}

	/**
	 * This escapes the quotes, backslashes, \n, and \r in the string using a
	 * backslash and appends the newly escaped string to a StringBuffer.
	 */
	private static void escapeString(String str, StringBuffer output) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			switch (c) {
				case '"' :
				case '\\' :
					output.append('\\');
					output.append(c);
					break;
				case '\r' :
					output.append("\\r");
					break;
				case '\n' :
					output.append("\\n");
					break;
				default :
					output.append(c);
					break;
			}
		}
	}

	/**
	 * Takes an encoded character array and decodes it into a new String.
	 */
	private static String unescapeString(char[] str, int begin, int end) {
		StringBuffer output = new StringBuffer(end - begin);
		for (int i = begin; i < end; i++) {
			char c = str[i];
			if (c == '\\') {
				i++;
				if (i < end) {
					c = str[i];
					switch (c) {
						case '"' :
						case '\\' :
							break;
						case 'r' :
							c = '\r';
							break;
						case 'n' :
							c = '\n';
							break;
						default :
							c = '\\';
							i--;
							break;
					}
				}
			}
			output.append(c);
		}
		
		return output.toString();
	}
}
