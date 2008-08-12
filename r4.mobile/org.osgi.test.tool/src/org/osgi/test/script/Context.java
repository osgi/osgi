/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) OSGi 2001 
 */
package org.osgi.test.script;

/**
 * Provides the context of a script. A context can provide variables, input
 * parameters and a user for running a script.
 */
class Context implements ScriptContext {
	Tag		input;
	Script	script;

	Context(Script script, Tag input) {
		this.script = script;
		this.input = input;
	}

	public String getUser() {
		return null;
	}

	public Object getVariable(String var) {
		return script.variables.get(var);
	}

	public void setVariable(String key, Object value) {
		script.variables.put(key, value);
	}

	public Tag execute(ScriptContext ctxt, Tag tag) throws Exception {
		return script.execute(ctxt, tag);
	}

	public Tag getInput() {
		return input;
	}
}
