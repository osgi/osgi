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
 * Servlet Whiteboard Property Types Package Version 3.0.
 * <p>
 * When used as annotations, component property types are processed by tools to
 * generate Component Descriptions which are used at runtime.
 * <p>
 * Bundles wishing to use this package at runtime must list the package in the
 * Import-Package header of the bundle's manifest.
 * <p>
 * Example import for consumers using the API in this package:
 * <p>
 * {@code  Import-Package: org.osgi.service.servlet.whiteboard.propertytypes; version="[3.0,4.0)"}
 *
 * @author $Id: 7e9e0f0504729eb0029a8e3a23720822df82bc4b $
 */

@Version(HTTP_WHITEBOARD_SPECIFICATION_VERSION + ".0")
package org.osgi.service.servlet.whiteboard.propertytypes;

import static org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SPECIFICATION_VERSION;

import org.osgi.annotation.versioning.Version;

