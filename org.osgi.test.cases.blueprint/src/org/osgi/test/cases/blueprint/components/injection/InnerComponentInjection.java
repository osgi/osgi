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

package org.osgi.test.cases.blueprint.components.injection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;

public class InnerComponentInjection extends BaseTestComponent {

    public InnerComponentInjection() {
        super();
    }

    // Manually inject componentId
    public InnerComponentInjection(String componentId) {
        super(componentId);
        setArgumentValue("arg1", componentId);
    }

    public InnerComponentInjection(String componentId, ComponentTestInfo arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
    }

    public InnerComponentInjection(String componentId, ComponentTestInfo arg2, ComponentTestInfo arg3) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
        setArgumentValue("arg3", arg3);
    }

    // Don't care about componentId. This injection form is used to validate values, not events
    public InnerComponentInjection(ComponentTestInfo arg1) {
        super();
        setArgumentValue("arg1", arg1);
    }

    public InnerComponentInjection(ComponentTestInfo arg1, ComponentTestInfo arg2) {
        super();
        setArgumentValue("arg1", arg1);
        setArgumentValue("arg2", arg2);
    }

    public InnerComponentInjection(List arg1) {
        super();
        setArgumentValue("arg1", arg1, List.class);
    }

    public InnerComponentInjection(Set arg1) {
        super();
        setArgumentValue("arg1", arg1, Set.class);
    }

    public InnerComponentInjection(Map arg1) {
        super();
        setArgumentValue("arg1", arg1, Map.class);
    }


    //Property Setter
    public void setInnerComponent(ComponentTestInfo arg1) {
        this.setPropertyValue("innerComponent", arg1, PropertyInjection.class);
    }

}
