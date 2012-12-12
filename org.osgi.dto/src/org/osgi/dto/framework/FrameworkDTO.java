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

import java.util.List;
import java.util.Map;
import org.osgi.dto.DTO;

/**
 * Data Transfer Object for a Framework.
 * 
 * <p>
 * The System Bundle can be adapted to provide a {@code FrameworkDTO} for the
 * framework of the system bundle. A {@code FrameworkDTO} obtained from a
 * framework will contain only the launch properties of the framework. These
 * properties will not include the System properties.
 * 
 * @author $Id$
 * @NotThreadSafe
 */
public class FrameworkDTO extends DTO {
    /**
     * The bundles that are installed in the framework.
     */
    public List<BundleDTO>           bundles;

    /**
     * The launch properties of the framework.
     * 
     * The value type must be a numerical type, Boolean, String, DTO or an array
     * of any of the former.
     */
    public Map<String, Object>       properties;

    /**
     * The services that are registered in the framework.
     */
    public List<ServiceReferenceDTO> services;
}
