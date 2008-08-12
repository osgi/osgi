package org.osgi.test.cases.upnp.tbc.parser;

import java.util.*;

/**
 * 
 * 
 * @author
 * @version
 * @since
 */
public class XMLTag {
	private String		name;
	private Hashtable	attributes;
	private Vector		content;

	public XMLTag(String name) {
		this.name = name;
		content = new Vector();
	}

	void appendChar(char ch) {
		if (content.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append(ch);
			content.addElement(sb);
			return;
		}
		Object top = content.lastElement();
		if (top instanceof StringBuffer) {
			((StringBuffer) top).append(ch);
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append(ch);
			content.addElement(sb);
		}
	}

	void appendSubTag(XMLTag tag) {
		Object top = content.lastElement();
		if (top instanceof StringBuffer) {
			StringBuffer sb = (StringBuffer) top;
			String bla = sb.toString();
			sb.setLength(0);
			sb.append(bla.trim());
		}
		content.addElement(tag);
	}

	void setHash(Hashtable hash) {
		attributes = hash;
	}

	public String getName() {
		return name;
	}

	public Dictionary getAttributes() {
		return attributes;
	}

	public Vector getContent() {
		return content;
	}

	void cleanUP() {
		for (int i = 0; i < content.size(); i++) {
			Object top = content.elementAt(i);
			if (top instanceof StringBuffer) {
				if (top.toString().trim().length() == 0) {
					content.removeElementAt(i);
					i--;
				}
			}
		}
	}

	public boolean hasOnlyTags() {
		int csz = content.size();
		for (int i = 0; i < csz; i++) {
			if (!(content.elementAt(i) instanceof XMLTag)) {
				return false;
			}
		}
		return true;
	}

	public boolean hasOnlyText() {
		int csz = content.size();
		for (int i = 0; i < csz; i++) {
			if (!(content.elementAt(i) instanceof StringBuffer)) {
				return false;
			}
		}
		return true;
	}
}