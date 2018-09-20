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

package org.osgi.test.cases.cdi.tb152_3_1;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Bean;
import org.osgi.service.cdi.annotations.FactoryComponent;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.annotations.Service;
import org.osgi.service.cdi.propertytypes.ServiceDescription;
import org.osgi.test.cases.cdi.interfaces.BeanService;

@Bean
@Service
@ServiceDescription("three")
@FactoryComponent
public class Three implements BeanService<Integer> {

	private volatile String status;

	public Three() {
		status = "CONSTRUCTED";
	}

	@Override
	public String doSomething() {
		return status;
	}

	@Override
	public Integer get() {
		return number;
	}

	@PostConstruct
	void postConstruct() {
		status = "POST_CONSTRUCTED";
	}

	@PreDestroy
	void preDestroy() {
		status = "DESTROYED";
	}

	@Inject
	@Reference
	@ServiceDescription("three")
	Integer number;
}
