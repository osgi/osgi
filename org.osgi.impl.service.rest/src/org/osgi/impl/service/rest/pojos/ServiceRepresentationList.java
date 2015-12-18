/*
 * Copyright (c) OSGi Alliance (2013, 2015). All Rights Reserved.
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

package org.osgi.impl.service.rest.pojos;

import java.util.ArrayList;
import org.osgi.framework.ServiceReference;
import org.osgi.impl.service.rest.PojoReflector.RootNode;

/**
 * List of service representation pojos.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 *
 */
@RootNode(name = "services")
@SuppressWarnings("serial")
public final class ServiceRepresentationList extends ArrayList<ServicePojo> {

	public ServiceRepresentationList(ServiceReference<?>[] srefs) {
		if (srefs != null) {
			for (final ServiceReference<?> sref : srefs) {
				add(new ServicePojo(sref));
			}
		}
	}

}
