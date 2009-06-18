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

/**
 * Test component for injection of wrapper array values into
 * a component constructor.
 */
public class WrapperArrayConstructorInjection extends BaseTestComponent {
    public WrapperArrayConstructorInjection(Integer[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Integer[].class);
    }

    public WrapperArrayConstructorInjection(Boolean[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Boolean[].class);
    }

    public WrapperArrayConstructorInjection(Byte[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Byte[].class);
    }

    public WrapperArrayConstructorInjection(Character[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Character[].class);
    }

    public WrapperArrayConstructorInjection(Short[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Short[].class);
    }

    public WrapperArrayConstructorInjection(Long[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Long[].class);
    }

    public WrapperArrayConstructorInjection(Double[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Double[].class);
    }

    public WrapperArrayConstructorInjection(Float[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Float[].class);
    }
}
