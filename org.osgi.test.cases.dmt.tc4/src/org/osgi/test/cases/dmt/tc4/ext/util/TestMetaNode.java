package org.osgi.test.cases.dmt.tc4.ext.util;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;

public class TestMetaNode implements MetaNode {

    private final boolean isLeaf;

    public TestMetaNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public int getScope() {
        return MetaNode.DYNAMIC;
    }

    public boolean can(int operation) {
        return true;
    }

    public String getDescription() {
        return null;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public boolean isZeroOccurrenceAllowed() {
        return true;
    }

    public int getMaxOccurrence() {
        return 0;
    }

    public String[] getRawFormatNames() {
        return null;
    }

    public boolean isValidName(String name) {
        return true;
    }

    public String[] getValidNames() {
        return null;
    }

    public int getFormat() {
        return 0;
    }

    public String[] getMimeTypes() {
        return null;
    }

    public DmtData getDefault() {
        return null;
    }

    public double getMin() {
        return 0;
    }

    public double getMax() {
        return 0;
    }

    public boolean isValidValue(DmtData value) {
        return true;
    }

    public DmtData[] getValidValues() {
        return null;
    }

    public String[] getExtensionPropertyKeys() {
        return null;
    }

    public Object getExtensionProperty(String key) {
        return null;
    }
}
