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

package org.osgi.test.cases.cdi.beans;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.annotations.SingleComponent;
import org.osgi.service.log.Logger;
import org.osgi.test.cases.cdi.interfaces.BeanService;

@SingleComponent
@Service({BeanService.class, Instance_ServiceProperties.class})
public class Instance_ServiceProperties implements BeanService<Map<String, Object>> {

	@Override
	public String doSomething() {
		return String.valueOf(_instance.size());
	}

	@Override
	public Map<String, Object> get() {
		Iterator<Map<String, Object>> iterator = _instance.iterator();
		if (iterator.hasNext())
			return iterator.next();
		return null;
	}

	@Inject
	@Reference(Integer.class)
	List<Map<String, Object>> _instance;

	@Inject
	Logger logger;

	@PostConstruct
	private void postConstructed() {
		logger.info("PostConstructed {}", this);
	}

	@PreDestroy
	private void preDestroyed() {
		logger.info("PreDestroyed {}", this);
	}

}
