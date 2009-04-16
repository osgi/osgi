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

package org.osgi.test.cases.blueprint.tests;

import org.osgi.test.cases.blueprint.framework.CompoundPropertyValidator;
import org.osgi.test.cases.blueprint.framework.MetadataEventSet;
import org.osgi.test.cases.blueprint.framework.StandardTestController;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class TestCompoundPropertyInjection  extends DefaultTestBundleControl {
    public void testCompoundProperty() throws Exception {
        StandardTestController controller = new StandardTestController(getContext(), getWebServer()
                + "www/compound_property_injection.jar");
        MetadataEventSet startEvents = controller.getStartEvents();

        startEvents.addValidator(new CompoundPropertyValidator("compoundProperty", "fred.bob.sammy", "sammy", new String("abc"), String.class));

        controller.run();
    }
}
