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

package org.osgi.test.cases.cdi.tb152_3_1;

import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.Bean;
import org.osgi.service.cdi.annotations.ComponentScoped;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.propertytypes.ServiceDescription;

@ApplicationScoped
@Bean
@SuppressWarnings({"rawtypes", "unchecked"})
public class One {

	@Inject
	@Reference
	@ServiceDescription("onInitialized")
	Function onInitialized;

	@Inject
	@Reference
	@ServiceDescription("onBeforeDestroyed")
	Function onBeforeDestroyed;

	@Inject
	@Reference
	@ServiceDescription("onDestroyed")
	Function onDestroyed;

	void onInitialized(@Observes @Initialized(ComponentScoped.class) Object obj, EventMetadata metadata, BeanManager bm) {
		onInitialized.apply(new Object[] {obj, bm, metadata});
	}

	void onBeforeDestroyed(@Observes @BeforeDestroyed(ComponentScoped.class) Object obj, EventMetadata metadata, BeanManager bm) {
		onBeforeDestroyed.apply(new Object[] {obj, bm, metadata});
	}

	void onDestroyed(@Observes @Destroyed(ComponentScoped.class) Object obj, EventMetadata metadata, BeanManager bm) {
		onDestroyed.apply(new Object[] {obj, bm, metadata});
	}

}
