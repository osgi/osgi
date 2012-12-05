/*
 * Copyright (c) OSGi Alliance (2012). All Rights Reserved.
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

package org.osgi.dto.framework;

import org.osgi.dto.DTO;

/**
 * Data Transfer Object for a Bundle.
 * 
 * <p>
 * A Bundle can be adapted to provide a {@code BundleDTO} for the Bundle.
 * 
 * @author $Id$
 * @NotThreadSafe
 */
public class BundleDTO extends DTO {
    /**
     * The bundle's unique identifier.
     */
    public long                   id;

    /**
     * The time when the bundle was last modified.
     */
    public long                   lastModified;

    /**
     * The bundle's state.
     */
    public int                    state;

    /**
     * The bundle's symbolic name.
     */
    public String                 symbolicName;

    /**
     * The bundle's version.
     */
    public String                 version;
}
