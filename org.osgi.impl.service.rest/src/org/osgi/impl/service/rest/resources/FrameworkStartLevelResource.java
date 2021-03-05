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

import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.impl.service.rest.PojoReflector;
import org.osgi.impl.service.rest.pojos.FrameworkStartLevelPojo;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

/**
 * The framework start level resource.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
public class FrameworkStartLevelResource extends
		AbstractOSGiResource<FrameworkStartLevelPojo> {

	private static final MediaType	MEDIA_TYPE	= MediaType.valueOf("application/org.osgi.frameworkstartlevel");

	public FrameworkStartLevelResource() {
		super(PojoReflector.getReflector(FrameworkStartLevelPojo.class), MEDIA_TYPE);
	}

	@Override
	public Representation get(final Variant variant) {
		try {
			return getRepresentation(new FrameworkStartLevelPojo(
					getFrameworkStartLevel()), variant);
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

	@Override
	public Representation put(final Representation r,
			final Variant variant) {
		try {
			final FrameworkStartLevelPojo sl = fromRepresentation(r, r.getMediaType());
			final FrameworkStartLevel fsl = getFrameworkStartLevel();

			if (sl.getStartLevel() != 0) {
				fsl.setStartLevel(sl.getStartLevel());
			}
			if (sl.getInitialBundleStartLevel() != 0) {
				fsl.setInitialBundleStartLevel(sl.getInitialBundleStartLevel());
			}

			return SUCCESS(Status.SUCCESS_NO_CONTENT);
		} catch (final Exception e) {
			return ERROR(e, variant);
		}
	}

}
