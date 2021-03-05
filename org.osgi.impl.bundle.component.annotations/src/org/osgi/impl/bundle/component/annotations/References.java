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
package org.osgi.impl.bundle.component.annotations;

import java.util.EventListener;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 *
 *
 */
@Component(name = "testReferences")
public class References {
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

	@Reference(name = "static", policy = ReferencePolicy.STATIC)
	private void bindStatic(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindStatic(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "dynamic", policy = ReferencePolicy.DYNAMIC)
	private void bindDynamic(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindDynamic(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "mandatory", cardinality = ReferenceCardinality.MANDATORY)
	private void bindMandatory(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindMandatory(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "optional", cardinality = ReferenceCardinality.OPTIONAL)
	private void bindOptional(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindOptional(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "multiple", cardinality = ReferenceCardinality.MULTIPLE)
	private void bindMultiple(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindMultiple(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "atleastone", cardinality = ReferenceCardinality.AT_LEAST_ONE)
	private void bindAtLeastOne(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindAtLeastOne(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "greedy", policyOption = ReferencePolicyOption.GREEDY)
	private void bindGreedy(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindGreedy(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "reluctant", policyOption = ReferencePolicyOption.RELUCTANT)
	private void bindReluctant(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindReluctant(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "updated", updated = "updatedUpdated")
	private void bindUpdated(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void updatedUpdated(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindUpdated(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "unbind", unbind = "fooUnbind")
	private void bindUnbind(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void fooUnbind(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "missingunbind")
	private void bindMissingUnbind(EventListener l) {
		System.out.println("Bind " + l);
	}

	@Reference(name = "nounbind", unbind = "-")
	private void bindNoUnbind(EventListener l) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindNoUnbind(EventListener l) {
		System.out.println("Unbind " + l);
	}

	@Reference(name = "target", target = "(test.attr=foo)")
	private void bindTarget(EventListener l, Map<?, ?> props) {
		System.out.println("Bind " + l);
	}

	@SuppressWarnings("unused")
	private void unbindTarget(EventListener l, Map<?, ?> props) {
		System.out.println("Unbind " + l);
	}
}
