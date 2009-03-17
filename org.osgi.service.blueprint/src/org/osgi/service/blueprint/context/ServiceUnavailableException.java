/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.context;

/**
 * Thrown when an invocation is made on an OSGi service reference component, and 
 * a backing service is not available.
 */
public class ServiceUnavailableException extends RuntimeException {

	private final Class serviceType;
	private final String filter;
	
	public ServiceUnavailableException(
           String message,
           Class serviceType,
           String filterExpression) {
		super(message);
		this.serviceType = serviceType;
		this.filter = filterExpression;
	}
  
	/**
	 * The type of the service that would have needed to be available in 
	 * order for the invocation to proceed.
	 */
	public Class getServiceType() {
		return this.serviceType;
	}
 
	/**
	 * The filter expression that a service would have needed to satisfy in order
	 * for the invocation to proceed.
	 */
	public String getFilter() {
		return this.filter;
	}
}

