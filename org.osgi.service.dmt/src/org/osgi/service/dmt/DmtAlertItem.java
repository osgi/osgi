/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.dmt;

import java.text.MessageFormat;

/**
 * Data structure carried in an alert (client initiated notification). The
 * DmtAlertItem describes details of various alerts that can be sent by the
 * client of the OMA DM protocol. The use cases include the client sending a
 * session request to the server (alert 1201), the client notifying the server
 * of completion of a software update operation (alert 1226) or sending back
 * results in response to an asynchronous EXEC command.
 * <p>
 * The data syntax and semantics varies widely between various alerts, so does
 * the optionality of particular parameters of an alert item. If an item, such
 * as source or type, is not defined, the corresponding getter method returns
 * <code>null</code>. For example, for alert 1201 (client-initiated session)
 * all elements will be <code>null</code>.
 */
public class DmtAlertItem {
	private static final MessageFormat xmlTag = 
        new MessageFormat("<{0}>{1}</{0}>");
    
	private String source;
	private String type;
	private String format;
	private String mark;
	private String data;

	/**
	 * Create an instance of the alert item. The constructor takes all possible
	 * data entries as parameters. Any of these parameters can be
	 * <code>null</code>
	 * 
	 * @param source The URI of the node which is the source of the alert item
	 * @param type The type of the alert item
	 * @param format The format of the alert item
	 * @param mark The markup of the alert item (as defined by OMA DM standards)
	 *        If this is <code>null</code>, but at least one of the other
	 *        parameters are not <code>null</code>, the markup string
	 *        returned by the <code>getMark()</code> method is created using
	 *        the other parameters. If this parameter is not <code>null</code>,
	 *        the <code>getMark()</code> method returns this value.
	 * @param data The data of the alert item
	 */
	public DmtAlertItem(String source, String type, String format, String mark,
			String data) {
		this.source = source;
		this.type = type;
		this.format = format;
		this.mark = mark;
		this.data = data;
	}

	/**
	 * Get the node which is the source of the alert. There might be no source
	 * associated with the alert item.
	 * 
	 * @return The URI of the node which is the source of this alert. Can be
	 *         <code>null</code>.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Get the type associated with the alert item. There might be no format
	 * associated with the alert item.
	 * 
	 * @return The type type associated with the alert item. Can be
	 *         <code>null</code>.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the format associated with the alert item. There might be no format
	 * associated with the alert item.
	 * 
	 * @return The format associated with the alert item. Can be
	 *         <code>null</code>.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Get the markup data associated with the alert item. There might be no
	 * markup associated with the alert item.
	 * 
	 * @return The markup data associated with the alert item, in the format
	 *         defined by OMA DM specifications. Can be <code>null</code>.
	 */
	public String getMark() {
		if (mark == null
				&& (source != null || type != null || format != null || data != null)) {
			// TODO insert newlines and indentations for readable XML output
			return wrap("Item", wrap("Source", wrap("LocURI", source))
					+ wrap("Meta", wrap("Type", type) + wrap("Format", format))
					+ wrap("Data", data));
		}
		return mark;
	}

	/**
	 * Get the data associated with the alert item. There might be no data
	 * associated with the alert item.
	 * 
	 * @return The data associated with the alert item. Can be <code>null</code>.
	 */
	public String getData() {
		return data;
	}

	public String toString() {
		return "DmtAlertItem(\"" + source + "\", \"" + type + "\", \"" + format
				+ "\", \"" + mark + "\", \"" + data + "\")";
	}

	private String wrap(String name, String content) {
		if (content == null || content.length() == 0)
			return "";
		return xmlTag.format(new Object[] {name, content});
	}
	// TODO remove test code
	/*
	 * public static void main(String[] args) { printMark("source", "type",
	 * "format", "mark", "data"); printMark( null, "type", "format", null,
	 * "data"); printMark("source", null, "format", null, "data");
	 * printMark("source", "type", null, null, "data"); printMark("source",
	 * "type", "format", null, "data"); printMark("source", "type", "format",
	 * null, null ); printMark("source", null, null, null, "data"); printMark(
	 * null, null, null, null, null ); }
	 * 
	 * private static void printMark(String source, String type, String format,
	 * String mark, String data) { DmtAlertItem ai = new DmtAlertItem(source,
	 * type, format, mark, data); System.out.println("AlertItem: " + ai);
	 * System.out.println("Mark: " + ai.getMark()); }
	 */
}
