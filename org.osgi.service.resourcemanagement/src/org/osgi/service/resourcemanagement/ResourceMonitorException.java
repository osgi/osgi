/*
 * Copyright (c) OSGi Alliance (2010, 2013). All Rights Reserved.
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

package org.osgi.service.resourcemanagement;

/**
 * Resource Monitor Exception reports an invalid usage of a monitor.
 * 
 * @author see RFC 200 authors: Andre Bottaro, Gregory Bonnardel, Svetozar
 *         Dimov, Evgeni Grigorov, Arnaud Rinquin, Antonin Chazalet.
 */
public class ResourceMonitorException extends Exception {

	/** generated */
	private static final long	serialVersionUID	= -8980805489250289689L;

	/**
	 * Create a new ResourceMonitorException
	 * 
	 * @param msg message
	 */
	public ResourceMonitorException(String msg) {
		super(msg);
	}

}
