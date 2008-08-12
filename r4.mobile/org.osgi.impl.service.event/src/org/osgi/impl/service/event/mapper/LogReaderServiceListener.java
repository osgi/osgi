/*
 * $Header$
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */
package org.osgi.impl.service.event.mapper;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;

/**
 * @version $Revision$
 */
public interface LogReaderServiceListener {
	public void logReaderServiceAdding(ServiceReference reference,
			LogReaderService service);

	public void logReaderServiceRemoved(ServiceReference reference,
			LogReaderService service);
}