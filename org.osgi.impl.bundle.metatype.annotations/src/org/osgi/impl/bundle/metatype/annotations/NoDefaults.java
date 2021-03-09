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

package org.osgi.impl.bundle.metatype.annotations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Icon;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@ObjectClassDefinition(id = "testNoDefaults",
		name = "%member.name",
		description = "%member.description",
		localization = "OSGI-INF/l10n/member",
		factoryPid = {"factoryPid1", "factoryPid2"},
		pid = {"pid1", "pid2"},
		icon = {@Icon(resource = "icon/member-32.png", size = 32), @Icon(resource = "icon/member-64.png", size = 64)})
public interface NoDefaults {
	@AttributeDefinition(
			description = "%member.password.description",
			name = "%member.password.name",
			type = AttributeType.PASSWORD)
	public String _password();

	@AttributeDefinition(cardinality = 12,
			defaultValue = "contributing",
			description = "%member.membertype.description",
			max = "max1",
			min = "min1",
			name = "%member.membertype.name",
			options = {
					@Option(label = "%strategic", value = "strategic"),
					@Option(label = "%principal", value = "principal"),
					@Option(label = "%contributing", value = "contributing")
			},
			required = true,
			type = AttributeType.STRING)
	public String type();

}
