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

package org.osgi.dto;

import java.io.Serializable;

/**
 * Marker interface for Data Transfer Objects.
 * 
 * All data transfer objects are trivially serializable and only use basic Java
 * numerical types, String and other DTOs.
 * 
 * @author $Id$
 * @NotThreadSafe
 */
public interface DTO extends Serializable {
    // marker interface
}
