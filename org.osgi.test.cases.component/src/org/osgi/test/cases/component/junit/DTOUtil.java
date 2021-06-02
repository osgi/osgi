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

package org.osgi.test.cases.component.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.ReferenceDTO;

public class DTOUtil {

	public static ComponentDescriptionDTO newComponentDescriptionDTO(
			String name, BundleDTO bundle, String factory, String scope,
			String implementationClass, boolean defaultEnabled,
			boolean immediate, String[] serviceInterfaces,
			Map<String,Object> properties, ReferenceDTO[] references,
			String activate, String deactivate, String modified,
			String configurationPolicy, String[] configurationPid,
			Map<String,Object> factoryProperties, String[] activationFields,
			int init) {
		ComponentDescriptionDTO dto = new ComponentDescriptionDTO();
		dto.name = name;
		dto.bundle = bundle;
		dto.factory = factory;
		dto.scope = scope;
		dto.implementationClass = implementationClass;
		dto.defaultEnabled = defaultEnabled;
		dto.immediate = immediate;
		dto.serviceInterfaces = serviceInterfaces;
		dto.properties = properties;
		dto.references = addTrueConditionRef(references);
		dto.activate = activate;
		dto.deactivate = deactivate;
		dto.modified = modified;
		dto.configurationPolicy = configurationPolicy;
		dto.configurationPid = configurationPid;
		dto.factoryProperties = factoryProperties;
		dto.activationFields = activationFields;
		dto.init = init;
		return dto;
	}

	private static ReferenceDTO[] addTrueConditionRef(
			ReferenceDTO[] references) {
		if (references != null) {
			for (ReferenceDTO referenceDTO : references) {
				if (ComponentConstants.REFERENCE_NAME_SATISFYING_CONDITION
						.equals(referenceDTO.name)) {
					return references;
				}
			}
		}
		List<ReferenceDTO> result = new ArrayList<>();
		if (references != null) {
			for (ReferenceDTO referenceDTO : references) {
				result.add(referenceDTO);
			}
		}
		result.add(newReferenceDTO(
				ComponentConstants.REFERENCE_NAME_SATISFYING_CONDITION,
				"org.osgi.service.condition.Condition", "1..1", "dynamic",
				"reluctant", "(osgi.condition.id=true)", null, null, null, null,
				null, "bundle", null, null));
		return result.toArray(new ReferenceDTO[0]);
	}

	public static BundleDTO newBundleDTO(Bundle b) {
		return b.adapt(BundleDTO.class);
	}

	public static ReferenceDTO newReferenceDTO(String name,
			String interfaceName, String cardinality, String policy,
			String policyOption, String target, String bind, String unbind,
			String updated, String field, String fieldOption, String scope,
			Integer parameter, String collectionType) {
		ReferenceDTO dto = new ReferenceDTO();
		dto.name = name;
		dto.interfaceName = interfaceName;
		dto.cardinality = cardinality;
		dto.policy = policy;
		dto.policyOption = policyOption;
		dto.target = target;
		dto.bind = bind;
		dto.unbind = unbind;
		dto.updated = updated;
		dto.field = field;
		dto.fieldOption = fieldOption;
		dto.scope = scope;
		dto.parameter = parameter;
		dto.collectionType = collectionType;
		return dto;
	}

}
