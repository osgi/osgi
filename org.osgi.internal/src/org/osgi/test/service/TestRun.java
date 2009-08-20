/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.service;

import java.io.*;

/**
	A TestRun controls the run of a set of testcase. It 
	is used as the interface from the test bundle running in
	the director framework. It can be used to log information
	and control the bundles in the target framework.
*/

public interface TestRun
{
	/**
		Creates a TestBundle representing the TestBundle to be installed
		on the tested framework.
		
		@param name		The name of the bundle.
		@param bundle	The FUT bundle jar file being installed on the FUT.
		@param refOut	The reference output to compare logs to.
		
		@return			The TestBundle created, or null if it fails to install and start
						the bundle on the FUT.
	*/
	TestBundle createTestBundle(String name, InputStream bundle, InputStream refOut)
		throws IOException;
	
	
	/**
		Report the progress to the TestRun.
		
		@param percent	The percentage completed so far.
		@param msg		A message to the user.
	*/
	void reportProgress(int percent) throws IOException;
	
	/**
		Report a message to the listeners.
	*/		
	void reportMessage( String msg ) throws IOException;
}
