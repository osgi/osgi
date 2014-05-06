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

import java.util.List;
import java.util.Map;

import org.restlet.data.Language;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class BundleHeaderResource extends
		AbstractOSGiResource<Map<String, String>> {

	public BundleHeaderResource() {
		super(null);
	}

	@Get("json|txt")
	public Representation doGet(final Representation param,
			final Variant variant) {
		try {
			final List<Preference<Language>> acceptedLanguages = getClientInfo()
					.getAcceptedLanguages();

			final String locale = acceptedLanguages == null
					|| acceptedLanguages.isEmpty() ? null : acceptedLanguages
					.get(0).getMetadata().toString();

			final org.osgi.framework.Bundle bundle = getBundleFromKeys(
					"bundleId", "bundleSymbolicName", "bundleVersion");
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			return getRepresentation(
					mapFromDict(locale == null ? bundle.getHeaders()
							: bundle.getHeaders(locale)), variant);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

}
