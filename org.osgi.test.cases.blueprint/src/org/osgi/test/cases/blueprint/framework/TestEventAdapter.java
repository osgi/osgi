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

public class TestEventAdapter implements TestEventListener {
    /**
     * Give the listener an opportunity to respond to receiving
     * an expected event. This is intended for triggering actions resulting
     * from receiving an event.  For example, a waiting event can register a
     * service upon notification that a WAITING event for that service has been
     * received.
     *
     * @param results  The test case expected result set.
     * @param expected The expected event.
     * @param received The actual received event, with all attached event properties.
     */
    public void eventReceived(TestEvent expected, TestEvent received) {
        // No action by default
    }

    /**
     * Give the listener an opportunity to respond to not receiving
     * an expected event.
     *
     * @param results  The test case expected result set.
     * @param expected The expected event.
     */
    public void eventNotReceived(TestEvent expected) throws Exception {
        // No action by default
    }

    /**
     * Give the listener to perform test validation at the point where
     * an event is returned.  If the listener returns null, then
     * the validation has passed.  If the listener returns a TestEvent
     * instance, the returned event will be added to the failures list
     * and will be used to raise a deferred assertion in the main thread.
     *
     * @param expected The expected exception (the one the listener is attached to).
     * @param received The received exception requiring validation.
     *
     * @return null, or a TestEvent instance used to raise a deferred assertion failure.
     */
    public TestEvent validateEvent(TestEvent expected, TestEvent received) {
        return null;
    }
}

