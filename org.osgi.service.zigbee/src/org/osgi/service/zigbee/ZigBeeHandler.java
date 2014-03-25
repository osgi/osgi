/*
 * Copyright (c) OSGi Alliance (2013). All Rights Reserved.
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

package org.osgi.service.zigbee;

/**
 * ZigBeeHandler manages response of a request to the Base Driver
 * 
 * @version 1.0
 * 
 * @author see RFC 192 authors: Andre Bottaro, Arnaud Rinquin, Jean-Pierre
 *         Poutcheu, Fabrice Blache, Christophe Demottie, Antonin Chazalet,
 *         Evgeni Grigorov, Nicola Portinaro, Stefano Lenzi.
 */

public interface ZigBeeHandler {

	/**
	 * Notifies the success result of the call. This method is used when the
	 * handler command result is a success.
	 * 
	 * @param response contains the results of the call.
	 */
	public void onSuccess(Object response);

	/**
	 * Notifies the failure result of the call. This method is used when the
	 * handler command result is a failure.
	 * 
	 * @param e the exception.
	 */
	public void onFailure(Exception e);

}
