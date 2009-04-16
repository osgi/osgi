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

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class RegionCodeInjection extends BaseTestComponent{

    public RegionCodeInjection(String componentId) {
        super(componentId);
        setArgumentValue("arg1", componentId);
    }

    public RegionCodeInjection(String componentId, RegionCode arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);

    }

    public void setRegionCode(RegionCode arg2) {
        this.setPropertyValue("regionCode", arg2, RegionCode.class);
    }


}
