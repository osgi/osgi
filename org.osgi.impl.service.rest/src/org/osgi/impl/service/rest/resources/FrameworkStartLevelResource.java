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
import org.osgi.impl.service.rest.pojos.FrameworkStartLevelPojo;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

public class FrameworkStartLevelResource extends
		AbstractOSGiResource<FrameworkStartLevelPojo> {

	public FrameworkStartLevelResource() {
		super(PojoReflector.getReflector(FrameworkStartLevelPojo.class));
	}

	@Get("json|txt")
	public Representation getStartLevel(final Variant variant) {
		return getRepresentation(new FrameworkStartLevelPojo(
				getFrameworkStartLevel()), variant);
	}

	@Put("json|txt")
	public Representation setStartLevel(final Representation r,
			final Variant variant) {
		try {
			final FrameworkStartLevelPojo sl = fromRepresentation(r, variant);
			getFrameworkStartLevel().setStartLevel(sl.getStartLevel());

			return SUCCESS(Status.SUCCESS_NO_CONTENT);
		} catch (final Exception e) {
			return ERROR(Status.SERVER_ERROR_INTERNAL, e, variant);
		}
	}

}
