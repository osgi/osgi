package org.osgi.test.cases.upnp.tbc.parser;

import java.util.*;

/**
 * 
 * 
 * @author
 * @version
 * @since
 */
class LStack extends Vector {
	Object peek() {
		return elementAt(0);
	}

	synchronized Object pop() {
		Object o = elementAt(0);
		removeElementAt(0);
		return o;
	}

	void push(Object o) {
		insertElementAt(o, 0);
	}
}

public class XMLParser {
	private String			_xml;
	private int				position;
	private LStack			stack;
	private XMLTag			rootTag;
	private LStack			tags;
	private StringBuffer	_dtd;

	//  private XMLDTD dtd;
	public XMLParser(String xml) throws XMLException {
		this._xml = xml;
		stack = new LStack();
		tags = new LStack();
		_dtd = new StringBuffer();
		try {
			parseXML();
			validateXML();
		}
		catch (XMLException exc) {
			rootTag = null;
			throw exc;
		}
	}

	private void validateXML() throws XMLException {
	}

	private void parseXML() throws XMLException {
		//    System.out.println(_xml);
		StringBuffer xml = new StringBuffer(_xml.trim());
		for (position = 0; position < xml.length(); position++) {
			//      System.out.println("Cycle Stack[" + position + "]: " + stack);
			char next = xml.charAt(position);
			if (next == '<') {
				position++;
				if (xml.charAt(position) == '/') {
					position++;
					String name = readTagName(xml);
					if (name == null) {
						throw new XMLException(
								"End of xml reached, but tag not closed");
					}
					char nch = xml.charAt(position);
					if (nch == ' ' || nch == '/') {
						throw new XMLException(
								"Closing tag can't has attributes and be selfclosed.Tag Name: "
										+ name);
					}
					if (stack.isEmpty() || !name.equals((String) stack.peek())) {
						throw new XMLException(
								"Closed not last opened tag.Tag Name: " + name);
					}
					tags.pop();
					stack.pop();
				}
				else
					if (xml.charAt(position) == '!') {
						//dtd
						position++;
						_dtd.append("<!");
						while (position < xml.length()) {
							char cur = xml.charAt(position);
							_dtd.append(cur);
							if (cur == '>') {
								break;
							}
							position++;
						}
						position++;
						continue;
					}
					else
						if (xml.charAt(position) == '?') {
							//version
							position++;
							while (position < xml.length()) {
								if ((xml.charAt(position) == '>')
										&& (xml.charAt(position - 1) == '?')) {
									break;
								}
								position++;
							}
							position++;
							continue;
						}
						else {
							String name = readTagName(xml);
							if (name == null) {
								throw new XMLException(
										"End of xml reached, but tag not closed");
							}
							XMLTag tag = new XMLTag(name);
							if (tags.isEmpty()) {
								rootTag = tag;
							}
							else {
								XMLTag parent = (XMLTag) tags.peek();
								parent.appendSubTag(tag);
							}
							char nch = xml.charAt(position);
							if (nch == ' ') {
								position++;
								Hashtable hash = readAttributes(xml);
								if (hash == null) {
									throw new XMLException(
											"Error parsing attributes.Tag Name: "
													+ name);
								}
								tag.setHash(hash);
							}
							nch = xml.charAt(position);
							if (nch == '/' && xml.charAt(position + 1) == '>') {
								position++;
								continue;
							}
							stack.push(name);
							tags.push(tag);
						}
			}
			else
				if (next == '"') {
					XMLTag tag = (XMLTag) tags.peek();
					tag.appendChar(next);
					position++;
					while (position < xml.length()) {
						char b = xml.charAt(position);
						if ((b == '"') && (xml.charAt(position - 1) != '\\')) {
							tag.appendChar(b);
							break;
						}
						else
							if ((b == '"')
									&& (xml.charAt(position - 1) == '\\')
									&& (xml.charAt(position - 2) == '\\')) {
								tag.appendChar(b);
								break;
							}
						if (b == '\\' && xml.charAt(position + 1) == '\\') {
							tag.appendChar(b);
							position++;
						}
						else
							if (b == '\\' && xml.charAt(position + 1) == '"') {
								position++;
								tag.appendChar(xml.charAt(position));
							}
							else {
								tag.appendChar(b);
							}
						position++;
					}
				}
				else {
					if (!stack.isEmpty()) {
						XMLTag tag = (XMLTag) tags.peek();
						tag.appendChar(next);
					}
				}
		}
		if (!stack.isEmpty()) {
			throw new XMLException("Some opened tags not closed.Name of last: "
					+ (String) stack.peek());
		}
		cleanUP(rootTag);
	}

