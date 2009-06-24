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
 * Small test class for testing constructor type assignability
 * rules for constructor disambiguation.  This will have
 * constructor methods, static factory methods, and component
 * factory methods with an assortment of signatures to
 * test the disambiguation rules.  Assignability does not
 * invoke any of the type conversion rules except for primitive
 * boxing/unboxing rules
 */
public class TypeAssignability extends BaseTestComponent {
    /**
     * Default constructor for using this as an instance factory.
     */
    public TypeAssignability() {
        super("typeCompatibility");
    }


    /**
     * A lot of the disambiguation rules involve string arguments,
     * so we need one of these.
     *
     * @param arg1
     */
    public TypeAssignability(String arg1) {
        super("StringArg");
        setArgumentValue("arg1", arg1);
    }


    /**
     * Boxing/unboxing behavior is specifically tested elsewhere, but the
     * type compatibility rules invoke boxing/unboxing behavior to make
     * matchups.  We'll have one primitive type constructor, and a
     * wrappered version to test the different combinations to ensure
     * the correct one is selected.  We need to use different primitive
     * types to avoid ambiguity, since both the primitive and the
     * wrappered version will satisfy the assignability test, thus
     * making them ambiguous.
     *
     * @param arg1
     */
    public TypeAssignability(int arg1) {
        super("intArg");
        setArgumentValue("arg1", arg1);
    }


    /**
     * Boxing/unboxing behavior is specifically tested elsewhere, but the
     * type compatibility rules invoke boxing/unboxing behavior to make
     * matchups.  We'll have one primitive type constructor, and a
     * wrappered version to test the different combinations to ensure
     * the correct one is selected.
     *
     * @param arg1
     */
    public TypeAssignability(Double arg1) {
        super("DoubleArg");
        setArgumentValue("arg1", arg1, Double.class);
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
    public TypeAssignability(RegionCode arg1) {
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
    public TypeAssignability(AsianRegionCode arg1) {
        super("AsianRegionCodeArg");
        setArgumentValue("arg1", arg1, AsianRegionCode.class);
    }


    /**
     * This will test that there is an ambiguity between primitive and
     * wrappered versions of the same type.  This will be used for both
     * error tests and to verify that the type overrides will work to
     * select the correct target.
     *
     * @param arg1
     */
    public TypeAssignability(boolean arg1) {
        super("booleanArg");
        setArgumentValue("arg1", arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    public TypeAssignability(Boolean arg1) {
        super("BooleanArg");
        setArgumentValue("arg1", arg1, Boolean.class);
    }


    /**
     * A lot of the disambiguation rules involve string arguments,
     * so we need one of these.
     *
     * @param arg1
     */
    public TypeAssignability makeInstance(String arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * Boxing/unboxing behavior is specifically tested elsewhere, but the
     * type compatibility rules invoke boxing/unboxing behavior to make
     * matchups.  We'll have one primitive type constructor, and a
     * wrappered version to test the different combinations to ensure
     * the correct one is selected.  We need to use different primitive
     * types to avoid ambiguity, since both the primitive and the
     * wrappered version will satisfy the assignability test, thus
     * making them ambiguous.
     *
     * @param arg1
     */
    public TypeAssignability makeInstance(int arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * Boxing/unboxing behavior is specifically tested elsewhere, but the
     * type compatibility rules invoke boxing/unboxing behavior to make
     * matchups.  We'll have one primitive type constructor, and a
     * wrappered version to test the different combinations to ensure
     * the correct one is selected.
     *
     * @param arg1
     */
    public TypeAssignability makeInstance(Double arg1) {
        return new TypeAssignability(arg1);
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
    public TypeAssignability makeInstance(RegionCode arg1) {
        return new TypeAssignability(arg1);
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
    public TypeAssignability makeInstance(AsianRegionCode arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * This will test that there is an ambiguity between primitive and
     * wrappered versions of the same type.  This will be used for both
     * error tests and to verify that the type overrides will work to
     * select the correct target.
     *
     * @param arg1
     */
    public TypeAssignability makeInstance(boolean arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    public TypeAssignability makeInstance(Boolean arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * A lot of the disambiguation rules involve string arguments,
     * so we need one of these.
     *
     * @param arg1
     */
    static public TypeAssignability makeStatic(String arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * Boxing/unboxing behavior is specifically tested elsewhere, but the
     * type compatibility rules invoke boxing/unboxing behavior to make
     * matchups.  We'll have one primitive type constructor, and a
     * wrappered version to test the different combinations to ensure
     * the correct one is selected.  We need to use different primitive
     * types to avoid ambiguity, since both the primitive and the
     * wrappered version will satisfy the assignability test, thus
     * making them ambiguous.
     *
     * @param arg1
     */
    static public TypeAssignability makeStatic(int arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * Boxing/unboxing behavior is specifically tested elsewhere, but the
     * type compatibility rules invoke boxing/unboxing behavior to make
     * matchups.  We'll have one primitive type constructor, and a
     * wrappered version to test the different combinations to ensure
     * the correct one is selected.
     *
     * @param arg1
     */
    static public TypeAssignability makeStatic(Double arg1) {
        return new TypeAssignability(arg1);
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
    static public TypeAssignability makeStatic(RegionCode arg1) {
        return new TypeAssignability(arg1);
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
    static public TypeAssignability makeStatic(AsianRegionCode arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * This will test that there is an ambiguity between primitive and
     * wrappered versions of the same type.  This will be used for both
     * error tests and to verify that the type overrides will work to
     * select the correct target.
     *
     * @param arg1
     */
    static public TypeAssignability makeStatic(boolean arg1) {
        return new TypeAssignability(arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    static public TypeAssignability makeStatic(Boolean arg1) {
        return new TypeAssignability(arg1);
    }
}
