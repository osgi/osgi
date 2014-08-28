/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2013
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2014 ibm.com. All rights reserved.
 */

package org.osgi.impl.service.rest.client;

import java.net.URI;

import org.osgi.service.rest.client.RestClient;
import org.osgi.service.rest.client.RestClientFactory;

public class RestClientFactoryImpl implements RestClientFactory {

	/**
	 * @see org.osgi.service.rest.client.RestClientFactory#createRestClient(java.net.URI)
	 */
	public RestClient createRestClient(final URI uri) {
		return new RestClientImpl(uri);
	}

}
