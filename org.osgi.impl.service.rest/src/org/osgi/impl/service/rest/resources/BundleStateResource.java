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
import org.osgi.impl.service.rest.pojos.BundleStatePojo;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class BundleStateResource extends AbstractOSGiResource<BundleStatePojo> {

	public BundleStateResource() {
		super(PojoReflector.getReflector(BundleStatePojo.class));
	}

	@Get("json|text")
	public Representation doGet(final Representation value,
			final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys("bundleId",
					"bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
			}
			return getRepresentation(new BundleStatePojo(bundle.getState()),
					variant);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

	@Put("json|text")
	public Representation doPut(final Representation value,
			final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys("bundleId",
					"bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
			}
			final BundleStatePojo targetState = fromRepresentation(value,
					variant);

			if (targetState.getState() == Bundle.ACTIVE) {
				bundle.start();
				return getRepresentation(
						new BundleStatePojo(bundle.getState()), variant);
			} else if (targetState.getState() == Bundle.RESOLVED) {
				bundle.stop();
				return getRepresentation(
						new BundleStatePojo(bundle.getState()), variant);
			} else {
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "target state "
						+ targetState.getState() + " not supported");
				return ERROR;
			}
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

}
