
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
 * Test component for injection of primitive array values into
 * a component constructor.
 */
public class PrimitiveArrayConstructorInjection extends BaseTestComponent {
    public PrimitiveArrayConstructorInjection(int[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, int[].class);
    }

    public PrimitiveArrayConstructorInjection(boolean[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, boolean[].class);
    }

    public PrimitiveArrayConstructorInjection(byte[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, byte[].class);
    }

    public PrimitiveArrayConstructorInjection(char[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, char[].class);
    }

    public PrimitiveArrayConstructorInjection(short[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, short[].class);
    }

    public PrimitiveArrayConstructorInjection(long[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, long[].class);
    }

    public PrimitiveArrayConstructorInjection(double[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, double[].class);
    }

    public PrimitiveArrayConstructorInjection(float[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, float[].class);
    }
}
