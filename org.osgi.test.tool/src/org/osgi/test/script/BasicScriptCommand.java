/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) OSGi 2001 
 */
package org.osgi.test.script;

import java.util.Enumeration;

/**
 * Provides the basic scripting commands. It is registered always as a script
 * provider.
 */
class BasicScriptCommand {
	/**
	 * Default entry for a script.
	 */
	public Tag _script(ScriptContext context, Tag tag) throws Exception {
		return executeContent(context, tag);
	}

	/**
	 * Run a script but catch exceptions and continue.
	 */
	public Tag _catch(ScriptContext context, Tag tag) throws Exception {
		try {
			return executeContent(context, tag);
		}
		catch (Exception e) {
			return new Tag("caught", "" + e);
		}
	}

	/**
	 * Convenience method to execute a content of a tag.
	 */
	public Tag executeContent(ScriptContext context, Tag tag) throws Exception {
		Tag result = new Tag(tag.getName());
		for (Enumeration e = tag.getContents().elements(); e.hasMoreElements();) {
			Object content = e.nextElement();
			if (content instanceof String) {
				String s = content.toString().trim();
				if (s.length() != 0)
					result.addContent(new Tag("comment", content.toString()));
			}
			else {
				Tag line = (Tag) content;
				Tag out = context.execute(context, line);
				result.addContent((Tag) out);
			}
		}
		return result;
	}
	
}
