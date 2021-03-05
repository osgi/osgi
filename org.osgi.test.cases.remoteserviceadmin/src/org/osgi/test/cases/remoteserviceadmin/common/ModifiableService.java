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

package org.osgi.test.cases.remoteserviceadmin.common;

/**
 * Simple service interface used in tb7 to provide a sample service to be
 * exported. The methods allow to change the services meta data to test the
 * behavior of the EndpointEventListener.
 * 
 * @author <a href="mailto:marc@marc-schaaf.de">Marc Schaaf</a>
 * 
 */
public interface ModifiableService {

	public void addServiceProperty();

	public void changeRequiredServiceProperty();
}
