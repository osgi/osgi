/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.director;

import java.net.URL;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.script.*;
import org.osgi.test.service.*;

/**
 * Keep track of a script.
 * 
 * The script content is a URL. This object is registered in the registry and
 * acts as a TestCase. When is executed, it will actually run the script
 * content. Note that Run does not call test, but detects this class instead.
 */
public class ScriptEditor implements TestCase {
	String				name;
	URL					path;
	ServiceRegistration	registration;

	public ScriptEditor(String name, URL path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public URL getPath() {
		return path;
	}

	public String getDescription() {
		return name + " - " + path;
	}

	public String getIconName() {
		return null;
	}

	public int test(TestRun run) {
		throw new RuntimeException("Should not be called");
	}

	public Tag execute() throws Exception {
		Script script = new Script(path);
		return script.execute();
	}

	public void abort() {
	}

	public void delete() {
		registration.unregister();
	}

	public Tag getContent() throws Exception {
		Script script = new Script(path);
		return script.getScript();
	}
}
