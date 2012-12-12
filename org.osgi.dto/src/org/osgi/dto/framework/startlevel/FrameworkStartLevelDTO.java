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

package org.osgi.dto.framework.startlevel;

import org.osgi.dto.DTO;

/**
 * Data Transfer Object for a FrameworkStartLevel.
 * 
 * <p>
 * The System Bundle can be adapted to provide a {@code FrameworkStartLevelDTO}
 * for the framework of the Bundle.
 * 
 * @author $Id$
 * @NotThreadSafe
 */
public class FrameworkStartLevelDTO extends DTO {
    /**
     * The active start level value for the framework.
     */
    public int                startLevel;

    /**
     * The initial start level value that is assigned to a bundle when it is
     * first installed.
     */
    public int                initialBundleStartLevel;
}
