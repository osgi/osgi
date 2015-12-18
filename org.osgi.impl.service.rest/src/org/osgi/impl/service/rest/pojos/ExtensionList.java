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
import org.osgi.service.rest.RestApiExtension;
import org.osgi.util.tracker.ServiceTracker;
import org.restlet.resource.ServerResource;

/**
 * List of extension pojos.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
@RootNode(name = "extensions")
@SuppressWarnings("serial")
public class ExtensionList extends ArrayList<ExtensionList.ExtensionPojo> {

	public ExtensionList(
			ServiceTracker<RestApiExtension, Class<? extends ServerResource>> tracker) {
		final ServiceReference<RestApiExtension>[] refs = tracker
				.getServiceReferences();
		if (refs != null) {
			for (final ServiceReference<RestApiExtension> ref : refs) {
				add(new ExtensionPojo(
						(String) ref.getProperty(RestApiExtension.NAME),
						(String) ref.getProperty(RestApiExtension.URI_PATH)));
			}
		}
	}

	/**
	 * Pojo for extensions to the REST service.
	 */
	@RootNode(name = "extension")
	public static class ExtensionPojo {

		private String	name;
		private String	path;

		public ExtensionPojo(final String name, final String path) {
			this.name = name;
			this.path = path;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

	}

}
