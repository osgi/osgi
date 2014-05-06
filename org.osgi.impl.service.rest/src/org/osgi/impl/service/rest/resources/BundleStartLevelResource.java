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
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.BundleStartLevelPojo;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class BundleStartLevelResource extends
		AbstractOSGiResource<BundleStartLevelPojo> {

	public BundleStartLevelResource() {
		super(PojoReflector.getReflector(BundleStartLevelPojo.class));
	}

	@Get("json|txt")
	public Representation doGet(final Representation value,
			final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys("bundleId",
					"bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			final BundleStartLevel bsl = bundle.adapt(BundleStartLevel.class);
			final BundleStartLevelPojo sl = new BundleStartLevelPojo(bsl);
			return getRepresentation(sl, variant);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

	@Put("json|txt")
	public Representation doPut(final Representation value,
			final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys("bundleId",
					"bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			final BundleStartLevelPojo sl = fromRepresentation(value, variant);
			final BundleStartLevel bsl = bundle.adapt(BundleStartLevel.class);
			bsl.setStartLevel(sl.getStartLevel());

			return SUCCESS(Status.SUCCESS_NO_CONTENT);
		} catch (final Exception e) {
			return ERROR(Status.CLIENT_ERROR_BAD_REQUEST, e, variant);
		}
	}

}
