/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2012
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2013 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.pojos;

import java.util.ArrayList;

import org.osgi.framework.ServiceReference;
import org.osgi.service.rest.RestApiExtension;
import org.osgi.util.tracker.ServiceTracker;
import org.restlet.resource.ServerResource;

@SuppressWarnings("serial")
public class ExtensionList extends ArrayList<ExtensionList.ExtensionPojo> {

	public ExtensionList(
			ServiceTracker<RestApiExtension, Class<? extends ServerResource>> tracker) {
		final ServiceReference<RestApiExtension>[] refs = tracker
				.getServiceReferences();
		for (final ServiceReference<RestApiExtension> ref : refs) {
			add(new ExtensionPojo(
					(String) ref.getProperty(RestApiExtension.NAME),
					(String) ref.getProperty(RestApiExtension.URI_PATH)));
		}
	}

	public static class ExtensionPojo {

		private String name;
		private String path;

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
