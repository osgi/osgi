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

import org.osgi.service.blueprint.reflect.BindingListenerMetadata;
import org.osgi.service.blueprint.reflect.Value;


/**
 * A BindingListenerMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class BindingListenerMetadataImpl implements BindingListenerMetadata {
    private Value listenerComponent;
    private String bindMethodName;
    private String unbindMethodName;

    public BindingListenerMetadataImpl()
    {
    }

    public BindingListenerMetadataImpl(BindingListenerMetadata source) {
        listenerComponent = NamespaceUtil.cloneValue(source.getListenerComponent());
        bindMethodName = source.getBindMethodName();
        unbindMethodName = source.getUnbindMethodName();
    }



    /**
     * The component instance that will receive bind and unbind
     * events. The returned value must reference a component and therefore be
     * either a ComponentValue, ReferenceValue, or ReferenceNameValue.
     *
     * @return the listener component reference.
     */
    public Value getListenerComponent() {
        return listenerComponent;
    }

    public void setListenerComponent(Value v) {
        listenerComponent = v;
    }


    /**
     * The name of the method to invoke on the listener component when
     * a matching service is bound to the reference
     *
     * @return the bind callback method name.
     */
    public String getBindMethodName() {
        return bindMethodName;
    }

    public void setBindMethodName(String name) {
        bindMethodName = name;
    }



    /**
     * The name of the method to invoke on the listener component when
     * a service is unbound from the reference.
     *
     * @return the unbind callback method name.
     */
    public String getUnbindMethodName() {
        return unbindMethodName;
    }

    public void setUnbindMethodName(String name) {
        unbindMethodName = name;
    }
}

