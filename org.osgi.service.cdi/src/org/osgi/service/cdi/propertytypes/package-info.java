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
 * Bean Property Types Package Version 1.0.
 * <p>
 * When used as annotations, bean property types are processed by CCR to
 * generate default component properties, service properties and target filters.
 * <p>
 * Bundles wishing to use this package at runtime must list the package in the
 * Import-Package header of the bundle's manifest.
 * <p>
 * Example import for consumers using the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.cdi.propertytypes; version="[1.0,2.0)"}
 *
 * @author $Id$
 */

@Version(CDI_SPECIFICATION_VERSION + ".0")
package org.osgi.service.cdi.propertytypes;

import static org.osgi.service.cdi.CDIConstants.CDI_SPECIFICATION_VERSION;

import org.osgi.annotation.versioning.Version;
