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

import java.net.MalformedURLException;
import java.net.URL;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.RestService;
import org.osgi.impl.service.rest.pojos.BundlePojo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

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

	@Override
	public Representation get(final Variant variant) {
		try {
			final Bundle bundle = getBundleFromKeys(RestService.BUNDLE_ID_KEY);
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			return getRepresentation(new BundlePojo(bundle), variant);
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

	@Override
	public Representation delete(final Variant variant) {
		try {
			final org.osgi.framework.Bundle bundle = getBundleFromKeys(RestService.BUNDLE_ID_KEY);
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			bundle.uninstall();
		} catch (final Exception e) {
			return ERROR(e, variant);
		}

		return SUCCESS(Status.SUCCESS_NO_CONTENT);
	}

	@Override
	public Representation put(final Representation content,
			final Variant variant) {
		try {
			if (MediaType.TEXT_PLAIN.equals(content.getMediaType())) {
				final org.osgi.framework.Bundle bundle = getBundleFromKeys(RestService.BUNDLE_ID_KEY);
				if (bundle == null) {
					return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
				}
				final String location = content.getText();
				if (location == null) {
					bundle.update();
				} else {
					bundle.update(new URL(location).openStream());
				}

				return SUCCESS(Status.SUCCESS_NO_CONTENT);
			}

			final org.osgi.framework.Bundle bundle = getBundleFromKeys(RestService.BUNDLE_ID_KEY);
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}

			bundle.update(content.getStream());

			return SUCCESS(Status.SUCCESS_NO_CONTENT);
		} catch (final MalformedURLException e) {
			return ERROR(new BundleException("Malformed update URL", BundleException.READ_ERROR, e), variant);
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

}
