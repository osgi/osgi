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
package org.osgi.test.cases.framework.secure.permissions.registerModify;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.test.cases.framework.secure.permissions.util.IService1;

/**
 * 
 * @author Shigekuni KONDO, Ikuo YAMASAKI, NTT Corporation
 */
public class IServiceImpl implements IService1 {

	private final BundleContext bc;

	IServiceImpl(BundleContext bc) {
		this.bc = bc;
	}

	public String getRegisteringBundleSymbolicName() {
		return bc.getBundle().getSymbolicName() + ":"
				+ bc.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
	}

}
