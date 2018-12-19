/*
 * Copyright (c) OSGi Alliance (2016, 2018). All Rights Reserved.
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

/**
 * Device Service Specification for ZigBee Technology Descriptors.
 * 
 * <p>
 * This package contains the interfaces representing the ZigBee descriptors and
 * the fields defined inside some of them.
 * 
 * <p>
 * An interface for modeling the ZigBee User Descriptor is missing because this
 * descriptor has only one field (the UserDescription). Therefore this field can
 * be read and written using respectively the
 * {@link org.osgi.service.zigbee.ZigBeeNode#getUserDescription()} and the
 * {@link org.osgi.service.zigbee.ZigBeeNode#setUserDescription(String)}
 * methods.
 * 
 * <p>
 * The {@link org.osgi.service.zigbee.descriptors.ZigBeeNodeDescriptor}, 
 * {@link org.osgi.service.zigbee.descriptors.ZigBeePowerDescriptor} and the
 * {@link org.osgi.service.zigbee.descriptors.ZigBeeComplexDescriptor} are read 
 * using the appropriate methods in the
 * {@link org.osgi.service.zigbee.ZigBeeNode} interface, whereas the
 * ZigBeeSimpleDescriptor can be read using the appropriate method of the
 * {@link org.osgi.service.zigbee.ZigBeeEndpoint} services registered in the
 * framework.
 * 
 * <p>
 * Bundles wishing to use this package must list the package in the
 * Import-Package header of the bundle's manifest. This package has two types of
 * users: the consumers that use the API in this package and the providers that
 * implement the API in this package.
 * 
 * <p>
 * Example import for consumers using the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.zigbee.descriptors; version="[1.0,2.0)"}
 * <p>
 * Example import for providers implementing the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.zigbee.descriptors; version="[1.0,1.1)"}
 * 
 * @author $Id$
 */
@Version("1.0")
package org.osgi.service.zigbee.descriptors;

import org.osgi.annotation.versioning.Version;
