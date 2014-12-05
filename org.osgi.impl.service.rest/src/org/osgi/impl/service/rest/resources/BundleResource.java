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

import java.net.URL;
import org.osgi.framework.Bundle;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.BundlePojo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * The bundle resource.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class BundleResource extends AbstractOSGiResource<BundlePojo> {

	private static final MediaType	MEDIA_TYPE	= MediaType.valueOf("application/org.osgi.bundle");

	public BundleResource() {
		super(PojoReflector.getReflector(BundlePojo.class), MEDIA_TYPE);
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

	@Delete()
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
