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

package org.osgi.test.cases.blueprint.framework;

import org.osgi.service.blueprint.reflect.ComponentMetadata;

/**
 * A validator for component metadata definitions.
 */
public interface TestComponentMetadata {
    /**
     * Validate a the contained component meta data against an actual instance
     *
     * @param blueprintMetadata
     *               The module metadata wrapper (needed for validating
     *               embedded types)
     * @param componentMeta
     *               The metadata target we validate against.
     *
     * @exception Exception
     */
    public void validate(BlueprintMetadata blueprintMetadata, ComponentMetadata componentMeta) throws Exception;


    /**
     * Perform a search match on component metadata.
     *
     * @param meta   The source metadata.
     *
     * @return true if this is considered a match. false if it is rejected.
     */
    public boolean matches(ComponentMetadata meta);


    /**
     * Retrieve the name for this component.  Used for validation
     * purposes.
     *
     * @return The String name of the component.  Returns null
     *         if no name has been provided.
     */
    public String getId();
}

