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
package org.osgi.service.component;

/**
 * Defines standard names for Service Component constants.
 * 
 * @version $Revision$
 */
public interface ComponentConstants {
	/**
	 * Manifest header (named &quot;Service-Component&quot;) identifying the XML
	 * documents within the bundle containing the bundle's Service Component
	 * descriptions.
	 * <p>
	 * The attribute value may be retrieved from the <tt>Dictionary</tt>
	 * object returned by the <tt>Bundle.getHeaders</tt> method.
	 */
	public static final String	SERVICE_COMPONENT		= "Service-Component";
	/**
	 * A service registration property for a Service Component. It contains the
	 * name of the Service Component. The type of this property must be
	 * <tt>String</tt>.
	 */
	public final static String	COMPONENT_NAME			= "component.name";
	/**
	 * A service registration property for a Service Component Factory. It
	 * contains the value of the <tt>factory</tt> attribute. The type of this
	 * property must be <tt>String</tt>.
	 */
	public final static String	COMPONENT_FACTORY		= "component.factory";
	/**
	 * A suffix for a service registration property for a reference target. It
	 * contains the filter to select the target services for a reference. The
	 * type of this property must be <tt>String</tt>.
	 */
	public final static String	REFERENCE_TARGET_SUFFIX	= ".target";
}