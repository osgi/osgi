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

import org.osgi.framework.Bundle;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.BundlePojoList;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class BundlesResource extends AbstractOSGiResource<BundlePojoList> {

	public BundlesResource() {
		super(PojoReflector.getReflector(BundlePojoList.class));
	}

	@Get("json|text")
	public Representation getBundles(final Variant variant) {
		try {
			final Representation rep = getRepresentation(new BundlePojoList(
					getBundlesFromFilter("filter")), variant);
			return rep;
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

	@Post
	public Representation installBundle(final Representation content,
			final Variant variant) {
		try {
			final Reference ref = variant.getLocationRef();
			final String location = ref == null ? null : ref.toString();

			if (location != null) {
				if (getBundleContext().getBundle(location) != null) {
					setStatus(Status.CLIENT_ERROR_CONFLICT);
					return null;
				}
			}

			final Bundle bundle = getBundleContext().installBundle(location,
					content.getStream());
			return new StringRepresentation("framework/bundle/"
					+ bundle.getBundleId());
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

	
	@Post("text")
	public Representation installBundle(final String uri, final Variant variant) {
		try {
			if (getBundleContext().getBundle(uri) != null) {
				setStatus(Status.CLIENT_ERROR_CONFLICT);
				return null;
			}

			final Bundle bundle = getBundleContext().installBundle(uri);

			return new StringRepresentation("framework/bundle/"
					+ bundle.getBundleId());
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}


}
