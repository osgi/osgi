/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.upnp.tbc.parser;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * 
 * @author
 * @version
 * @since
 */
public class XMLTag {
	private String		name;
	private Hashtable<String,String>	attributes;
	private Vector<Object>	content;

	public XMLTag(String name) {
		this.name = name;
		content = new Vector<>();
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

	void setHash(Hashtable<String,String> hash) {
		attributes = hash;
	}

	public String getName() {
		return name;
	}

	public Dictionary<String,String> getAttributes() {
		return attributes;
	}

	public Vector<Object> getContent() {
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
