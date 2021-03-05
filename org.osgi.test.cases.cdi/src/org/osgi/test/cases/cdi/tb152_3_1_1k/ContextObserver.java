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

package org.osgi.test.cases.cdi.tb152_3_1_1k;

import java.util.Map;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;

import org.osgi.service.cdi.annotations.ComponentProperties;
import org.osgi.service.cdi.annotations.ComponentScoped;
import org.osgi.service.cdi.annotations.Reference;
import org.osgi.service.cdi.propertytypes.ServiceDescription;

@ApplicationScoped
@SuppressWarnings({"rawtypes", "unchecked"})
public class ContextObserver {

	@Inject
	public ContextObserver(
		@Reference
		@ServiceDescription("onInitialized")
		Consumer onInitialized,
		@Reference
		@ServiceDescription("onBeforeDestroyed")
		Consumer onBeforeDestroyed,
		@Reference
		@ServiceDescription("onDestroyed")
		Consumer onDestroyed) {

		this.onInitialized = onInitialized;
		this.onBeforeDestroyed = onBeforeDestroyed;
		this.onDestroyed = onDestroyed;
	}

	private final Consumer onInitialized;
	private final Consumer onBeforeDestroyed;
	private final Consumer onDestroyed;

	void onInitialized(@Observes @Initialized(ComponentScoped.class) Object obj,
			@ComponentProperties Map<String, Object> properties, EventMetadata metadata) {
		onInitialized.accept(new Object[] { obj, properties, metadata });
	}

	void onBeforeDestroyed(@Observes @BeforeDestroyed(ComponentScoped.class) Object obj,
			@ComponentProperties Map<String, Object> properties, EventMetadata metadata) {
		onBeforeDestroyed.accept(new Object[] { obj, properties, metadata });
	}

	void onDestroyed(@Observes @Destroyed(ComponentScoped.class) Object obj,
			@ComponentProperties Map<String, Object> properties, EventMetadata metadata) {
		onDestroyed.accept(new Object[] { obj, properties, metadata });
	}

}
