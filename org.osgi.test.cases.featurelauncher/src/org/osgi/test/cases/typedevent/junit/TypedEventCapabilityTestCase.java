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

package org.osgi.test.cases.typedevent.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.osgi.namespace.implementation.ImplementationNamespace.*;
import static org.osgi.namespace.service.ServiceNamespace.*;
import static org.osgi.resource.Namespace.CAPABILITY_USES_DIRECTIVE;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.featurelauncher.FeatureLauncher;
import org.osgi.service.featurelauncher.FeatureLauncherConstants;
import org.osgi.service.featurelauncher.repository.ArtifactRepository;
import org.osgi.service.featurelauncher.runtime.FeatureRuntime;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class TypedEventCapabilityTestCase {

	@InjectBundleContext
	BundleContext ctx;

	@ParameterizedTest
	@ValueSource(classes = {
			FeatureLauncher.class, ArtifactRepository.class,
			FeatureRuntime.class
	})
	public void test_service_capability(Class< ? > clz) {

		String className = clz.getName();

		ServiceReference< ? > ref = ctx.getServiceReference(clz);

		assertThat(ref).isNotNull();

		BundleWiring wiring = ref.getBundle().adapt(BundleWiring.class);

		@SuppressWarnings("unchecked")
		BundleCapability cap = wiring.getCapabilities(SERVICE_NAMESPACE)
				.stream()
				.filter(c -> ((List<String>) c.getAttributes()
						.get(CAPABILITY_OBJECTCLASS_ATTRIBUTE))
								.contains(className))
				.findFirst()
				.orElse(null);

		assertThat(cap).isNotNull();

		assertThat(cap.getDirectives().get(CAPABILITY_USES_DIRECTIVE))
				.isEqualTo(clz.getPackage().getName());
	}

	@Test
	public void test_implementation_capability() {

		BundleCapability implCap = Arrays.stream(ctx.getBundles())
				.map(b -> b.adapt(BundleWiring.class))
				.filter(bw -> bw != null)
				.map(bw -> bw.getCapabilities(IMPLEMENTATION_NAMESPACE))
				.flatMap(lbc -> lbc.stream()
						.filter(bc -> FeatureLauncherConstants.FEATURE_LAUNCHER_IMPLEMENTATION
								.equals(bc.getAttributes()
										.get(IMPLEMENTATION_NAMESPACE))))
				.findFirst()
				.orElse(null);

		assertThat(implCap).isNotNull();

		assertThat(implCap.getDirectives().get(CAPABILITY_USES_DIRECTIVE))
				.isEqualTo(FeatureLauncher.class.getPackage().getName());

		assertThat(implCap.getAttributes().get(CAPABILITY_VERSION_ATTRIBUTE))
				.isEqualTo(Version
						.parseVersion(
								FeatureLauncherConstants.FEATURE_LAUNCHER_SPECIFICATION_VERSION));
	}

}
