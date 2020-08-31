/*
 * Copyright (c) OSGi Alliance (2020). All Rights Reserved.
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

package org.osgi.util.feature;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Interface implemented by a callback that can resolve merge conflicts.
 * @param <T> The type of entity this conflict resolver is used for.
 * @param <R> The type of the result of the resolution.
 * @ThreadSafe
 */
@ConsumerType
public interface ConflictResolver<T, R> {
    /**
     * Resolve this conflict between o1 and o2.
     * @param f1 The first feature model.
     * @param o1 The first conflicting object.
     * @param f2 The second feature model
     * @param o2 The second conflicting object.
     * @return The resolution of the conflict.
     */
    R resolve(Feature f1, T o1, Feature f2, T o2);
}
