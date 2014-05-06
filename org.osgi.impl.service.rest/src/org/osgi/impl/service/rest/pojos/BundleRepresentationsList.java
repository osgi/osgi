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

@SuppressWarnings("serial")
public final class BundleRepresentationsList extends ArrayList<BundlePojo> {

	public BundleRepresentationsList(final org.osgi.framework.Bundle[] bundles) {
		for (final org.osgi.framework.Bundle bundle : bundles) {
			add(new BundlePojo(bundle));
		}
	}

}
