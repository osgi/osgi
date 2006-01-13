/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.deploymentadmin;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.deploymentadmin.DeploymentException;

/**
 * Is used to figure out whether it is a jar file
 */
public class DeploymentInputStream extends InputStream {

	// Every jar begins with this 
	private static final int[] patter = new int[] { 'P', 'K', '\3', '\4' };

	private InputStream is;

	private int[] buffer = new int[4];

	private int index;

	public DeploymentInputStream(InputStream is) throws IOException,
			DeploymentException 
	{
		this.is = is;
		while (index < 4) {
			int data = is.read();
			if (data == -1 || patter[index] != data)
				throw new DeploymentException(
						DeploymentException.CODE_NOT_A_JAR, "Bad jar file");
			buffer[index] = data;
			++index;
		}
		index = 0;
	}

	public int read() throws IOException {
		if (index < 4)
			return buffer[index++];
		return is.read();
	}

}
