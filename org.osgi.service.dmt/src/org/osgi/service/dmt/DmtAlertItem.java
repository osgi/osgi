/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.dmt;

/**
 * Data structure carried in an alert (client initiated notification). The
 * <code>DmtAlertItem</code> describes details of various alerts that can be
 * sent by the client of the OMA DM protocol. The use cases include the client
 * sending a session request to the server (alert 1201), the client notifying
 * the server of completion of a software update operation (alert 1226) or
 * sending back results in response to an asynchronous EXEC command.
 * <p>
 * The data syntax and semantics varies widely between various alerts, so does
 * the optionality of particular parameters of an alert item. If an item, such
 * as source or type, is not defined, the corresponding getter method returns
 * <code>null</code>. For example, for alert 1201 (client-initiated session)
 * all elements will be <code>null</code>.
 */
public class DmtAlertItem {

    private String source;
    private String type;
    private String format;
    private String data;

    /**
     * Create an instance of the alert item. The constructor takes all possible
     * data entries as parameters. Any of these parameters can be
     * <code>null</code>.
     * 
     * @param source The URI of the node which is the source of the alert item
     * @param type The type of the alert item
     * ### Needs to define where the possible values come from
     * @param format The format of the alert item
     * @param data The data of the alert item
     */
    public DmtAlertItem(String source, String type, String format,
                        String data) {
        this.source = source;
        this.type   = type;
        this.format = format;
        this.data   = data;
    }


    /**
     * Get the node which is the source of the alert. There might be no source
     * associated with the alert item.
     * 
     * @return The URI of the node which is the source of this alert, or
     *         <code>null</code> if there is no source
     */
    public String getSource() {
        return source;
    }

    /**
     * Get the type associated with the alert item. There might be no format
     * associated with the alert item.
     * 
     * @return The type type associated with the alert item, or
     *         <code>null</code> if there is no type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the format associated with the alert item. There might be no format
     * associated with the alert item.
     * 
     * @return The format associated with the alert item, or <code>null</code>
     *         if there is no format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Get the data associated with the alert item. There might be no data
     * associated with the alert item.
     * 
     * @return The data associated with the alert item, or <code>null</code>
     *         if there is no data
     */
    public String getData() {
        return data;
    }

    /**
     * Returns the string representation of this alert item.  The returned
     * includes all parameters of the alert item, and has the following format:
     * <code>DmtAlertItem(&lt;source&gt;, &lt;type&gt;, &lt;format&gt;, &lt;data&gt;)</code>
     * 
     * @return the string representation of this alert item
     */
    public String toString() {
        return
            "DmtAlertItem(\"" + source + "\", \"" + type + "\", \"" +
            format + "\", \"" + data + "\")";
    }
}
