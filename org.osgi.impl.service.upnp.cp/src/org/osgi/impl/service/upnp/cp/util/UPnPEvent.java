/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.impl.service.upnp.cp.util;

import java.util.Hashtable;

// This event class provides a way to to identify the notify messages. 
// When ever a notification comes, this event class will be fired to all the suscribers.
public class UPnPEvent {
	public static final int	UPNP_SUBSCRIBE		= 1;
	public static final int	UPNP_RENEW			= 2;
	public static final int	UPNP_NOTIFY			= 4;
	public static final int	UPNP_UNSUBSCRIBE	= 3;
	public static final int	UPNP_TIME_EXPIRED	= 5;
	private int				type;
	private String			timeout;
	private Hashtable<String,Object>	statevariables;
	private String			serviceId;

	public UPnPEvent(int state, String serviceId, String timeout,
			Hashtable<String,Object> states) {
		this.type = state;
		this.timeout = timeout;
		this.statevariables = states;
		this.serviceId = serviceId;
	}

	// Type of the message(UPNP_SUBSCRIBE,UPNP_RENEW, UPNP_UNSUBSCRIBE,
	// UPNP_NOTIFY)
	public int getType() {
		return type;
	}

	// Returns the timeout in seconds.
	public String getTimeout() {
		return timeout;
	}

	// Returns a table containing all the changed variables and values.
	public Hashtable<String,Object> getList() {
		return statevariables;
	}

	public String getSubscriptionId() {
		return serviceId;
	}
}
