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

package org.osgi.impl.service.rest.pojos;

import org.osgi.impl.service.rest.PojoReflector.RootNode;

/**
 * Pojo for the bundle state.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
@RootNode(name = "bundleState")
public final class BundleStatePojo {

	private int	state;

	private int	options;

	public BundleStatePojo() {

	}

	public BundleStatePojo(final int state) {
		this.state = state;
	}

	public void setState(final int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void setOptions(final int options) {
		this.options = options;
	}

	public int getOptions() {
		return options;
	}

}
