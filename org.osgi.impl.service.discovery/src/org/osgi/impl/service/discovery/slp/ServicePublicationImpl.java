/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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

package org.osgi.impl.service.discovery.slp;

import java.util.Map;

import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;

/**
 * @author kt32483
 * 
 */
public class ServicePublicationImpl implements ServicePublication {

	private SLPHandlerImpl				handler	= null;
	private ServiceEndpointDescription	sed		= null;

	/**
	 * 
	 * @param disco
	 */
	public ServicePublicationImpl(SLPHandlerImpl disco,
			ServiceEndpointDescription descr) {
		handler = disco;
		sed = descr;
	}

	/**
	 * @see org.osgi.service.discovery.ServicePublication#getServiceEndpointDescription()
	 */
	public ServiceEndpointDescription getServiceEndpointDescription() {
		return sed;
	}

	/**
	 * @see org.osgi.service.discovery.ServicePublication#unpublishService()
	 */
	public void unpublishService() {
		handler.unpublishService(sed);
	}

	/**
	 * @see org.osgi.service.discovery.ServicePublication#updateService(java.util.Map)
	 */
	public void updateService(Map arg0) {

	}
	
	/**
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals (Object o) {
		if (!(o instanceof ServicePublication)) {
			return false;
		}
		ServicePublication servicePublication = (ServicePublication) o;
		if (! ((sed == servicePublication.getServiceEndpointDescription())
				|| (sed != null && sed
						.equals(servicePublication.getServiceEndpointDescription())))) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return 0;
	}

}
