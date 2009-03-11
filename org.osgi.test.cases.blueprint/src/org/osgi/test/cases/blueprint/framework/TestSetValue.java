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

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.osgi.service.blueprint.reflect.SetValue;
import org.osgi.service.blueprint.reflect.Value;

public class TestSetValue extends TestValue {
    // The expected set of items in the List.
    protected TestValue[] entries;
    // the default element type for the list
    protected String typeName;

    public TestSetValue() {
        this(null, null);
    }

    public TestSetValue(TestValue[] entries) {
        this(entries, null);
    }

    public TestSetValue(TestValue[] entries, Class listType) {
        super(SetValue.class);
        this.entries = entries;
        if (listType != null) {
            typeName = listType.getName();
        }
    }


    /**
     * Validate a ParameterSpecification against an expected value.
     *
     * @param spec   The metadata spec for this argument.
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, Value v) throws Exception {
        super.validate(moduleMetadata, v);
        // we might not have a validation list
        if (entries == null) {
            return;
        }
        Set set = ((SetValue)v).getSet();
        // validate the size first
        assertEquals("Set value size mismatch", entries.length, set.size());
        assertEquals("Set default type mismatch", typeName, ((SetValue)v).getValueType());
        // we work off of a copy of this
        Set working = new HashSet(set);
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            Value target = locateEntry(working, entries[i]);
            assertNotNull("Target value not found in set item: " + entries[i], target);
            // validate the real entry
            entries[i].validate(moduleMetadata, target);
        }
    }

    /**
     * do a comparison between a real metadata item and our test validator.
     * This is used primarily to locate specific values in the different
     * CollectionValues.
     *
     * @param v      The target value item.
     *
     * @return True if this can be considered a match, false for any mismatch.
     */
    public boolean equals(Value v) {
        // must be of matching type
        if (!super.equals(v)) {
            return false;
        }

        // we might not have a validation list, so use this
        // as a wildcard placeholder if asked.
        if (entries == null) {
            return true;
        }

        Set set = ((SetValue)v).getSet();
        // not the one we need
        if (entries.length != set.size()) {
            return false;
        }

        // we work off of a copy of this
        Set working = new HashSet(set);
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            Value target = locateEntry(working, entries[i]);
            if (target == null) {
                return false;
            }
            // validate the real entry
            if (!entries[i].equals(target)) {
                return false;
            }
        }
        return true;
    }


    protected Value locateEntry(Set values, TestValue target) {
        Iterator i = values.iterator();
        while (i.hasNext()) {
            Value item = (Value)i.next();
            if (target.equals(item)) {
                i.remove();
                return item;
            }
        }
        return null;
    }
}

