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

package org.osgi.test.cases.blueprint.components.cmsupport;

import java.util.Iterator;
import java.util.Map;

import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class ManagedComponentInjection extends BaseTestComponent {

    public static int count = 0;

    public ManagedComponentInjection() {
        super();
    }

    // need manually inject the componentId
    public ManagedComponentInjection(String componentId) {
        // single string argument can only be the componentId.
        super(componentId);
        setArgumentValue("arg1", componentId);
    }

    public ManagedComponentInjection(String componentId, String arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
    }

    public ManagedComponentInjection(String componentId, Boolean arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
    }

    //setter
    public void setString(String value) {
        setPropertyValue("string", value, String.class);
    }

    public void setBoolean(Boolean value) {
        setPropertyValue("boolean", value, Boolean.class);
    }

    public void setByte(Byte value) {
        setPropertyValue("byte", value, Byte.class);
    }

    public void setCharacter(Character value) {
        setPropertyValue("character", value, Character.class);
    }

    public void setDouble(Double value) {
        setPropertyValue("double", value, Double.class);
    }

    public void setFloat(Float value) {
        setPropertyValue("float", value, Float.class);
    }

    public void setInteger(Integer value) {
        setPropertyValue("integer", value, Integer.class);
    }

    public void setLong(Long value) {
        setPropertyValue("long", value, Long.class);
    }

    public void setShort(Short value) {
        setPropertyValue("short", value, Short.class);
    }

    // component-managed strategy
    public void makeUpdate(Map properties) {
        Iterator it = properties.keySet().iterator();
        while(it.hasNext()){
            String key = (String) it.next();
            setPropertyValue(key, properties.get(key));
        }
        AssertionService.sendEvent(this, AssertionService.METHOD_CALLED);
    }

    // init
    public void onInit() {
        AssertionService.sendPropertyNameEvent(this, AssertionService.METHOD_CALLED, "ManagedComponentInjection_onInit");

    }

    // destroy
    public void onDestroy(int reasonCode) {
        if (reasonCode==1) {
            AssertionService.sendPropertyNameEvent(this, AssertionService.METHOD_CALLED, "ManagedComponentInjection_onDestroy_ConfigDeleted");
        }
        if (reasonCode==2){
            AssertionService.sendPropertyNameEvent(this, AssertionService.METHOD_CALLED, "ManagedComponentInjection_onDestroy_BundleStopping");
        }

    }

}