	private void cleanUP(XMLTag tag) {
		tag.cleanUP();
		Vector cont = tag.getContent();
		for (int i = 0; i < cont.size(); i++) {
			Object cur = cont.elementAt(i);
			if (cur instanceof XMLTag) {
				cleanUP((XMLTag) cur);
			}
		}
	}

	private String readTagName(StringBuffer sb) throws XMLException {
		StringBuffer name = new StringBuffer();
		char ch;
		while (position < sb.length()) {
			ch = sb.charAt(position);
			if (ch == ' ') {
				break;
			}
			if (ch == '/') {
				break;
			}
			if (ch == '>') {
				break;
			}
			name.append(ch);
			position++;
			if (position >= sb.length()) {
				return null;
			}
		}
		return name.toString();
	}

	private Hashtable readAttributes(StringBuffer sb) throws XMLException {
		Hashtable hash = new Hashtable();
		StringBuffer nameAtt = new StringBuffer();
		StringBuffer valueAtt = new StringBuffer();
		char ch;
		boolean name = false;
		boolean value = false;
		boolean change = false;
		while (position < sb.length()) {
			ch = sb.charAt(position);
			if (ch == '/') {
				if (value) {
					value = false;
				}
				if (name) {
					name = false;
				}
				break;
			}
			if (ch == '>') {
				if (value) {
					value = false;
				}
				if (name) {
					name = false;
				}
				break;
			}
			if (ch == ' ') {
				if (change) {
					throw new XMLException("= without value in attributes");
				}
				if (value) {
					value = false;
				}
			}
			else
				if (ch == '=') {
					if (name) {
						name = false;
						change = true;
					}
					else
						if (change) {
							throw new XMLException("Double = in attributes");
						}
				}
				else
					if (ch == '"') {
						if (change) {
							change = false;
							position++;
							while (position < sb.length()) {
								char b = sb.charAt(position);
								if ((b == '"')
										&& (sb.charAt(position - 1) != '\\')) {
									break;
								}
								else
									if ((b == '"')
											&& (sb.charAt(position - 1) == '\\')
											&& (sb.charAt(position - 2) == '\\')) {
										break;
									}
								if (b == '\\'
										&& sb.charAt(position + 1) == '\\') {
									valueAtt.append(b);
									position++;
								}
								else
									if (b == '\\'
											&& sb.charAt(position + 1) == '"') {
										position++;
										valueAtt.append(sb.charAt(position));
									}
									else {
										valueAtt.append(b);
									}
								position++;
							}
							value = true;
							hash.put(nameAtt.toString(), valueAtt.toString());
							nameAtt.setLength(0);
							valueAtt.setLength(0);
						}
					}
					else {
						nameAtt.append(ch);
						name = true;
					}
			position++;
		}
		if (!(change || name || value)) {
			return hash;
		}
		else {
			return null;
		}
	}

	public XMLTag getRootXMLTag() {
		return rootTag;
	}

	public String getXMLString() {
		return getXMLTagString(rootTag, 0);
	}

	private String getXMLTagString(XMLTag tag, int indent) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			sb.append(" ");
		}
		StringBuffer result = new StringBuffer();
		result.append(sb + "<" + tag.getName());
		Dictionary attrlist = tag.getAttributes();
		if (attrlist != null) {
			Enumeration enum = attrlist.keys();
			while (enum.hasMoreElements()) {
				String next = (String) enum.nextElement();
				result.append(" " + next + "=\"" + attrlist.get(next) + "\"");
			}
		}
		Vector vec = tag.getContent();
		if (vec.isEmpty()) {
			result.append("/>\r\n");
			return result.toString();
		}
		result.append(">");
		int sz = vec.size();
		sb.append("    ");
		if (sz == 1 && vec.elementAt(0) instanceof StringBuffer) {
			result.append(vec.elementAt(0).toString());
			result.append("</" + tag.getName() + ">\r\n");
		}
		else {
			result.append("\r\n");
			for (int i = 0; i < sz; i++) {
				Object next = vec.elementAt(i);
				if (next instanceof StringBuffer) {
					if (next.toString().trim().length() != 0) {
						result.append(sb.toString() + next + "\r\n");
					}
				}
				else {
					result.append(getXMLTagString((XMLTag) next, indent + 4));
				}
			}
			sb.setLength(sb.length() - 4);
			result.append(sb + "</" + tag.getName() + ">\r\n");
		}
		return result.toString();
	}
}