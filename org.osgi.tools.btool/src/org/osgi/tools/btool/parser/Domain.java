package org.osgi.tools.btool.parser;

public interface Domain {
	String getValue(String key);
	void warning(String s);
	void error(String s);
}
