/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.BundleStartLevelPojo;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * The bundle start level resource.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
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
