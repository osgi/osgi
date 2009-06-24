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
 * Small test class for testing constructor type compatibility
 * rules for constructor disambiguation.  This will have
 * constructor methods, static factory methods, and component
 * factory methods with an assortment of signatures to
 * test the disambiguation rules.  Many of these rules will
 * require type conversion in order to derive the correct signature,
 */
public class TypeClassCompatibility extends BaseTestComponent {
    /**
     * Default constructor for using this as an instance factory.
     */
    public TypeClassCompatibility() {
        super("typeClassCompatibility");
    }


    /**
     * Class assignability tests are part of the matching rules.  We'll
     * have a version that uses a superclass as well, which will require
     * specifically identifying the target type for some of the tests.  This
     * will also be used in some error tests to verify that the ambiguity is
     * detected.
     *
     * @param arg1
     */
    public TypeClassCompatibility(RegionCode arg1) {
        super("RegionCodeArg");
        setArgumentValue("arg1", arg1, RegionCode.class);
    }


    /**
     * Class assignability tests are part of the matching rules.  We'll
     * have a version that uses a superclass as well, which will require
     * specifically identifying the target type for some of the tests.  This
     * will also be used in some error tests to verify that the ambiguity is
     * detected.
     *
     * @param arg1
     */
    public TypeClassCompatibility(AsianRegionCode arg1) {
        super("AsianRegionCodeArg");
        setArgumentValue("arg1", arg1, AsianRegionCode.class);
    }


    /**
     * Class assignability tests are part of the matching rules.  We'll
     * have a version that uses a superclass as well, which will require
     * specifically identifying the target type for some of the tests.  This
     * will also be used in some error tests to verify that the ambiguity is
     * detected.
     *
     * @param arg1
     */
    public TypeClassCompatibility makeInstance(RegionCode arg1) {
        return new TypeClassCompatibility(arg1);
    }


    /**
     * Class assignability tests are part of the matching rules.  We'll
     * have a version that uses a superclass as well, which will require
     * specifically identifying the target type for some of the tests.  This
     * will also be used in some error tests to verify that the ambiguity is
     * detected.
     *
     * @param arg1
     */
    public TypeClassCompatibility makeInstance(AsianRegionCode arg1) {
        return new TypeClassCompatibility(arg1);
    }


    /**
     * Class assignability tests are part of the matching rules.  We'll
     * have a version that uses a superclass as well, which will require
     * specifically identifying the target type for some of the tests.  This
     * will also be used in some error tests to verify that the ambiguity is
     * detected.
     *
     * @param arg1
     */
    static public TypeClassCompatibility makeStatic(RegionCode arg1) {
        return new TypeClassCompatibility(arg1);
    }


    /**
     * Class assignability tests are part of the matching rules.  We'll
     * have a version that uses a superclass as well, which will require
     * specifically identifying the target type for some of the tests.  This
     * will also be used in some error tests to verify that the ambiguity is
     * detected.
     *
     * @param arg1
     */
    static public TypeClassCompatibility makeStatic(AsianRegionCode arg1) {
        return new TypeClassCompatibility(arg1);
    }
}

