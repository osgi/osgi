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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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
@Component(name = "testFieldReferences")
public class FieldReferences {
	public FieldReferences() {
		fieldReferenceM = new ArrayList<ServiceReference<EventListener>>();
	}

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
	private EventListener fieldStatic;

	@Reference(name = "dynamic", policy = ReferencePolicy.DYNAMIC)
	private volatile EventListener fieldDynamic;

	@Reference(name = "mandatory", cardinality = ReferenceCardinality.MANDATORY)
	private EventListener fieldMandatory;

	@Reference(name = "optional", cardinality = ReferenceCardinality.OPTIONAL)
	private EventListener fieldOptional;

	@Reference(name = "multiple", cardinality = ReferenceCardinality.MULTIPLE)
	private List<EventListener> fieldMultiple;

	@Reference(name = "atleastone", cardinality = ReferenceCardinality.AT_LEAST_ONE)
	private List<EventListener> fieldAtLeastOne;

	@Reference(name = "greedy", policyOption = ReferencePolicyOption.GREEDY)
	private EventListener fieldGreedy;

	@Reference(name = "reluctant", policyOption = ReferencePolicyOption.RELUCTANT)
	private EventListener fieldReluctant;

	@Reference(name = "update", fieldOption = FieldOption.UPDATE)
	private EventListener fieldUpdate;

	@Reference(name = "replace", fieldOption = FieldOption.REPLACE)
	private EventListener fieldReplace;

	@Reference(name = "target", target = "(test.attr=foo)")
	private EventListener fieldTarget;

	@Reference(name = "bundle", scope = ReferenceScope.BUNDLE)
	private volatile EventListener fieldBundle;

	@Reference(name = "prototype", scope = ReferenceScope.PROTOTYPE)
	private EventListener fieldPrototype;

	@Reference(name = "prototype_required", scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private EventListener fieldPrototypeRequired;

	@Reference(name = "reference")
	private ServiceReference<EventListener> fieldReference;

	@Reference(name = "serviceobjects")
	private ComponentServiceObjects<EventListener> fieldServiceObjects;

	@Reference(name = "properties", service = EventListener.class)
	private volatile Map<String, Object> fieldProperties;

	@Reference(name = "tuple")
	private Map.Entry<Map<String, Object>, EventListener> fieldTuple;

	@Reference(name = "collection_reference", policy = ReferencePolicy.DYNAMIC)
	private final Collection<ServiceReference<EventListener>> fieldReferenceM;

	@Reference(name = "collection_serviceobjects")
	private Collection<ComponentServiceObjects<EventListener>> fieldServiceObjectsM;

	@Reference(name = "collection_properties", service = EventListener.class)
	private Collection<Map<String, Object>> fieldPropertiesM;

	@Reference(name = "collection_tuple")
	private Collection<Map.Entry<Map<String, Object>, EventListener>> fieldTupleM;
}
