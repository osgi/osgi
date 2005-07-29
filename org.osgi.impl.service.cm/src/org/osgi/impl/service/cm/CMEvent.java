/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.cm;

import java.util.Dictionary;
import org.osgi.framework.ServiceReference;

/**
 * Event objects are passed from ConfigurationImpl to CMEventManager, when a
 * configuration is updated or deleted, in order to inform target services
 * (MaanagedService(Factories)).
 * 
 * CMEvent class encapsulates info for such an event: ConfigurationImpl, which
 * has been updated or deleted; properties with which the Configuration has been
 * updated (as there may be a sequential update with different props); and type
 * of event - deletion or update.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class CMEvent {
	/** constant for an update event */
	protected final static int	UPDATED	= 0;
	/** constant for a delete event */
	protected final static int	DELETED	= 1;
	/** Object been updated/deleted */
	protected ConfigurationImpl	config;
	/** Props with which the update has been done */
	protected Dictionary		props;
	/** Type of event: 0 for update and 1 for deletion */
	protected int				event;
	protected ServiceReference	sRef;

	/**
	 * Constructs a new CMEvent.
	 * 
	 * @param config Configuration that has been updated or deleted
	 * @param props Props with which the update has been done
	 * @param event Type of event: 0 for update and 1 for deletion
	 */
	public CMEvent(ConfigurationImpl config, Dictionary props, int event,
			ServiceReference sRef) {
		this.config = config;
		this.props = props;
		this.event = event;
		this.sRef = sRef;
	}
}