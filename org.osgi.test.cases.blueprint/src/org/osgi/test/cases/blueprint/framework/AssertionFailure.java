/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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

