/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) OSGi 2001 
 */
package org.osgi.test.script;

public interface ScriptContext {
	String getUser();

	Object getVariable(String var);

	void setVariable(String key, Object value);

	Tag execute(ScriptContext ctxt, Tag tag) throws Exception;

	Tag getInput();
}
