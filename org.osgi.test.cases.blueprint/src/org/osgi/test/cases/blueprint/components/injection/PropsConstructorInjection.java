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

import java.util.Properties;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Class for testing different types of injection using
 * <props> objects.  This is done using a separate
 * class to avoid assignability ambiguities with the
 * Map interface, which Properties also implements.
 *
 * This also handles the instance and static factory
 * construction scenarios.
 */
public class PropsConstructorInjection extends BaseTestComponent {

    /**
     * Simple no-arg injection
     */
    public PropsConstructorInjection() {
        super();
    }

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *            The component identifier used for test verification purposes.
     */
    public PropsConstructorInjection(String arg1) {
        super(arg1);
    }

    public PropsConstructorInjection(Properties arg1) {
        super("PropertiesArg");
        setArgumentValue("arg1", arg1, Properties.class);
    }

    public PropsConstructorInjection(Properties[] arg1) {
        super();
        this.setArgumentValue("arg1", arg1, Properties[].class);
    }


    public PropsConstructorInjection makeInstance(Properties arg1) {
        return new PropsConstructorInjection(arg1);
    }


    static public PropsConstructorInjection makeStatic(Properties arg1) {
        return new PropsConstructorInjection(arg1);
    }


    public PropsConstructorInjection makeInstance(Properties[] arg1) {
        return new PropsConstructorInjection(arg1);
    }


    static public PropsConstructorInjection makeStatic(Properties[] arg1) {
        return new PropsConstructorInjection(arg1);
    }
}
