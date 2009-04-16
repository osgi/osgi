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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.blueprint.reflect.*;


/**
 * A ListValue implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class ListValueImpl implements ListValue {
    private List list;
    private String valueType;

    public ListValueImpl(List list) {
        this.list = list;
    }

    public ListValueImpl(ListValue source) {
        this.list = new ArrayList();
        Iterator i = source.getList().iterator();
        while (i.hasNext()) {
            list.add(NamespaceUtil.cloneValue((Value)i.next()));
        }
        valueType = source.getValueType();
    }


    /**
     * The List (of Value objects) for this List-based value
     */
    public List getList() {
        return list;
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
}


