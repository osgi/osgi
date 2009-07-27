/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) OSGi 2001 
 */
package org.osgi.test.script;

import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

/**
 * Holds a script.
 * 
 * A script is a Tag (XML) holding the script. The tag is made from a text file
 * that is obtained from a URL. It is compiled using an XML parser into the Tag
 * object.
 * <p>
 * XML is really easy to use as a script language because it provides the
 * recursive block oriented nature needed.
 * <p>
 * The command set of the script language is open ended. Any part can add and
 * remove providers. These are normal objects. Any public method starting with a
 * "_" is exported as a script command (without the "_"). Reflection is used to
 * find these methods.
 */
public class Script {
	Hashtable		variables;
	Tag				result;
	Tag				script;
	static Vector	chain	= new Vector();

	/**
	 * Add a new script command provider.
	 */
	static public void addProvider(Object provider) {
		chain.addElement(provider);
	}

	/**
	 * Remove a script command provider.
	 */
	static public void removeProvider(Object provider) {
		chain.removeElement(provider);
	}

	/**
	 * Initialize. Add the basic commands.
	 */
	static {
		chain = new Vector();
		addProvider(new BasicScriptCommand());
	}

	/**
	 * Constructor, create a new script from a URL and parse it.
	 */
	public Script(URL url) throws Exception {
		ScriptParser sp = new ScriptParser();
		script = sp.parseURI(url.toString());
	}

	/**
	 * Constructor, create a new script from a string and parse this string.
	 */
	public Script(String string) throws Exception {
		ScriptParser sp = new ScriptParser();
		script = sp.parseString(string);
	}

	/**
	 * Execute this script and return the result as a Tag.
	 */
	public Tag execute() throws Exception {
		ScriptContext ctxt = new Context(this, null);
		return (Tag) execute(ctxt, script);
	}

	/**
	 * Execute a script with a context and the tag that represents the script.
	 */
	public Tag execute(ScriptContext ctxt, Tag tag) throws Exception {
		String name = tag.getName();
		try {
			for (Enumeration e = chain.elements(); e.hasMoreElements();) {
				Object provider = e.nextElement();
				Method method = findMethod(provider.getClass(), name);
				if (method != null) {
					return (Tag) method.invoke(provider, new Object[] {ctxt,
							tag});
				}
			}
			throw new RuntimeException("No such cmd: " + name);
		}
		catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
			throw (Exception) e.getTargetException();
		}
	}

	/**
	 * Convenience method to find a method without throwing up.
	 */
	Method findMethod(Class clazz, String method) {
		try {
			return clazz.getMethod("_" + method, new Class[] {
					ScriptContext.class, Tag.class});
		}
		catch (Exception e) {
			return null;
		}
	}

	public Tag getScript() {
		return script;
	}
}
