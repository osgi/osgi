/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.service;

/**
	A TestCase is a service that can execute a testcase
	on a target system.
	
	When a test bundle installs, it should register a number
	of TestCase object. These objects are picked up by the
	Director and dispatched to all the applets. The applets
	allow the end user to pick a set of testcases and run
	these in a batch.
	<p>
	Note that this service runs in the same framework as the
	director, which is NOT the target framework. This allows
	us to reuse the framework code to maintain a registry of
	testbundles.
*/

public interface TestCase
{
	/**
		The getName method returns the name of this TestCase.
		
		@return The name.
	*/
	String getName();
	
	/**
		Answer a description of this bundle.
	*/
	String getDescription();
	
	/**
		The getIconName method returns a resource name that points to a resource
		containing a bitmap.
		
		@return A resource name.
	*/
	String getIconName();


	/**
		Runs the test case on the target controlled by the TestRun indicated.

		@param	run		The TestDirector.		
		@return			The number of errors detected.
	*/
	int test(TestRun run);
	
	/**
		Abort the currently running testcase. This method
		is called asynchronous while test() is active. The
		test() method should return as quickly as possible.
	*/
	void abort();	
}
