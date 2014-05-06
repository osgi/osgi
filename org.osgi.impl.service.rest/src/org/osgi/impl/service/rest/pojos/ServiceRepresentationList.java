/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2012
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2013 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.pojos;

import java.util.ArrayList;

import org.osgi.framework.ServiceReference;

@SuppressWarnings("serial")
public final class ServiceRepresentationList extends ArrayList<ServicePojo> {

	public ServiceRepresentationList(ServiceReference<?>[] srefs) {
		if (srefs != null) {
			for (final ServiceReference<?> sref : srefs) {
				add(new ServicePojo(sref));
			}
		}
	}

}
