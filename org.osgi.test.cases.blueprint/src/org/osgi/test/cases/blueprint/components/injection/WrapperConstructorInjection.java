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
 * Test component for injection of wrapper values into
 * a component constructor.
 */
public class WrapperConstructorInjection extends BaseTestComponent {
    public WrapperConstructorInjection(Boolean arg1) {
        super("BooleanArg");
        // need to set this explicitly for the null tests
        setArgumentValue("arg1", arg1, Boolean.class);
    }

    public WrapperConstructorInjection(Byte arg1) {
        super("ByteArg");
        setArgumentValue("arg1", arg1, Byte.class);
    }

    public WrapperConstructorInjection(Character arg1) {
        super("CharacterArg");
        setArgumentValue("arg1", arg1, Character.class);
    }

    public WrapperConstructorInjection(Double arg1) {
        super("DoubleArg");
        setArgumentValue("arg1", arg1, Double.class);
    }

    public WrapperConstructorInjection(Float arg1) {
        super("FloatArg");
        setArgumentValue("arg1", arg1, Float.class);
    }

    public WrapperConstructorInjection(Integer arg1) {
        super("IntegerArg");
        setArgumentValue("arg1", arg1, Integer.class);
    }

    public WrapperConstructorInjection(Long arg1) {
        super("LongArg");
        setArgumentValue("arg1", arg1, Long.class);
    }

    public WrapperConstructorInjection(Short arg1) {
        super("ShortArg");
        setArgumentValue("arg1", arg1, Short.class);
    }
}
