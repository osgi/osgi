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

package org.osgi.test.cases.component.tb3.impl;

import org.osgi.test.cases.component.service.ServiceProvider;

/**
 * This class is extended by the service implementation. Its purpose is to
 * verify that the SCR will also try to lookup the methods from all super
 * classes if they are not found in top class containing the component
 * implementation.
 * 
 * @author $Id$
 */
public class DefaultBindImpl {
	protected ServiceProvider	serviceProvider;

	protected void bindServiceProvider(ServiceProvider sp) {
		this.serviceProvider = sp;
	}

	protected void unbindServiceProvider(ServiceProvider sp) {
		this.serviceProvider = null;
	}

}
