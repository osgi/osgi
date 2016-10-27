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

/**
 * Device Service Specification for ZigBee Technology Descriptions.
 * 
 * <p>
 * This package contains the interfaces for descriptions. The latter may be used
 * to embed meta information about the ZigBee devices, and in other words a meta
 * description of each device type present in a ZCL profile, or even custom
 * devices.
 * 
 * <p>
 * It is not mandatory to provide this meta model for being able to interact
 * with a specific device, but the presence of this meta model would make much
 * easier to implement, for example user interfaces.
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
 * {@code  Import-Package: org.osgi.service.zigbee.descriptions; version="[1.0,2.0)"}
 * <p>
 * Example import for providers implementing the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.zigbee.descriptions; version="[1.0,1.1)"}
 * 
 * @version 1.0
 */

package org.osgi.service.zigbee.descriptions;
