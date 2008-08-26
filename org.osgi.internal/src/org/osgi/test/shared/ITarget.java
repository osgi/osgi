/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.io.*;
import java.util.Dictionary;

public interface ITarget {
	void linkClosed();

	void install(String name, InputStream in) throws Exception;

	void uninstall(String name) throws Exception;

	void push(String name, Object msg);

	//TestCaseLinkImpl getTestCaseLink( String name );
	//TestCaseLinkImpl getTestCaseLink( Bundle bundle );
	void setProperties() throws IOException;

	void reboot(int cause);

	void updateFramework();

	void setTestProperties(Dictionary dictionary) throws IOException;
}
