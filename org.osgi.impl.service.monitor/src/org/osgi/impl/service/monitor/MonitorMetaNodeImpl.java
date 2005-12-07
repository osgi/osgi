/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.monitor;

import java.util.Arrays;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.DmtData;

// TODO update meta-info based on spec (e.g. wrt permanent/automatic nodes)

public class MonitorMetaNodeImpl implements MetaNode
{
    static final String LEAF_MIME_TYPE = "text/plain";
    
    boolean   deletable             = false;
    boolean   addable               = false;
    boolean   retrievable           = true;
    boolean   replaceable           = true;
    boolean   executable            = false;

    boolean   leaf;             // there is no meaningful default
    int       scope                 = PERMANENT;

    String    description           = null;

    int       maxOccurrence         = 1;
    boolean   zeroOccurrenceAllowed = false;

    DmtData   defaultData           = null;
    double    max                   = Double.MAX_VALUE;
    double    min                   = Double.MIN_VALUE;
    String[]  validNames            = null;
    DmtData[] validValues           = null;
    int       format                = DmtData.FORMAT_NULL;
    String[]  mimeTypes             = null;

    // Leaf node in MonitorPlugin
    public MonitorMetaNodeImpl(String description, boolean replaceable, 
                               boolean isPermanent, DmtData defaultData, 
                               DmtData[] validValues, int format)
    {
        leaf = true;
        // No leaf nodes can be created (they are either permanent or automatic)
        scope = isPermanent ? PERMANENT : AUTOMATIC;
        mimeTypes = new String[] { LEAF_MIME_TYPE };

        this.replaceable = replaceable;
        this.defaultData = defaultData;
        this.validValues = validValues;
        this.format      = format;

        setCommon(description, false);
    }

    // Interior node in ConfigurationPlugin
    public MonitorMetaNodeImpl(String description, boolean deletable, 
                               boolean addable, boolean allowInfinte, 
                               int scope)
    {
        // TODO merge deletable and addable parameter into one (they are always the same at the time of reading)
        leaf = false;
        format = DmtData.FORMAT_NODE;

        this.scope = scope;
        this.deletable = deletable;
        this.addable = addable;

        setCommon(description, allowInfinte);        
    }

    public boolean can(int operation) {
        switch(operation) {
        case CMD_DELETE:  return deletable;
        case CMD_ADD:     return addable;
        case CMD_GET:     return retrievable;
        case CMD_REPLACE: return replaceable;
        case CMD_EXECUTE: return executable;
        }
        return false;
    }

    public boolean isLeaf()
    {
        return leaf;
    }

    public int getScope()
    {
        return scope;
    }

    public String getDescription()
    {
        return description;
    }

    public int getMaxOccurrence()
    {
        return maxOccurrence;
    }

    public boolean isZeroOccurrenceAllowed()
    {
        return zeroOccurrenceAllowed;
    }

    public DmtData getDefault()
    {
        return defaultData;
    }

    public double getMax()
    {
        return max;
    }

    public double getMin()
    {
        return min;
    }

    public String[] getValidNames() {
        return validNames;
    }
    
    public DmtData[] getValidValues()
    {
        return validValues;
    }

    public int getFormat()
    {
        return format;
    }

    public String [] getMimeTypes()
    {
        return mimeTypes;
    }

    public boolean isValidName(String name) {
        return validNames == null ? true :
            Arrays.asList(validNames).contains(name);
    }
    
    public boolean isValidValue(DmtData value) {
        int valueFormat = value.getFormat();
        if((valueFormat & format) == 0)
            return false;
        
        if(valueFormat == DmtData.FORMAT_INTEGER) {
            int intValue = value.getInt();
            if(intValue < min || intValue > max)
                return false;
        }
            
        return validValues == null ? true :
            Arrays.asList(validValues).contains(value);
    }
    
    private void setCommon(String description, boolean allowInfinte)
    {
        this.description = description;

        if(allowInfinte) {
            maxOccurrence = Integer.MAX_VALUE; // infinite
            zeroOccurrenceAllowed = true;
            deletable = true;
        }
    }
}
