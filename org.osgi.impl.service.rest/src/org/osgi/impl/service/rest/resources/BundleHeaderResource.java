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

import java.util.List;
import java.util.Map;
import org.osgi.impl.service.rest.RestService;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

/**
 * The bundle header resource.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class BundleHeaderResource extends
		AbstractOSGiResource<Map<String, String>> {

	private static final MediaType	MEDIA_TYPE	= MediaType.valueOf("application/org.osgi.bundleheader");

	public BundleHeaderResource() {
		super(null, MEDIA_TYPE);
	}

	@Override
	public Representation get(final Variant variant) {
		try {
			final List<Preference<Language>> acceptedLanguages = getClientInfo()
					.getAcceptedLanguages();

			final String locale = acceptedLanguages == null
					|| acceptedLanguages.isEmpty() ? null : acceptedLanguages
					.get(0).getMetadata().toString();

			final org.osgi.framework.Bundle bundle = getBundleFromKeys(RestService.BUNDLE_ID_KEY);
			if (bundle == null) {
				return ERROR(Status.CLIENT_ERROR_NOT_FOUND);
			}
			return getRepresentation(
					mapFromDict(locale == null ? bundle.getHeaders()
							: bundle.getHeaders(locale)), variant);
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

}
