/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2011
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2011 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.pojos;

import java.util.ArrayList;

import org.osgi.framework.Bundle;

@SuppressWarnings("serial")
public final class BundlePojoList extends ArrayList<String> {

	public BundlePojoList(final Bundle[] bundles) {
		for (int i = 0; i < bundles.length; i++) {
			add("framework/bundle/" + bundles[i].getBundleId());
		}
	}

}
