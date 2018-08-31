/*
 * Copyright (c) OSGi Alliance (2012, 2015). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.impl.bundle.component.annotations;

import java.util.EventListener;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 *
 *
 */
@Component(name = "testReferenceScopes")
public class ReferenceScopes {
	/**
	 */
	@Activate
	private void activate() {
		System.out.println("Hello World!");
	}

	/**
	 */
	@Deactivate
	private void deactivate() {
		System.out.println("Goodbye World!");
	}

	@Reference(name = "bundle", scope = ReferenceScope.BUNDLE)
	private void bindBundle(ServiceReference<EventListener> ref, EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindBundle(EventListener l, ServiceReference<EventListener> ref) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "prototype", scope = ReferenceScope.PROTOTYPE)
	private void bindPrototype(ComponentServiceObjects<EventListener> cso, EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindPrototype(EventListener l, ComponentServiceObjects<EventListener> cso) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "prototype_required", scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private void bindPrototypeRequired(EventListener l,
			Map<String,Object> properties) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindPrototypeRequired(EventListener l, Map<String, Object> properties) {
		System.out.println("Unbind " + l);
	}

}
