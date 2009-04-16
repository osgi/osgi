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

import org.osgi.service.blueprint.reflect.ArrayValue;
import org.osgi.service.blueprint.reflect.CollectionBasedServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.ComponentValue;
import org.osgi.service.blueprint.reflect.ListValue;
import org.osgi.service.blueprint.reflect.LocalComponentMetadata;
import org.osgi.service.blueprint.reflect.MapValue;
import org.osgi.service.blueprint.reflect.NullValue;
import org.osgi.service.blueprint.reflect.PropertiesValue;
import org.osgi.service.blueprint.reflect.ReferenceNameValue;
import org.osgi.service.blueprint.reflect.ReferenceValue;
import org.osgi.service.blueprint.reflect.ServiceExportComponentMetadata;
import org.osgi.service.blueprint.reflect.SetValue;
import org.osgi.service.blueprint.reflect.TypedStringValue;
import org.osgi.service.blueprint.reflect.UnaryServiceReferenceComponentMetadata;
import org.osgi.service.blueprint.reflect.Value;


/**
 * A utility class that handles cloning various polymorphic
 * bits of metadata into concrete class implementations.
 */
public class NamespaceUtil {
    static public Value cloneValue(Value source) {
        if (source instanceof ArrayValue) {
            return new ArrayValueImpl((ArrayValue)source);
        }
        else if (source instanceof ComponentValue) {
            return new ComponentValueImpl((ComponentValue)source);
        }
        else if (source instanceof ListValue) {
            return new ListValueImpl((ListValue)source);
        }
        else if (source instanceof SetValue) {
            return new SetValueImpl((SetValue)source);
        }
        else if (source instanceof MapValue) {
            return new MapValueImpl((MapValue)source);
        }
        else if (source instanceof NullValue) {
            return new NullValueImpl();
        }
        else if (source instanceof PropertiesValue) {
            return new PropertiesValueImpl((PropertiesValue)source);
        }
        else if (source instanceof PropertiesValue) {
            return new PropertiesValueImpl((PropertiesValue)source);
        }
        else if (source instanceof ReferenceNameValue) {
            return new ReferenceNameValueImpl((ReferenceNameValue)source);
        }
        else if (source instanceof ReferenceValue) {
            return new ReferenceValueImpl((ReferenceValue)source);
        }
        else if (source instanceof TypedStringValue) {
            return new TypedStringValueImpl((TypedStringValue)source);
        }

        throw new RuntimeException("Unknown Value type received: " + source.getClass().getName());
    }


    /**
     * Clone a component metadata item, returning a mutable
     * instance.
     *
     * @param source The source metadata item.
     *
     * @return A mutable instance of this metadata item.
     */
    static public ComponentMetadata cloneComponentMetadata(ComponentMetadata source) {
        if (source instanceof LocalComponentMetadata) {
            return new LocalComponentMetadataImpl((LocalComponentMetadata)source);
        }
        else if (source instanceof CollectionBasedServiceReferenceComponentMetadata) {
            return new CollectionBasedServiceReferenceComponentMetadataImpl((CollectionBasedServiceReferenceComponentMetadata)source);
        }
        else if (source instanceof CollectionBasedServiceReferenceComponentMetadata) {
            return new CollectionBasedServiceReferenceComponentMetadataImpl((CollectionBasedServiceReferenceComponentMetadata)source);
        }
        else if (source instanceof ServiceExportComponentMetadata) {
            return new ServiceExportComponentMetadataImpl((ServiceExportComponentMetadata)source);
        }
        else if (source instanceof ServiceExportComponentMetadata) {
            return new ServiceExportComponentMetadataImpl((ServiceExportComponentMetadata)source);
        }
        else if (source instanceof UnaryServiceReferenceComponentMetadata) {
            return new UnaryServiceReferenceComponentMetadataImpl((UnaryServiceReferenceComponentMetadata)source);
        }

        throw new RuntimeException("Unknown ComponentMetadata type received: " + source.getClass().getName());
    }

}

