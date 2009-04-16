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

import org.osgi.service.blueprint.reflect.ComponentMetadata;


/**
 * A ComponentMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class ComponentMetadataImpl implements ComponentMetadata {
    // the name of the component;
    private String name;
    // the dependency set
    private Set dependencies;

    protected ComponentMetadataImpl() {
        this((String)null);
    }

    protected ComponentMetadataImpl(String name) {
        this.name = name;
        dependencies = new HashSet();
    }

    protected ComponentMetadataImpl(ComponentMetadata source) {
        name = source.getName();
        dependencies = new HashSet(source.getExplicitDependencies());
    }



    /**
     * The name of the component.
     *
     * @return component name. The component name may be null if this is an anonymously
     * defined inner component.
     */
    public String getName() {
        return name;
    }

    /**
     * The names of any components listed in a "depends-on" attribute for this
     * component.
     *
     * @return an immutable set of component names for components that we have explicitly
     * declared a dependency on, or an empty set if none.
     */
    public Set getExplicitDependencies() {
        return dependencies;
    }


    /**
     * Add a new dependency to the explicit list.
     *
     * @param name   The new dependency name.
     */
    public void addDependency(String name) {
        dependencies.add(name);
    }
}

