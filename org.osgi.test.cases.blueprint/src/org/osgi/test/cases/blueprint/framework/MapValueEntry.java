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

import java.util.Map;

import org.osgi.service.blueprint.reflect.Value;

/**
 * A descriptor for an index/value pair we expect to see in
 * component metadata.  This is the metadata descriptors for the
 * map entries, not the values themselves.
 */
public class MapValueEntry {
    // the entry key
    protected TestValue key;
    // the mapped value
    protected TestValue value;

    public MapValueEntry(TestValue key, TestValue value) {
        this.key = key;
        this.value = value;
    }

    public MapValueEntry(String key, String value) {
        this(new TestStringValue(null, key), new TestStringValue(null, value));
    }

    public MapValueEntry(String key, String value, Class type) {
        this(new TestStringValue(null, key), new TestStringValue(type, value));
    }

    public MapValueEntry(String key, TestValue value) {
        this(new TestStringValue(null, key), value);
    }

    public MapValueEntry(TestValue key, String value) {
        this(key, new TestStringValue(null, value));
    }

    public MapValueEntry(TestValue key, String value, Class type) {
        this(key, new TestStringValue(type, value));
    }

    public boolean equals(Map.Entry entry) {
        Value entryKey = (Value)entry.getKey();
        return key.equals(entryKey);
    }

    public void validate(ModuleMetadata moduleMetadata, Map.Entry entry) throws Exception {
        key.validate(moduleMetadata, (Value)entry.getKey());
        value.validate(moduleMetadata, (Value)entry.getValue());
    }

    public String toString() {
        return "MapEntry key: " + key + " value: " + value;
    }
}

