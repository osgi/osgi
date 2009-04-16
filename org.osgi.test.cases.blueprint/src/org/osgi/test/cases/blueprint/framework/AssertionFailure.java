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
import junit.framework.AssertionFailedError;

import org.osgi.test.cases.blueprint.services.AssertionService;

public class AssertionFailure extends TestEvent {
    // the message associated with the event
    protected String message;
    // a possible attached cause
    protected Throwable cause;

    /**
     * Create a failure assertion on the event receiving side for
     * to raise an assertion failure.
     */
    public AssertionFailure(String message) {
        this(message, null);
    }

    /**
     * Create a failure assertion on the event receiving side for
     * to raise an assertion failure.
     */
    public AssertionFailure(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    /**
     * Raise a deferred assertion failure.
     */
    public void fail() {
        AssertionFailedError exception = new AssertionFailedError(this.toString());
        if (cause != null) {
            exception.initCause(exception);
        }
        throw exception;
    }

    /**
     * Raise an assertion failure for an extra assertion event
     */
    public void failUnexpected() {
        AssertionFailedError exception = new AssertionFailedError(this.toString());
        if (cause != null) {
            exception.initCause(cause);
        }
        throw exception;
    }


    /**
     * Flag an event as being an error situation that will terminate
     * the test if received when not expected.
     *
     * @return Always returns true for the assertions, since unexpected ones might
     * indicate a problem.
     */
    public boolean isError() {
        return true;
    }

    /**
     * Retrieve the type of the assertion.
     */
    public String getType() {
        return AssertionService.ASSERTION_FAILURE;
    }

    public String toString() {
        return "AssertionFailure: " + message;
    }
}

