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

package org.osgi.test.cases.blueprint.framework;

import java.util.List;

public class TestListValue extends TestCollectionValue {

    /**
     * A TestListValue that only verifies that the value
     * is a list type.  No entry validation is done.
     */
    public TestListValue() {
        this(null, null);
    }

    public TestListValue(TestValue[] entries) {
        this(entries, null);
    }

    public TestListValue(TestValue[] entries, Class listType) {
        super(List.class, entries, listType);
    }
}
