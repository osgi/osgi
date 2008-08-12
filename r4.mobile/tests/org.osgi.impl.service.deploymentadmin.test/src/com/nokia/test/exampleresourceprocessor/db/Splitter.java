/*
 * copyright (c) 2004 Nokia, all rights reserved, COMPANY CONFIDENTAL
 *
 * author: Ivan Zahoranszky
 */
package com.nokia.test.exampleresourceprocessor.db;

import java.util.Vector;

public class Splitter {
	public static String[] split(String input, char sep, int limit) {
		Vector v = new Vector();
		boolean limited = (limit > 0);
		int applied = 0;
		int index = 0;
		StringBuffer part = new StringBuffer();
		while (index < input.length()) {
			char ch = input.charAt(index);
			if (ch != sep)
				part.append(ch);
			else {
				++applied;
				v.add(part.toString());
				part = new StringBuffer();
			}
			++index;
			if (limited && applied == limit - 1)
				break;
		}
		while (index < input.length()) {
			char ch = input.charAt(index);
			part.append(ch);
			++index;
		}
		v.add(part.toString());
		int last = v.size();
		if (0 == limit) {
			for (int j = v.size() - 1; j >= 0; --j) {
				String s = (String) v.elementAt(j);
				if ("".equals(s))
					--last;
				else
					break;
			}
		}
		String[] ret = new String[last];
		for (int i = 0; i < last; ++i)
			ret[i] = (String) v.elementAt(i);
		return ret;
	}
}
