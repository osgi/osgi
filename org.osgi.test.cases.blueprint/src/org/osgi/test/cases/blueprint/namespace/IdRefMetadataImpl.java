/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.blueprint.namespace;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.blueprint.reflect.NonNullMetadata;
import org.osgi.service.blueprint.reflect.IdRefMetadata;

/**
 * A value which represents the name of another component in the module context.
 * The name itself will be injected, not the component that the name refers to.
 *
 */
public class IdRefMetadataImpl extends NonNullMetadataImpl implements IdRefMetadata {
    // the name of the component;
    private String id;

    protected IdRefMetadataImpl() {
        this((String)null);
    }

    protected IdRefMetadataImpl(String id) {
        super();
        this.id = id;
    }

    protected IdRefMetadataImpl(IdRefMetadata source) {
        super();
        id = source.getComponentId();
    }


    /**
     * The name of the component.
     *
     * @return component name. The component name may be null if this is an anonymously
     * defined inner component.
     */
    public String getComponentId() {
        return id;
    }


    public void setComponentId(String id) {
        this.id = id;
    }
}

