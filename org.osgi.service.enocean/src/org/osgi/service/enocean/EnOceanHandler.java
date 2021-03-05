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

package org.osgi.service.enocean;

/**
 * The interface used to get callback answers from a RPC or a Message.
 * 
 * @author $Id$
 */
public interface EnOceanHandler {

    /**
     * Notifies of the answer to a RPC.
     * 
     * @param original the original {@link EnOceanRPC} that originated this
     *        answer.
     * @param payload the payload of the response; may be deserialized to an
     *        {@link EnOceanRPC} object.
     */
    public void notifyResponse(EnOceanRPC original, byte[] payload);

}
