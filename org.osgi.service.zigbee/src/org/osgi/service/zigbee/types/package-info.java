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

/**
 * Device Service Specification for ZigBee Technology Data Types.
 * 
 * <p>
 * Utility classes modeling the ZCL data types. Each class provides the static
 * getInstance() method for retrieving a singleton instance of the class itself.
 * 
 * <p>
 * Every class contains methods for getting information about the data type like
 * its ID and name. It is also possible to know if the data type is analog or
 * digital or get the Java class it is mapped in.
 * 
 * <p>
 * Bundles wishing to use this package must list the package in the
 * Import-Package header of the bundle's manifest. This package has two
 * types of users: the consumers that use the API in this package and the
 * providers that implement the API in this package.
 * 
 * <p>
 * Example import for consumers using the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.zigbee.types; version="[1.0,2.0)"}
 * <p>
 * Example import for providers implementing the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.zigbee.types; version="[1.0,1.1)"}
 * 
 * @see org.osgi.service.zigbee.descriptions.ZCLDataTypeDescription
 * @author $Id$
 */
@Version("1.0.1")
package org.osgi.service.zigbee.types;

import org.osgi.annotation.versioning.Version;
