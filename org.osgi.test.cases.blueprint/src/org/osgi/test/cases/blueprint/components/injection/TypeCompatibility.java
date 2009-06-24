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

import java.util.Set;
import java.util.SortedSet;
import java.util.Properties;
import java.util.SortedMap;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Small test class for testing constructor type compatibility
 * rules for constructor disambiguation.  This will have
 * constructor methods, static factory methods, and component
 * factory methods with an assortment of signatures to
 * test the disambiguation rules.  Many of these rules will
 * require type conversion in order to derive the correct signature,
 */
public class TypeCompatibility extends BaseTestComponent {
    /**
     * Default constructor for using this as an instance factory.
     */
    public TypeCompatibility() {
        super("typeCompatibility");
    }


    /**
     * This will test that there is an ambiguity between primitive and
     * wrappered versions of the same type.  This will be used for both
     * error tests and to verify that the type overrides will work to
     * select the correct target.
     *
     * @param arg1
     */
    public TypeCompatibility(boolean arg1) {
        super("booleanArg");
        setArgumentValue("arg1", arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    public TypeCompatibility(Boolean arg1) {
        super("BooleanArg");
        setArgumentValue("arg1", arg1, Boolean.class);
    }


    /**
     * This will test that there is an ambiguity between builtin collection
     * class conversions.
     *
     * @param arg1
     */
    public TypeCompatibility(Set arg1) {
        super("SetArg");
        setArgumentValue("arg1", arg1, Set.class);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    public TypeCompatibility(SortedSet arg1) {
        super("SortedSetArg");
        setArgumentValue("arg1", arg1, SortedSet.class);
    }


    /**
     * This will test that there is an ambiguity between builtin collection
     * class conversions.
     *
     * @param arg1
     */
    public TypeCompatibility(Properties arg1) {
        super("PropertiesArg");
        setArgumentValue("arg1", arg1, Properties.class);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    public TypeCompatibility(SortedMap arg1) {
        super("SortedMapArg");
        setArgumentValue("arg1", arg1, SortedMap.class);
    }


    /**
     * This will test that there is an ambiguity between primitive and
     * wrappered versions of the same type.  This will be used for both
     * error tests and to verify that the type overrides will work to
     * select the correct target.
     *
     * @param arg1
     */
    public TypeCompatibility makeInstance(boolean arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    public TypeCompatibility makeInstance(Boolean arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * This will test that there is an ambiguity between builtin collection
     * class conversions.
     *
     * @param arg1
     */
    public TypeCompatibility makeInstance(Set arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    public TypeCompatibility makeInstance(SortedSet arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * This will test that there is an ambiguity between builtin collection
     * class conversions.
     *
     * @param arg1
     */
    public TypeCompatibility makeInstance(Properties arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    public TypeCompatibility makeInstance(SortedMap arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * This will test that there is an ambiguity between primitive and
     * wrappered versions of the same type.  This will be used for both
     * error tests and to verify that the type overrides will work to
     * select the correct target.
     *
     * @param arg1
     */
    static public TypeCompatibility makeStatic(boolean arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    static public TypeCompatibility makeStatic(Boolean arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * This will test that there is an ambiguity between builtin collection
     * class conversions.
     *
     * @param arg1
     */
    static public TypeCompatibility makeStatic(Set arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    static public TypeCompatibility makeStatic(SortedSet arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * This will test that there is an ambiguity between builtin collection
     * class conversions.
     *
     * @param arg1
     */
    static public TypeCompatibility makeStatic(Properties arg1) {
        return new TypeCompatibility(arg1);
    }


    /**
     * The wrappered version to create some ambiguity.
     *
     * @param arg1
     */
    static public TypeCompatibility makeStatic(SortedMap arg1) {
        return new TypeCompatibility(arg1);
    }
}
