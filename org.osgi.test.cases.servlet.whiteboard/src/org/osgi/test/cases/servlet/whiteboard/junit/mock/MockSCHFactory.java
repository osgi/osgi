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
package org.osgi.test.cases.servlet.whiteboard.junit.mock;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.servlet.context.ServletContextHelper;

public class MockSCHFactory implements ServiceFactory<ServletContextHelper> {

	@Override
	public ServletContextHelper getService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> registration) {

		return new ServletContextHelper(bundle) {};
	}

	@Override
	public void ungetService(
			Bundle bundle,
			ServiceRegistration<ServletContextHelper> registration,
			ServletContextHelper service) {
	}

}