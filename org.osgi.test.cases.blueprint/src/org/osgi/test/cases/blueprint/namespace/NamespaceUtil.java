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

import org.osgi.service.blueprint.reflect.CollectionMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.IdRefMetadata;
import org.osgi.service.blueprint.reflect.MapMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.NullMetadata;
import org.osgi.service.blueprint.reflect.PropsMetadata;
import org.osgi.service.blueprint.reflect.RefCollectionMetadata;
import org.osgi.service.blueprint.reflect.ReferenceMetadata;
import org.osgi.service.blueprint.reflect.RefMetadata;
import org.osgi.service.blueprint.reflect.ServiceMetadata;
import org.osgi.service.blueprint.reflect.ValueMetadata;

/**
 * A utility class that handles cloning various polymorphic
 * bits of metadata into concrete class implementations.
 */
public class NamespaceUtil {
    static public Metadata cloneMetadata(Metadata source) {
        if (source instanceof CollectionMetadata) {
            return new CollectionMetadataImpl((CollectionMetadata)source);
        }
        else if (source instanceof MapMetadata) {
            return new MapMetadataImpl((MapMetadata)source);
        }
        else if (source instanceof NullMetadata) {
            return new NullMetadataImpl();
        }
        else if (source instanceof PropsMetadata) {
            return new PropsMetadataImpl((PropsMetadata)source);
        }
        else if (source instanceof RefMetadata) {
            return new RefMetadataImpl((RefMetadataImpl)source);
        }
        else if (source instanceof IdRefMetadata) {
            return new IdRefMetadataImpl((IdRefMetadataImpl)source);
        }
        else if (source instanceof ValueMetadata) {
            return new ValueMetadataImpl((ValueMetadata)source);
        }
        else if (source instanceof BeanMetadata) {
            return new BeanMetadataImpl((BeanMetadata)source);
        }
        else if (source instanceof RefCollectionMetadata) {
            return new RefCollectionMetadataImpl((RefCollectionMetadata)source);
        }
        else if (source instanceof ServiceMetadata) {
            return new ServiceMetadataImpl((ServiceMetadata)source);
        }
        else if (source instanceof ReferenceMetadata) {
            return new ReferenceMetadataImpl((ReferenceMetadata)source);
        }
        throw new RuntimeException("Unknown Value type received: " + source.getClass().getName());
    }
}

