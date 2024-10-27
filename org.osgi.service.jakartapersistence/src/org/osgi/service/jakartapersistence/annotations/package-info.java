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
 * Jakarta Persistence Service Annotations Package Version 2.0.
 * <p>
 * This package contains annotations that can be used to require the Jakarta
 * Persistence Service implementation.
 * <p>
 * Bundles should not normally need to import this package as the annotations
 * are only used at build-time.
 * <p>
 * 
 * @author $Id$
 */

@Version(PERSISTENCE_SPECIFICATION_VERSION)
package org.osgi.service.jakartapersistence.annotations;

import static org.osgi.service.jakartapersistence.EntityManagerFactoryBuilder.PERSISTENCE_SPECIFICATION_VERSION;

import org.osgi.annotation.versioning.Version;

