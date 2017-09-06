/*
 * Copyright (c) OSGi Alliance (2004, 2016). All Rights Reserved.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

@SuppressWarnings("javadoc")
public class HtmlCleaner {
	StringTokenizer				in;
	Stack<String>				pushed		= new Stack<String>();
	int							pushedType	= 0;
	String						pushedTag	= null;
	String						source;
	int							cnt			= 0;
	int							line		= 0;
	String						tokens[]	= new String[16];
	int							rover;
	String						file;

	int							type		= 0;
	String						tag			= null;
	String						token		= null;
	boolean						eof;

	final static int			CHAR		= 1;
	final static int			PARA		= 2;
	final static int			LIST		= 4;
	final static int			TABLE		= 8;
	final static int			ROW			= 16;
	final static int			COLUMN		= 32;
	final static int			TEXT		= 64;
	final static int			ITEM		= 128;
	final static int			CLOSE		= 256;
	final static int			SINGLE		= 512;

	static Map<String, Integer>	descr		= new HashMap<String, Integer>();

	static {
		descr.put("h1", Integer.valueOf(PARA));
		descr.put("h2", Integer.valueOf(PARA));
		descr.put("h3", Integer.valueOf(PARA));
		descr.put("h4", Integer.valueOf(PARA));
		descr.put("h5", Integer.valueOf(PARA));
		descr.put("h6", Integer.valueOf(CHAR));
		descr.put("h7", Integer.valueOf(PARA));
		descr.put("h8", Integer.valueOf(PARA));
		descr.put("p", Integer.valueOf(PARA));
		descr.put("pre", Integer.valueOf(PARA));
		descr.put("em", Integer.valueOf(CHAR));
		descr.put("i", Integer.valueOf(CHAR));
		descr.put("b", Integer.valueOf(CHAR));
		descr.put("strong", Integer.valueOf(CHAR));
		descr.put("tt", Integer.valueOf(CHAR));
		descr.put("code", Integer.valueOf(CHAR));
		descr.put("big", Integer.valueOf(CHAR));
		descr.put("q", Integer.valueOf(CHAR));
		descr.put("small", Integer.valueOf(CHAR));
		descr.put("sub", Integer.valueOf(CHAR));
		descr.put("sup", Integer.valueOf(CHAR));
		descr.put("ul", Integer.valueOf(LIST));
		descr.put("ol", Integer.valueOf(LIST));
		descr.put("dl", Integer.valueOf(LIST));
		descr.put("menu", Integer.valueOf(LIST));
		descr.put("li", Integer.valueOf(ITEM));
		descr.put("dd", Integer.valueOf(ITEM));
		descr.put("dt", Integer.valueOf(ITEM));
		descr.put("table", Integer.valueOf(TABLE));
		descr.put("tr", Integer.valueOf(ROW));
		descr.put("th", Integer.valueOf(COLUMN));
		descr.put("td", Integer.valueOf(COLUMN));
		descr.put("br", Integer.valueOf(SINGLE));
		descr.put("a", Integer.valueOf(CHAR));
	}

	StringBuilder result = new StringBuilder();

	public HtmlCleaner(String file, String s) {
		this.file = file;
		source = s;
		in = new StringTokenizer(s, "<>", true);
	}

	public String clean() {
		try {
			result = new StringBuilder();
			while (!eof) {
				next();
				element(-1);
				if (!eof) {
					// wrong!
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < tokens.length; i++) {
						sb.append(" ");
						String tmp = tokens[(i + rover) % tokens.length];
						if (tmp != null)
							sb.append(tmp);
					}
					next();
					sb.append(token);
					error("Invalid tag on top level: "
							+ tokens[rover % tokens.length]);
				}
			}
			return result.toString();
		} catch (Throwable e) {
			System.out.println("Error " + source);
			e.printStackTrace();
			error("Cannot parse text");
			return "XXX" + escape(source);
		}
	}

	String escape(String s) {
		StringBuilder sb = new StringBuilder();
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
				case '\'' :
					sb.append("&quot;");
					break;
				case '"' :
					sb.append("&quot;");
					break;
				default :
					sb.append(c);
			}
		}
		return sb.toString().trim();
	}

	// on entry = token is first element
	void element(int allowed) {
		String current = tag;

		while (!eof) {
			if (type == CLOSE) {
				return;
			}

			if ((type & allowed) == 0) {
				// System.out.println( "Not allowed now " + token + " in " +
				// current );
				push();
				return;
			}

			switch (type) {
				case SINGLE :
					result.append("<" + tag.toLowerCase() + "/>");
					break;

				case TEXT :
					result.append(token);
					break;

				case PARA :
					element(token, CHAR + TEXT + SINGLE);
					break;

				case CHAR :
					element(token, TEXT + SINGLE);
					break;

				case TABLE :
					element(token, ROW + TEXT);
					break;

				case ROW :
					element(token, COLUMN + TEXT);
					break;

				case COLUMN :
					element(token, CHAR + TEXT + SINGLE);
					break;

				case LIST :
					element(token, ITEM + TEXT + SINGLE);
					break;

				case ITEM :
					element(token, CHAR + TEXT + SINGLE + LIST);
					break;

				default :
					error("Unexpected element " + current);
					return;
			}
			next();
		}
	}

	void element(String t, int allowed) {
		// System.out.print( "<" + tag + ">" );
		String current = t;
		result.append("<" + getTag(current) + getRemainder(current) + ">");
		next();
		if (t.equalsIgnoreCase("pre")) {
			StringBuilder previous = result;
			result = new StringBuilder();
			element(allowed);
			previous.append(pre(result.toString()));
			result = previous;
		} else
			element(allowed);
		result.append("</" + getTag(current) + ">");
		// System.out.print( "</" + current + ">" );
	}

	void next() {
		tokens[rover++ % tokens.length] = token;
		if (!pushed.empty()) {
			token = pushed.pop();
			// System.out.println( "pushed " + token );
			return;
		} else if (!in.hasMoreTokens()) {
			token = "";
			eof = true;
			// System.out.println( "eof" );
			return;
		} else {
			token = in.nextToken();
			count(token);
		}
		// result.append( "<!-- TOKEN " + token + "-->" );

		if (token.equals(">")) {
			type = TEXT;
			token = "&gt;";
			return;
		}

		if (!token.equals("<")) {
			type = TEXT;
			return;
		}

		String content = in.nextToken();
		count(content);
		// result.append( "<!-- TAG '" + content + "'-->" );
		char first = content.charAt(0);
		if (!Character.isLetter(first) && first != '/') {
			type = TEXT;
			token = "&lt;" + escape(content);
			return;
		}

		String stop = in.nextToken();
		count(stop);
		// result.append( "<!-- STOP '" + stop + "'-->" );
		if (!stop.equals(">")) {
			error("Expected close");
			token = escape("<" + content + stop);
			type = TEXT;
			return;
		}

		if (content.startsWith("/")) {
			type = CLOSE;
			token = content.substring(1).trim();
			tag = getTag(token);
			// System.out.println( "close " + tag );
			return;
		} else if (content.endsWith("/")) {
			type = SINGLE;
			token = tidy(content.substring(0, content.length() - 1));
			tag = getTag(token);
			// System.out.println( "single " + tag );
			return;
		} else {
			token = tidy(content);
			tag = getTag(token);
			Integer t = descr.get(tag);
			if (t != null)
				type = t.intValue();
			else {
				error("Unexpected tag " + token + ", assume para");
				type = PARA;
			}
			// System.out.println( "tag " + tag + " " + t );
			return;
		}
	}

	String getTag(String s) {
		StringTokenizer st = new StringTokenizer(s, " ");
		return st.nextToken().toLowerCase();
	}

	String getRemainder(String s) {
		int n = s.indexOf(" ");
		if (n < 0)
			return "";

		return s.substring(n);
	}

	void push() {
		pushed.push(token);
	}

	void error(String msg) {
		// System.out.println( msg );
		result.append("\n<formattingerror msg='" + escape(msg) + "' line='"
				+ line + "' file='" + file + "' cnt='" + cnt + "' token='"
				+ escape(token) + "'><![CDATA[" + source
				+ "]]></formattingerror>\n");
	}

	String pre(String s) {
		StringBuilder sb = new StringBuilder();
		while (s.startsWith("\n") || s.startsWith("\r"))
			s = s.substring(1);
		while (s.endsWith("\n") || s.endsWith("\r") || s.endsWith(" "))
			s = s.substring(0, s.length() - 1);

		boolean inside = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '<' :
					inside = true;
					sb.append(c);
					break;
				case '>' :
					inside = false;
					sb.append(c);
					break;

				case ' ' :
					if (inside)
						sb.append(" ");
					else
						sb.append("&#8194;");
					break;
				case '\n' :
					sb.append("<br/>");
					break;

				default :
					sb.append(c);
			}
		}
		return sb.toString();
	}

	static String tidy(String in) {
		StringBuilder sb = new StringBuilder();
		in = in.replace('\t', ' ').trim();
		int n = in.indexOf(' ');
		if (n < 0)
			n = in.length();

		sb.append(in.substring(0, n).trim());

		String remainder = in.substring(n).trim();
		while (remainder.length() > 0) {
			int k = remainder.indexOf('=');
			if (k > 0) {
				sb.append(" ");
				sb.append(remainder.substring(0, k).trim());
				sb.append("=");

				remainder = remainder.substring(k + 1).trim();
				char del = remainder.charAt(0);
				char use = del;
				if (del == '\'' || del == '"')
					remainder = remainder.substring(1);
				else {
					del = ' ';
					use = '"';
				}
				int e = remainder.indexOf(del);
				if (e == -1)
					e = remainder.length();

				sb.append(use);
				sb.append(remainder.substring(0, e));
				sb.append(use);
				if (e >= remainder.length())
					break;

				remainder = remainder.substring(e + 1).trim();
			} else
				throw new RuntimeException("Not a valid element " + in);
		}
		return sb.toString();
	}

	void count(String s) {
		cnt += s.length();
		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) == '\n') {
				line++;
				cnt = 0;
			} else
				cnt++;
	}

	public static void main(String args[]) {

		String t1 = " abc def = ghi";
		String t2 = " abc def = 'ghi' klm=0";
		String t3 = " abc def =\"g'h'i\"";
		String t4 = " abc def =\"g'h'i\"";
		String t5 = "abc def =\"g'h'i\"";
		String t6 = "ab-c def=\"g'h'i\"";
		System.out.println(t1 + " - " + tidy(t1));
		System.out.println(t2 + " - " + tidy(t2));
		System.out.println(t3 + " - " + tidy(t3));
		System.out.println(t4 + " - " + tidy(t4));
		System.out.println(t5 + " - " + tidy(t5));
		System.out.println(t6 + " - " + tidy(t6));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			sb.append(" ");
		}
		System.out.println(new HtmlCleaner("<>", sb.toString()).clean());
	}

}
