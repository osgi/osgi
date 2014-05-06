/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2011
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2011 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.resources;

import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.BundleRepresentationsList;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class BundleRepresentationsResource extends
		AbstractOSGiResource<BundleRepresentationsList> {

	public BundleRepresentationsResource() {
		super(PojoReflector.getReflector(BundleRepresentationsList.class));
	}

	@Get("json|text")
	public Representation getBundles(final Variant variant) {
		try {
			final Representation rep = getRepresentation(
					new BundleRepresentationsList(getBundleContext()
							.getBundles()), variant);
			return rep;
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

}
