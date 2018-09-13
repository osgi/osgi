/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
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

import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.CollectionType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 *
 *
 */
@Component(name = "testConstructorInjection")
public class ConstructorInjection {
	@interface Config {
		String prop() default "default.prop";
	}

	@Activate
	public ConstructorInjection(ComponentContext cc,

			BundleContext bc,

			Map<String,Object> props,

			Config configNames,

			@Reference(name = "static", policy = ReferencePolicy.STATIC) EventListener fieldStatic,

			@Reference(name = "mandatory", cardinality = ReferenceCardinality.MANDATORY) EventListener fieldMandatory,

			@Reference(name = "optional", cardinality = ReferenceCardinality.OPTIONAL) EventListener fieldOptional,

			@Reference(name = "multiple", cardinality = ReferenceCardinality.MULTIPLE) List<EventListener> fieldMultiple,

			@Reference(name = "atleastone", cardinality = ReferenceCardinality.AT_LEAST_ONE) List<EventListener> fieldAtLeastOne,

			@Reference(name = "greedy", policyOption = ReferencePolicyOption.GREEDY) EventListener fieldGreedy,

			@Reference(name = "reluctant", policyOption = ReferencePolicyOption.RELUCTANT) EventListener fieldReluctant,

			@Reference(name = "replace", fieldOption = FieldOption.REPLACE) EventListener fieldReplace,

			@Reference(name = "target", target = "(test.attr=foo)") EventListener fieldTarget,

			@Reference(name = "bundle", scope = ReferenceScope.BUNDLE) EventListener fieldBundle,

			@Reference(name = "prototype", scope = ReferenceScope.PROTOTYPE) EventListener fieldPrototype,

			@Reference(name = "prototype_required", scope = ReferenceScope.PROTOTYPE_REQUIRED) EventListener fieldPrototypeRequired,

			@Reference(name = "reference") ServiceReference<EventListener> fieldReference,

			@Reference(name = "serviceobjects") ComponentServiceObjects<EventListener> fieldServiceObjects,

			@Reference(name = "properties", service = EventListener.class) Map<String,Object> fieldProperties,

			@Reference(name = "tuple") Map.Entry<Map<String,Object>,EventListener> fieldTuple,

			@Reference(name = "collection_serviceobjects") Collection<ComponentServiceObjects<EventListener>> fieldServiceObjectsM,

			@Reference(name = "collection_reference") Collection<ServiceReference<EventListener>> fieldServiceReferencesM,

			@Reference(name = "collection_properties", service = EventListener.class) Collection<Map<String,Object>> fieldPropertiesM,

			@Reference(name = "collection_tuple") Collection<Map.Entry<Map<String,Object>,EventListener>> fieldTupleM,

			@Reference(name = "collection_specified", service = Map.class, collectionType = CollectionType.SERVICE) Collection<Map<String,Object>> fieldServiceM) {
		/**/
	}
}
