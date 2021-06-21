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

package org.osgi.service.resourcemonitoring.monitor;

import java.net.DatagramSocket;
import java.net.Socket;

import org.osgi.service.resourcemonitoring.ResourceContext;
import org.osgi.service.resourcemonitoring.ResourceMonitor;
import org.osgi.service.resourcemonitoring.ResourceMonitoringService;

/**
 * A {@link ResourceMonitor} for the
 * {@link ResourceMonitoringService#RES_TYPE_SOCKET} resource type.
 * {@link SocketMonitor} instance are used to monitor and limit the number of
 * in-use sockets per {@link ResourceContext} instance. {@link SocketMonitor}
 * instance handle all types of sockets (TCP, UDP, ...).
 * <p>
 * A TCP socket is considered to be in-use when it is bound (
 * {@link Socket#bind(java.net.SocketAddress)}) or when it is connected (
 * {@link Socket#connect(java.net.SocketAddress)}). It leaves the in-use state
 * when the socket is closed ({@link Socket#close()}). *
 * <p>
 * A UDP socket is in-use when it is bound (
 * {@link DatagramSocket#bind(java.net.SocketAddress)}) or connected (
 * {@link DatagramSocket#connect(java.net.SocketAddress)}). A UDP Socket leaves
 * the in-use state when it is closed ({@link DatagramSocket#close()}).
 * 
 * @version 1.0
 * @author $Id$
 */
public interface SocketMonitor extends ResourceMonitor<Long> {

	/**
	 * Returns the number of existing socket created by a
	 * {@link ResourceContext}.
	 * <p>
	 * The {@link #getUsage()} method returns the same value, wrapped in a long.
	 * 
	 * @return the number of existing socket.
	 */
	public long getSocketUsage();

}
