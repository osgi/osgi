/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.application;

import java.util.Map;

/**
 * Descriptor of an application. This is the Application representer in the
 * service registry. The application descriptor will be passed to an application
 * container for instance creation.
 * 
 */
public interface ApplicationDescriptor {
	/**
	 * Returns the display name of application
	 * 
	 */
	public String getName();

	/**
	 * Get the unique identifier of the descriptor.
	 * 
	 */
	public String getUniqueID();

	/**
	 * Returns the version descriptor of the service.
	 * 
	 */
	public String getVersion();

	/**
	 * Get the application container type. The following container types are
	 * supported by default
	 * <ul>
	 * <li>MEG (?)
	 * <li>Midlet
	 * <li>Doja
	 * </ul>
	 * 
	 */
	public String getContainerID();

	/**
	 * Return the category of the application.
	 * <p>
	 * The following list of application types are predefined:
	 * <ul>
	 * <li>APPTYPE_GAMES
	 * <li>APPTYPE_MESSAGING
	 * <li>APPTYPE_OFFICETOOLS
	 * </ul>
	 * 
	 */
	public String getCategory();

	public Map getProperties(String locale);

	public Map getContainerProperties(String locale);
}
