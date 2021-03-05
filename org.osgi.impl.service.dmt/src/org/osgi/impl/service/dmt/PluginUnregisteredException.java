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

package org.osgi.impl.service.dmt;

/**
 * Indicates that a plugin has been unregistered while in use by a session.
 * It is thrown by PluginSessionWrapper and caught by SessionWrapper, which 
 * invalidates the session (rolling back if needed), and throws a
 * DmtIllegalStateException.  The exceptions have to be runtime, becasue
 * isNodeUri cannot throw checked exceptions.
 */
public class PluginUnregisteredException extends RuntimeException {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String				uri;
    
    public PluginUnregisteredException(String[] path) {
        uri = path == null ? null : Node.convertPathToUri(path);
    }
    
    @Override
	public String getMessage() {
        return "The plugin handling the node '" + uri + "' has been " +
                "unregistered while in use by a session.";
    }
}
