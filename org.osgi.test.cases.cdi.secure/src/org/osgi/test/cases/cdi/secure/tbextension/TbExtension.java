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
package org.osgi.test.cases.cdi.secure.tbextension;

import java.time.Instant;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;

import org.osgi.annotation.bundle.Capability;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.dictionary.Dictionaries;

@Capability(namespace = "osgi.cdi.extension", name = "tb.extension")
@Header(name = Constants.BUNDLE_ACTIVATOR, value = "${@class}")
public class TbExtension implements BundleActivator, Extension,
		PrototypeServiceFactory<Extension> {

	private ServiceRegistration<Extension> registration;

	@SuppressWarnings("serial")
	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		final Instant whenExtensionCalled = Instant.now();

		abd.addBean()
				.addType(Instant.class)
				.addQualifier(new AnnotationLiteral<TBExtensionCalled>() {
				})
				.produceWith(i -> whenExtensionCalled);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		registration = context.registerService(Extension.class,
				(PrototypeServiceFactory<Extension>) this, Dictionaries
						.dictionaryOf("osgi.cdi.extension", "tb.extension"));
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (registration != null) {
			registration.unregister();
		}

	}

	public Extension getService(Bundle bundle,
			ServiceRegistration<Extension> registration) {
		return this;
	}

	public void ungetService(Bundle bundle,
			ServiceRegistration<Extension> registration, Extension service) {
	}

}
