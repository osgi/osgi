/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */

package org.osgi.test.service;

/**
   The TestCaseLink runs on the target and controls the target
   bundles' life cycles. It also provides communication channels to the director
   and the TestCase bundle running on the director framework. A target bundle should
   use this service to send and receive objects to its TestCase and to send logs to the
   director
*/
import java.io.*;

public interface TestCaseLink
{
  /**
	 Sends an object to the TestCase controlling the target bundle.

	 @param o		The object being sent.
  */
  public void send(Object o) throws IOException;


  /**
	 Receives an object from the TestCase controlling the target bundle.

	 @param timeout	Timeout in milliseconds.

	 @return		The object received, or null if it timed out.
  */
  public Object receive(long timeout) throws IOException;


  /**
	 Sends a log to the TestDirector. A log id string will be prefixed to this string
	 before sending. It looks like [ThreadName yyyy-mm-dd HH:MM:SS.mmm].

	 @param log		The log string.
  */
  public void log(String log) throws IOException;	
}
