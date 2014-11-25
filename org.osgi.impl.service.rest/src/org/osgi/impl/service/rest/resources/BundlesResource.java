/*
 * Copyright (c) OSGi Alliance (2013, 2014). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

/**
 * The bundles resource, a list of all bundle paths.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
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
