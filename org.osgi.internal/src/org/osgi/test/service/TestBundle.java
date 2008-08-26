/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.service;

import java.io.*;

/**
   The TestBundle interface is used by the TestCase to manipulate a TestBundle.
   It also provides a communication channel to the bundle installed on the target framework.
   It's created by the TestRun using the createTestBundle() method.
*/

public interface TestBundle
{
	/**
		Retrieve the name of the TestBundle.
		
		@return		The name of the TestBundle
	*/
	public String getName();
	
	
	/**
		Sends an object to the test bundle running on the target system.
		
		@param	o		The object being sent (must be serializable)
	*/
	public void send(Object o) throws IOException;
	
	
	/**
		Receives an object from the test bundle running on the target.
		
		@param	timeout	Timeout in milliseconds.		
		@return			The object received, or null if it timed out.
	*/
	public Object receive(long timeout) throws IOException;
	
	
	/**
		Retrieve the number of mismatches between 
		the reference output and the
		log stream detected so far.
		
		@return		The number of errors detected.
	*/
	public int getCompareErrors();
	
	/**
		Uninstall the testbundle from the target.
	*/
	public void uninstall() throws IOException;
}
