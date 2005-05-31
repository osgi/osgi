package com.nokia.test.db;

import java.io.Serializable;

public class FieldDef implements Serializable {
    
    public static final int STRING =  0;
    public static final int INTEGER = 1;
    
    private String[] strVals = {"STRING", 
                                "INTEGER"};
    
    public Integer type;
    public String  name;
    public Boolean key;
    
    public FieldDef(int type, String name, boolean key) {
        this.type = new Integer(type);
        this.name = name;
        this.key  = new Boolean(key);
    }
    
    public FieldDef(int type, String name) {
        this(type, name, false);
    }
    
    public String toString() {
        return name + " " + strVals[type.intValue()]; 
    }
    
    public static int indexOfKeyField(FieldDef[] fieldDefs) {
        for (int i = 0; i < fieldDefs.length; i++) {
            if (fieldDefs[i].key.booleanValue())
                return i;
        }
        return -1;
    }

}
