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

package org.osgi.test.cases.blueprint.services;

import java.util.Map;

import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;

import org.osgi.test.cases.blueprint.components.injection.AsianRegionCode;
import org.osgi.test.cases.blueprint.components.injection.EuropeanRegionCode;

/**
 * A class designed to test all sorts of bundle callback situations.  This class will
 * reside in the main test jar, which runs with AllPermissions.  This will test all aspects
 * of the callback types that can be made to code instantiated by the blueprint extender.
 * For each of these callbacks, the Assertion service will be called to publish a blueprint
 * event that the managed bundle does not have authority to publish.  This should cause
 * a security exception if the bundle has been called with the correct access control
 * context.
 */
public class SecurityTestBean extends BaseTestComponent {

    /**
     * The null argument constructor version is used for normal operations where
     * we need to create an instance.  We'll only need one per test since these are
     * all error tests, do different ids are not needed.
     */
    public SecurityTestBean() {
        super("SecurityTestBean");
    }

    /**
     * A single string argument constructor used to trigger
     * security failures on a call back.
     *
     * @param componentId
     *               The component id (mostly used as a trigger).
     */
    public SecurityTestBean(String componentId) {
        super(componentId);
        causeSecurityFailure();
    }


    /**
     * A component factory method that should generate a SecurityException
     * if called with the proper context.
     *
     * @return A SecurityTestBean instance, but this should be a
     *         SecurityException.
     */
    public SecurityTestBean makeInstance() {
        return new SecurityTestBean("SecurityTestBean");
    }


    /**
     * A static factory method that should generate a SecurityException
     * if called with the proper context.
     *
     * @return A SecurityTestBean instance, but this should be a
     *         SecurityException.
     */
    static public SecurityTestBean makeStatic() {
        return new SecurityTestBean("SecurityTestBean");
    }


    /**
     * A property setter that should trigger a security exception.
     *
     * @param value
     */
    public void setProperty(String value) {
        causeSecurityFailure();
    }

    // init and destroy methods to trigger security exceptions.
    public void init() {
        causeSecurityFailure();
    }

    public void destroy() {
        causeSecurityFailure();
    }

    // service reference listener methods that should generate security exceptions.
    public void bind(TestServiceOne service, Map serviceProperties) {
        causeSecurityFailure();
    }

    public void unbind(TestServiceOne service, Map serviceProperties) {
        causeSecurityFailure();
    }

    // registration listener methods to trigger exceptions
    public void registered(TestServiceOne service, Map serviceProperties) {
        causeSecurityFailure();
    }

    public void unregistered(TestServiceOne service, Map serviceProperties) {
        causeSecurityFailure();
    }

    // type converter methods to trigger security exceptions
    public Object convert(Object source, ReifiedType toType) throws Exception {
        try {
            causeSecurityFailure();
        } catch (SecurityException e) {
            // if the security exception did not occur, an ASSERTION_FAILURE
            // has been sent.  We'll just swallow the exception when it does occur
            // and continue with the conversion step.
        }
        return new EuropeanRegionCode("UK+86");
    }

    public boolean canConvert(Object value, ReifiedType toType) {
        Class toClass = (Class)toType.getRawClass();
        // if this is the AsianRegionCode, then cause a failure now
        if (toClass == AsianRegionCode.class) {
            try {
                causeSecurityFailure();
            } catch (SecurityException e) {
                // if the security exception did not occur, an ASSERTION_FAILURE
                // has been sent.  We'll just swallow the exception when it does occur
                // and say we can't convert this
            }
            return false;
        }
        else {
            // say we can handle the EuropeanRegionCode so we can get to the convert() method.
            return toClass == EuropeanRegionCode.class;
        }
    }


    /**
     * Cause a SecurityException from a call back.
     */
    protected void causeSecurityFailure() {
        AssertionService.sendSecurityEvent(this);
        // we should never get here...the sendSecurityEvent should
        // cause a SecurityException.
        AssertionService.fail(this, "SecurityException not raised");
    }
}

