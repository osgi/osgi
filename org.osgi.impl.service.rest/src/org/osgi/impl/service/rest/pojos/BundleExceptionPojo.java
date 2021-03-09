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

import org.osgi.framework.BundleException;
import org.osgi.impl.service.rest.PojoReflector.RootNode;

/**
 * Pojo for bundle exceptions.
 * 
 * @author Jan S. Rellermeyer, IBM Research
 */
@RootNode(name = "bundleException")
public class BundleExceptionPojo {

	private int		typecode;
	private String	message;

	public BundleExceptionPojo(final BundleException be) {
		this.typecode = be.getType();
		this.message = be.toString();
	}

	public int getTypecode() {
		return typecode;
	}

	public void setTypecode(final int typecode) {
		this.typecode = typecode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

}
