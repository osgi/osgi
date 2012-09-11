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
import org.osgi.dto.MapDTO;

/**
 * Data Transfer Object for a Framework.
 * 
 * @author $Id$
 * @NotThreadSafe
 */
public class FrameworkDTO implements DTO {
    /**
     * DTOs are serializable.
     */
    private static final long     serialVersionUID = 1L;

    /**
     * The bundles that are installed in the framework.
     */
    public BundleDTO[]            bundles;

    /**
     * The properties for the framework.
     * 
     * The value type must be a numerical type, Boolean, String, DTO or an array
     * of any of the former.
     */
    public MapDTO<String, Object> properties;

    /**
     * The service that are registered in the framework.
     */
    public ServiceReferenceDTO[]  services;
}
