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

import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.BundlePojo;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class BundleResource extends AbstractOSGiResource<BundlePojo> {

	public BundleResource() {
		super(PojoReflector.getReflector(BundlePojo.class));
	}

	@Get("json|txt")
	public Representation doGet(final Representation none, final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys("bundleId",
					"bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			return getRepresentation(new BundlePojo(bundle), variant);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

	@Delete("txt")
	public Representation delete(final String none, final Variant variant) {
		try {
			final org.osgi.framework.Bundle bundle = getBundleFromKeys(
					"bundleId", "bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			bundle.uninstall();
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
		return SUCCESS;
	}

	@Put
	public Representation doPutStream(final Representation content,
			final Variant variant) {
		try {
			final org.osgi.framework.Bundle bundle = getBundleFromKeys(
					"bundleId", "bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}

			bundle.update(content.getStream());

			return SUCCESS(Status.SUCCESS_NO_CONTENT);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

	@Put("txt")
	public Representation doPut(final String param, final Variant variant) {
		try {
			final org.osgi.framework.Bundle bundle = getBundleFromKeys(
					"bundleId", "bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			if (param == null) {
				bundle.update();
			} else {
				bundle.update(new URL(param).openStream());
			}

			return SUCCESS(Status.SUCCESS_NO_CONTENT);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

}
