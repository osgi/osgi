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
package org.osgi.impl.bundle.annotations.export.substitution;

public class ExportSubstitution {
	public static class Default implements
			org.osgi.impl.bundle.annotations.export.ExportConsumerType {
		//
	}

	public static class Calculated implements
			org.osgi.impl.bundle.annotations.export.calculated.ExportProviderType {
		//
	}

	public static class Consumer implements
			org.osgi.impl.bundle.annotations.export.consumer.ExportProviderType {
		//
	}

	public static class Noimport implements
			org.osgi.impl.bundle.annotations.export.noimport.ExportProviderType {
		//
	}

	public static class Provider implements
			org.osgi.impl.bundle.annotations.export.provider.ExportConsumerType {
		//
	}
}
