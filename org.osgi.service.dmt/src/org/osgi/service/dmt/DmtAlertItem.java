package org.osgi.service.dmt;

/**
 * Data structure carried in an alert (client initiated notification). 
 * The DmtAlertItem describes details of various alerts that can be sent by the
 * client of the OMA DM protocol. The use cases include the client sending a 
 * session request to the server (alert 1201), the client notifying the server 
 * of completion of a software update operation (alert 1226) or sending back 
 * results in response to an asynchronous EXEC command. <p>
 * The data syntax and semantics varies widely between various alerts, so does 
 * the optionality of particular parameters of an alert item. If an item, such 
 * as source or type, is not defined, the corresponding getter method 
 * returns <code>null</code>. For example, for alert 1201 (client-initiated 
 * session) all elements will be <code>null</code>.
 */
public interface DmtAlertItem {
    
    /**
     * Get the node which is the source of the alert. There might be
     * no source associated with the alert item.
     * @return The URI of the node which is the source of this alert.
     * Can be <code>null</code>.
     */
    String getSource();
    
    /**
     * Get the type associated with the alert item. There might be
     * no format associated with the alert item.
     * @return The type type associated with the alert item.
     * Can be <code>null</code>.
     */
    String getType();
    
    /**
     * Get the format associated with the alert item. There might be
     * no format associated with the alert item.
     * @return The format associated with the alert item.
     * Can be <code>null</code>.
     */
    String getFormat();
    
    /**
     * Get the markup data associated with the alert item. There might be
     * no markup associated with the alert item. 
     * @return The markup data associated with the alert item, in the format
     * defined by OMA DM specifications.
     * Can be <code>null</code>.
     */
    String getMarkUp();
    
    /**
     * Get the data associated with the alert item. There might be
     * no data associated with the alert item.
     * @return The data associated with the alert item.
     * Can be <code>null</code>.
     */
    String getData();
}
