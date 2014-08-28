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

import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.ExtensionList;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

public class ExtensionsResource extends AbstractOSGiResource<ExtensionList> {

	public ExtensionsResource() {
		super(PojoReflector.getReflector(ExtensionList.class));
	}

	@Get("json|text")
	public Representation doGet(final Representation value,
			final Variant variant) {
		try {
			return getRepresentation(new ExtensionList(getTracker()), variant);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}
}
