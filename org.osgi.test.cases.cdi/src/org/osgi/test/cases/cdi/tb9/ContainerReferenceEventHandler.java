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
/**
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
 */

package org.osgi.test.cases.cdi.tb9;

import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cdi.annotations.Bean;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.reference.BindServiceReference;
import org.osgi.service.log.Logger;
import org.osgi.test.cases.cdi.interfaces.Pojo;

@ApplicationScoped
@Bean
@Service
public class ContainerReferenceEventHandler implements Pojo {

	@Inject
	void integers(BindServiceReference<Integer> binder, Logger logger) {
		binder.adding(
			sr -> {
					logger.info("=====ADDING==>>> {}", sr);

				_services.put(sr, "ADDED");
			}
		).modified(
			sr -> {
					logger.info("=====UPDATING==>>> {}", sr);

				_services.put(sr, "UPDATED");
			}
		).removed(
			sr -> {
					logger.info("=====REMOVING==>>> {}", sr);

				_services.remove(sr);
			}
		).bind();
	}

	@Override
	public String foo(String fooInput) {
		return _services.values().toString();
	}

	@Override
	public int getCount() {
		return _services.size();
	}

	private final TreeMap<ServiceReference<Integer>, String> _services = new TreeMap<>();

}
