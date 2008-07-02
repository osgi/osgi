/*
 * $Date: 2008-04-02 12:42:59 -0800 $
 *
 * Copyright (c) OSGi Alliance (2004, 2007). All Rights Reserved.
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

package org.osgi.service.discovery;

import java.util.Collection;

 /** 
 * 
 * 
 * @version $Revision: 4930 $
 */
public interface FindServiceCallback {
    /**
     * @param serviceDescriptions Collection of ServiceDescription objects satisfying the find criteria.
     * The Collection may be empty if none was found.
     */
    void servicesFound(Collection serviceDescriptions);
}
