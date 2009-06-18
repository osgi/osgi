
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
 * Test component for injection of primitive values into
 * a component constructor.
 */
public class PrimitiveConstructorInjection extends BaseTestComponent {
    public PrimitiveConstructorInjection(boolean arg1) {
        super("booleanArg");
        setArgumentValue("arg1", arg1);
    }

    public PrimitiveConstructorInjection(byte arg1) {
        super("byteArg");
        setArgumentValue("arg1", arg1);
    }

    public PrimitiveConstructorInjection(char arg1) {
        super("charArg");
        setArgumentValue("arg1", arg1);
    }

    public PrimitiveConstructorInjection(double arg1) {
        super("doubleArg");
        setArgumentValue("arg1", arg1);
    }

    public PrimitiveConstructorInjection(float arg1) {
        super("floatArg");
        setArgumentValue("arg1", arg1);
    }

    public PrimitiveConstructorInjection(int arg1) {
        super("intArg");
        setArgumentValue("arg1", arg1);
    }

    public PrimitiveConstructorInjection(long arg1) {
        super("longArg");
        setArgumentValue("arg1", arg1);
    }

    public PrimitiveConstructorInjection(short arg1) {
        super("shortArg");
        setArgumentValue("arg1", arg1);
    }
}
