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

package org.osgi.test.cases.blueprint.components.injection;

import java.util.Set;

import org.osgi.test.cases.blueprint.services.AssertionService;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

/**
 * Test component used to validate singleton/prototype component
 * semantics.  This component will be injected with a Set created
 * using component references.  This Set will be constructed using
 * references to either singleton or prototype components.  At
 * injection time, the set will be validated against an expected
 * number of components in the set.
 */
public class ScopeComponent extends BaseTestComponent {
    // the number of elements we expect in the Set
    protected int expectedCount;

    /**
     * Create a component that is expecting the indicated number of
     * elements in an injected set property.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     * @param expectedCount
     *               The size of the expected set.
     */
    public ScopeComponent(String componentId, int expectedCount) {
        super(componentId);
        this.expectedCount = expectedCount;
    }


    /**
     * References property used to validate singleton/prototype
     * reference sets.
     *
     * @param refs   The injected Set of references.
     */
    public void setReferences(Set refs) {
        AssertionService.assertEquals(this, "Unexpected component Set size", expectedCount, refs.size());
    }
}

