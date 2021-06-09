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
package org.osgi.service.feature;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The Features service is the primary entry point for interacting with the feature model.
 * @ThreadSafe
 */
@ProviderType
public interface FeatureService {
    /**
     * Get a factory which can be used to build feature model entities.
     * @return A builder factory.
     */
    public BuilderFactory getBuilderFactory();

    /**
     * Read a Feature from JSON
     * @param jsonReader A Reader to the JSON input
     * @return The Feature represented by the JSON
     * @throws IOException When reading fails
     */
    public Feature readFeature(Reader jsonReader) throws IOException;

    /**
     * Write a Feature Model to JSON
     * @param feature the Feature to write.
     * @param jsonWriter A Writer to which the Feature should be written.
     * @throws IOException When writing fails.
     */
    public void writeFeature(Feature feature, Writer jsonWriter) throws IOException;
}
