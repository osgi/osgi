package org.osgi.service.dmt;

/**
 * A collection of constants describing the possible formats of a DMT node. 
 */
public interface DmtDataType {
    
    /**
     * The node holds an integer value. Note that this does not correspond to
     * the Java <code>int</code> type, OMA DM integers are unsigned.  
     */
    static final int INTEGER = 1;
    
    /**
     * The node holds an OMA DM <code>chr</code> value. 
     */
    static final int STRING  = 2;
    
    /**
     * The node holds an OMA DM <code>bool</code> value. 
     */
    static final int BOOLEAN = 3;
    
    /**
     * The node holds an OMA DM <code>binary</code> value. The value of the 
     * node corresponds to the Java <code>byte[]</code> type.  
     */
    static final int BINARY  = 4;
    
    /**
     * The node holds an OMA DM <code>xml</code> value. 
     */
    static final int XML     = 5;
    
    /**
     * The node holds an OMA DM <code>null</code> value. This corresponds to
     * the Java <code>null</code> type. 
     */
    static final int NULL    = 6;
    
    /**
     * The node is an internal node. 
     */
    static final int NODE    = 7;
}
