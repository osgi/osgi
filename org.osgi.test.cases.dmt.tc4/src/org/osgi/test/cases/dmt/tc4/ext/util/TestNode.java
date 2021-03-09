/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.dmt.tc4.ext.util;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

import java.util.Date;

public class TestNode {

    private MetaNode metaNode;

    private boolean isLeaf;

    private String title;

    private String type;

    private DmtData value;

    private Date date;

    private int version;

    public static TestNode newInteriorNode(String title, String type) {
        TestNode node = new TestNode();
        node.metaNode = new TestMetaNode(false);
        node.isLeaf = false;
        node.title = title;
        node.type = type;
        node.value = null;
        return node;
    }

    public static TestNode newLeafNode(String title, String type, DmtData value) {
        TestNode node = new TestNode();
        node.metaNode = new TestMetaNode(true);
        node.isLeaf = true;
        node.title = title;
        node.type = type;
        node.value = value;
        return node;
    }

    public TestNode() {
    }

    public void setMetaNode(MetaNode metaNode) {
        this.metaNode = metaNode;
    }

    public MetaNode getMetaNode() {
        return metaNode;
    }

    public void setIsLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setValue(DmtData value) {
        this.value = value;
    }

    public DmtData getValue() {
        return value;
    }

    public int getSize() {
        return value.getSize();
    }

    public void setTimestamp(Date date) {
        this.date = date;
    }

    public Date getTimestamp() {
        return date;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
