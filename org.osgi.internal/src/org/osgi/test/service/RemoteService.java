/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.service;

/**
	A RemoteService describes a service available via
	a host:port combination. A service is define by this
	tuple and an application identifier.
	<p>
	When a RemoteService is registered in the framework, the
	property "application" should be set to the type so
	that filters can search for specific applications.
	<p>
	The application name is suggested to be the main package 
	name.
*/

public interface RemoteService
{	
	/**
		Return the application id.
	*/
	String getApplication();
	
	/**
		Return the host name associated with this service.
	*/
	String getHost();
	
	/**
		Return the port number associated with this service.
	*/
	int getPort();
	
	/**
		Return a unique identifier for this specific instance.
	*/
	public String getID();
	
	/**
		Hashing is implemented on the id.
	*/
	int hashCode();
	
	/**
		Equals must be implemented on id.
	*/
	boolean equals(Object service);
	
	/**
		Answer the comment part.
	*/
	
	public String getComment();

}
