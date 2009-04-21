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

import org.osgi.service.blueprint.reflect.Listener;
import org.osgi.service.blueprint.reflect.Target;



/**
 * Metadata for a listener interested in service bind and unbind events for a service
 * reference.
 */
public class ListenerImpl implements Listener {
    private Target listenerComponent;
    private String bindMethodName;
    private String unbindMethodName;

    public ListenerImpl()
    {
    }

    public ListenerImpl(Listener source) {
        listenerComponent = (Target)NamespaceUtil.cloneMetadata(source.getListenerComponent());
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
    public Target getListenerComponent() {
        return listenerComponent;
    }

    public void setListenerComponent(Target v) {
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

