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

import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.BundleRepresentationsList;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

/**
 * The bundle representations resource, listing the full representations of all
 * bundles.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class BundleRepresentationsResource extends
		AbstractOSGiResource<BundleRepresentationsList> {

	private static final MediaType	MEDIA_TYPE	= MediaType.valueOf("application/org.osgi.bundles.representations");

	public BundleRepresentationsResource() {
		super(PojoReflector.getReflector(BundleRepresentationsList.class), MEDIA_TYPE);
	}

	@Override
	public Representation get(final Variant variant) {
		try {
			final Representation rep = getRepresentation(
					new BundleRepresentationsList(getBundles()), variant);
			return rep;
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

}
