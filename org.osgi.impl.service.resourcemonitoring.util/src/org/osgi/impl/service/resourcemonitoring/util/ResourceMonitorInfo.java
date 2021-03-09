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

package org.osgi.impl.service.resourcemonitoring.util;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.resourcemonitoring.ResourceMonitor;

/**
 * 
 */
public class ResourceMonitorInfo {

	private final String	resourceContextName;
	private boolean			enabled;

	/**
	 * @param pResourceContextName
	 * @param pEnabled
	 */
	public ResourceMonitorInfo(final String pResourceContextName,
			final boolean pEnabled) {
		resourceContextName = pResourceContextName;
		enabled = pEnabled;
	}

	/**
	 * @param resourceMonitor
	 */
	public ResourceMonitorInfo(final ResourceMonitor<Long> resourceMonitor) {
		resourceContextName = resourceMonitor.getContext().getName();
		enabled = resourceMonitor.isEnabled();
	}

	/**
	 * @param memoryMonitorInfoAsCsv
	 */
	public ResourceMonitorInfo(final String memoryMonitorInfoAsCsv) {
		String[] splitted = split(memoryMonitorInfoAsCsv, ";");
		if ((splitted != null) && (splitted.length == 2)) {
			resourceContextName = splitted[0];
			enabled = Boolean.valueOf(splitted[1]).booleanValue();
		} else {
			throw new NullPointerException(
					"ResourceContextName or Enabled is null");
		}

	}

	/**
	 * @return resource context name.
	 */
	public String getResourceContextName() {
		return resourceContextName;
	}

	/**
	 * @return true if enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param pEnabled
	 */
	public void setEnabled(boolean pEnabled) {
		enabled = pEnabled;
	}

	/**
	 * @return csv data.
	 */
	public String toCsv() {
		return resourceContextName + ";" + enabled;
	}

	private static String[] split(String inputString, String separatingCharacter) {
		// List<String> parts = new ArrayList<String>();
		List<String> parts = new ArrayList<>();
		int length = inputString.length();
		int fromIndex = 0;
		int index = 0;
		while ((index = inputString.indexOf(separatingCharacter, fromIndex)) != -1) {
			// substringing the part between fromIndex and index
			String part = inputString.substring(fromIndex, index);
			parts.add(part);

			// update fromIndex
			fromIndex = index + 1;
		}
		parts.add(inputString.substring(fromIndex, length));

		return parts.toArray(new String[parts.size()]);
	}

}
