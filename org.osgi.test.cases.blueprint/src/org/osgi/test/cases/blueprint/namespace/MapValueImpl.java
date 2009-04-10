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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.blueprint.reflect.*;


/**
 * A MapValue implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class MapValueImpl implements MapValue {
    private Map map;
    private String valueType;
    private String keyType;

    public MapValueImpl(Map map) {
        this.map = map;
    }

    public MapValueImpl(MapValue source) {
        this.map = new HashMap();
        // both the values and the keys are Value types, so we need to deep copy them
        Iterator i = source.getMap().entrySet().iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            map.put(NamespaceUtil.cloneValue((Value)entry.getKey()), NamespaceUtil.cloneValue((Value)entry.getValue()));
        }
        valueType = source.getValueType();
        keyType = source.getKeyType();
    }


    /**
     * The Map of Value->Value mappings for this map-based value
     */
    public Map getMap() {
        return map;
    }

    /**
     * The value-type specified for the list elements, or null if none given
     */
	public String getValueType() {
        return valueType;
    }

    public void setValueType(String type) {
        valueType = type;
    }

	/**
	 * The key-type specified for map keys, or null if none given
	 */
	public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String type) {
        keyType = type;
    }
}

