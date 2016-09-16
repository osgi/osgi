/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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

package org.osgi.service.zigbee.descriptors;

/**
 * Represents the ZigBee Server Mask field of the ZigBee Node Descriptor.
 * 
 * @noimplement
 * 
 * @author $Id$
 */
public interface ZigBeeServerMask {

	/**
	 * @return {@code true} if and only if the server is a Primary Trust Center
	 */
	public boolean isPrimaryTrustCenter();

	/**
	 * @return {@code true} if and only if the server is a Backup Trust Center
	 */
	public boolean isBackupTrustCenter();

	/**
	 * @return {@code true} if and only if the server is a Primary Binding Table
	 *         Cache
	 */
	public boolean isPrimaryBindingTableCache();

	/**
	 * @return {@code true} if and only if the server is a Backup Binding Table
	 *         Cache
	 */
	public boolean isBackupBindingTableCache();

	/**
	 * @return {@code true} if and only if the server is a Primary Discovery
	 *         Cache
	 */
	public boolean isPrimaryDiscoveryCache();

	/**
	 * @return {@code true} if and only if the server is a Backup Discovery
	 *         Cache
	 */
	public boolean isBackupDiscoveryCache();

	/**
	 * @return {@code true} if and only if the server is a Network Manager
	 */
	public boolean isNetworkManager();

}
