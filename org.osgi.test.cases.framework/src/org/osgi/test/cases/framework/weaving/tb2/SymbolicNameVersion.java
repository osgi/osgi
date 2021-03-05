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
package org.osgi.test.cases.framework.weaving.tb2;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * This class is used to identify the bundle from which it
 * was loaded. This allows the CT to verify that various
 * constrained Dynamic-Import additions are followed correctly
 * 
 * @author IBM
 */
public class SymbolicNameVersion {
	/**
	 * Return the symbolic name and version of 
	 * the bundle that loaded this class
	 */
    public String toString() {
    	Bundle b = FrameworkUtil.getBundle(this.getClass());
    	return b.getSymbolicName() + "_" + b.getVersion();
    }
}
