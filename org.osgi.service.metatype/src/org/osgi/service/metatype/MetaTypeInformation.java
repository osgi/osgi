/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGi Alliance DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.metatype;

import org.osgi.framework.Bundle;

/**
 * A MetaType Information object is created by the MetaTypeService to return
 * meta type information for a specific bundle.
 * 
 * @version $Revision$
 */
public interface MetaTypeInformation extends MetaTypeProvider {
	/**
	 * Return the PIDs (for ManagedServices) for which ObjectClassDefinition
	 * information is available.
	 * ### how dynamic is this? I.e. what happens when a bundle register/unregisters
	 * ### a Managed Service?
	 * 
	 * @return Array of PIDs.
	 */
	String[] getPids();

	/**
	 * Return the Factory PIDs (for ManagedServices) for which
	 * ObjectClassDefinition information is available.
	 * 
	 * @return Array of Factory PIDs.
	 */
	String[] getFactoryPids();

	/**
	 * Return the bundle for which this object provides metatype information.
	 * 
	 * @return Bundle for which this object provides metatype information.
	 */
	Bundle getBundle();
}