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

package org.osgi.test.cases.dmt.tc3.tbc.ConfigurationPlugin;

import org.osgi.service.dmt.DmtException;

/**
 * Thrown by different components of the confiugration plugin implementation,
 * to indicate DMT errors without knowing all the parameters. 
 */
class ConfigPluginException extends Exception {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private int					code;

    /**
     * Creates a new ConfigPluginException with the given error code and detail
     * message. 
     */
    ConfigPluginException(int code, String message) {
        super(message);
        
        this.code = code;
    }
    
    int getCode() {
        return code;
    }
    
    DmtException getDmtException(String[] path) {
        return new DmtException(path, code, getMessage());
    }
}
