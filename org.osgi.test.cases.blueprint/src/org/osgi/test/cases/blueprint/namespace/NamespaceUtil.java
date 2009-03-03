/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.blueprint.namespace;

import java.util.Properties;
import java.util.Iterator;

import org.osgi.service.blueprint.reflect.*;


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

