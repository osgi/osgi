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

import java.util.List;

import org.osgi.service.blueprint.reflect.ListValue;
import org.osgi.service.blueprint.reflect.Value;

public class TestListValue extends TestValue {
    // The expected set of items in the List.
    protected TestValue[] entries;
    // the default element type for the list
    protected String typeName;

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
        super(ListValue.class);
        this.entries = entries;
        if (listType != null) {
            typeName = listType.getName();
        }
    }


    /**
     * An expected ListValue item against a received one.
     *
     * @param moduleMetadata
     *               The metadata source for the validation.
     * @param v
     *
     * @exception Exception
     */
    public void validate(ModuleMetadata moduleMetadata, Value v) throws Exception {
        // This validates the metadata type.
        super.validate(moduleMetadata, v);
        // we might not have a validation list if we're just interested
        // in verifying this is a ListValue
        if (entries == null) {
            return;
        }

        List list = ((ListValue)v).getList();
        // validate the size first
        assertEquals("List value size mismatch", entries.length, list.size());
        assertEquals("List default type mismatch", typeName, ((ListValue)v).getValueType());
        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            // nulls appear as NullValue items, so everything should match
            entries[i].validate(moduleMetadata, (Value)list.get(i));
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
        List list = ((ListValue)v).getList();
        // not the one we need
        if (entries.length != list.size()) {
            return false;
        }

        // now validate each of the entries
        for (int i = 0; i < entries.length; i++) {
            // validate the real entry
            if (!entries[i].equals((Value)list.get(i))) {
                return false;
            }
        }
        return true;
    }
}

