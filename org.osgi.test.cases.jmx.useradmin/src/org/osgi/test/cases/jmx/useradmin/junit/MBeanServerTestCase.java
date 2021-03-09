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

package org.osgi.test.cases.jmx.useradmin.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;


@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class MBeanServerTestCase {

	@BeforeEach
	protected void setUp(@InjectBundleContext BundleContext context) {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		context.registerService(MBeanServer.class,
                mBeanServer, null);
	}
	
	@Test
	public void testMBeanServerExistence(
			@InjectService MBeanServer mBeanServer) {
		assertThat(mBeanServer).isNotNull();
	}
}
