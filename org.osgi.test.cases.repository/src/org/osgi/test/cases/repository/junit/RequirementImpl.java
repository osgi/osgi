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
package org.osgi.test.cases.repository.junit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

/**
 * @author David Bosschaert
 */
public class RequirementImpl implements Requirement {
    private final String namespace;
    private final Map<String, String> directives = new HashMap<String, String>();

    public RequirementImpl(String ns) {
        namespace = ns;
    }

    public RequirementImpl(String ns, String filter) {
        namespace = ns;
        directives.put("filter", filter);
    }

    public String getNamespace() {
        return namespace;
    }

    public Map<String, String> getDirectives() {
        return directives;
    }

    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    public Resource getResource() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((directives == null) ? 0 : directives.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequirementImpl other = (RequirementImpl) obj;
        if (directives == null) {
            if (other.directives != null)
                return false;
        } else if (!directives.equals(other.directives))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        return true;
    }
}
