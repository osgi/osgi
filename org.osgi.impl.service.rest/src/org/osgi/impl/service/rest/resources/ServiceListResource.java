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
 *  Copyright 2012 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.resources;

import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.ServicePojoList;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class ServiceListResource extends AbstractOSGiResource<ServicePojoList> {

	public ServiceListResource() {
		super(PojoReflector.getReflector(ServicePojoList.class));
	}

	@Get("json|txt")
	public Representation doGet(final Variant variant) {
		try {
			final String filter = (String) getRequest().getAttributes().get(
					"filter");
			final ServiceReference<?>[] srefs = getBundleContext()
					.getAllServiceReferences(null, filter);
			return getRepresentation(new ServicePojoList(srefs), variant);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

}
