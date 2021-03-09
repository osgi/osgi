/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.rest.resources;

import org.osgi.framework.Bundle;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.RestService;
import org.osgi.impl.service.rest.pojos.BundleStartLevelPojo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

/**
 * The bundle start level resource.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class BundleStartLevelResource extends
		AbstractOSGiResource<BundleStartLevelPojo> {

	private static final MediaType	MEDIA_TYPE	= MediaType.valueOf("application/org.osgi.bundlestartlevel");

	public BundleStartLevelResource() {
		super(PojoReflector.getReflector(BundleStartLevelPojo.class), MEDIA_TYPE);
	}

	@Override
	public Representation get(final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys(RestService.BUNDLE_ID_KEY);
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			final BundleStartLevel bsl = bundle.adapt(BundleStartLevel.class);
			final BundleStartLevelPojo sl = new BundleStartLevelPojo(bsl);
			return getRepresentation(sl, variant);
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

	@Override
	public Representation put(final Representation value,
			final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys(RestService.BUNDLE_ID_KEY);
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			final BundleStartLevelPojo sl = fromRepresentation(value, value.getMediaType());
			final BundleStartLevel bsl = bundle.adapt(BundleStartLevel.class);
			bsl.setStartLevel(sl.getStartLevel());

			return getRepresentation(new BundleStartLevelPojo(bsl), variant);
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

}
