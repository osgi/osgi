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

package org.osgi.service.enocean.descriptions;

/**
 * Subinterface of {@link EnOceanChannelDescription} that describes boolean
 * channels.
 * 
 * @version 1.0
 * @author $Id$
 */
public interface EnOceanFlagChannelDescription extends EnOceanChannelDescription {
    // As it is written in the RFC: EnOcean Flag Channel Description
    // The EnOceanFlagChannelDescription interface inherits from the
    // EnOceanChannelDescription interface.
    // Those channels, are typically used for On/Off reporting values (like a
    // switch); they have no additional methods, though the deserialize() method
    // converts the input bit into a proper Boolean object.
}
