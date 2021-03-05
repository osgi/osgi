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
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.RestService;
import org.osgi.impl.service.rest.pojos.BundleStatePojo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

/**
 * The bundle state resource.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class BundleStateResource extends AbstractOSGiResource<BundleStatePojo> {

	private static final MediaType	MEDIA_TYPE	= MediaType.valueOf("application/org.osgi.bundlestate");

	public BundleStateResource() {
		super(PojoReflector.getReflector(BundleStatePojo.class), MEDIA_TYPE);
	}

	@Override
	public Representation get(final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys(RestService.BUNDLE_ID_KEY);
			if (bundle == null) {
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
			}
			return getRepresentation(new BundleStatePojo(bundle.getState()),
					variant);
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
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				return null;
			}

			final BundleStatePojo targetState = fromRepresentation(value,
					value.getMediaType());

			if (bundle.getState() == Bundle.UNINSTALLED) {
				return ERROR(Status.CLIENT_ERROR_PRECONDITION_FAILED, "target state "
						+ targetState.getState() + " not reachable from the current state");
			} else if (targetState.getState() == Bundle.ACTIVE) {
				bundle.start(targetState.getOptions());
				return getRepresentation(
						new BundleStatePojo(bundle.getState()), variant);
			} else if (targetState.getState() == Bundle.RESOLVED) {
				bundle.stop(targetState.getOptions());
				return getRepresentation(
						new BundleStatePojo(bundle.getState()), variant);
			} else {
				return ERROR(Status.CLIENT_ERROR_BAD_REQUEST, "target state "
						+ targetState.getState() + " not supported");
			}
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

}
