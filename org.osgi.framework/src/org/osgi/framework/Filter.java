/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
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
package org.osgi.framework;

import java.util.Dictionary;

/**
 * An RFC 1960-based Filter.
 * <p>
 * <code>Filter</code> objects can be created by calling
 * {@link BundleContext#createFilter}with the chosen filter string.
 * <p>
 * A <code>Filter</code> object can be used numerous times to determine if the
 * match argument matches the filter string that was used to create the
 * <code>Filter</code> object.
 * <p>
 * Some examples of LDAP filters are:
 * 
 * <pre>
 *     &quot;(cn=Babs Jensen)&quot;
 *     &quot;(!(cn=Tim Howes))&quot;
 *     &quot;(&amp;(&quot; + Constants.OBJECTCLASS + &quot;=Person)(|(sn=Jensen)(cn=Babs J*)))&quot;
 *     &quot;(o=univ*of*mich*)&quot;
 * </pre>
 * 
 * @version $Revision$
 * @since 1.1
 */
public interface Filter {
	/**
	 * Filter using a service's properties.
	 * <p>
	 * The filter is executed using properties of the referenced service.
	 * 
	 * @param reference The reference to the service whose properties are used
	 *        in the match.
	 * 
	 * @return <code>true</code> if the service's properties match this filter;
	 *         <code>false</code> otherwise.
	 */
	public boolean match(ServiceReference reference);

	/**
	 * Filter using a <code>Dictionary</code> object. The Filter is executed using
	 * the <code>Dictionary</code> object's keys and values.
	 * 
	 * @param dictionary The <code>Dictionary</code> object whose keys are used in
	 *        the match.
	 * 
	 * @return <code>true</code> if the <code>Dictionary</code> object's keys and
	 *         values match this filter; <code>false</code> otherwise.
	 * 
	 * @exception IllegalArgumentException If <code>dictionary</code> contains
	 *            case variants of the same key name.
	 */
	public boolean match(Dictionary dictionary);

	/**
	 * Returns this <code>Filter</code> object's filter string.
	 * <p>
	 * The filter string is normalized by removing whitespace which does not
	 * affect the meaning of the filter.
	 * 
	 * @return Filter string.
	 */
	public String toString();

	/**
	 * Compares this <code>Filter</code> object to another object.
	 * 
	 * @param obj The object to compare against this <code>Filter</code> object.
	 * 
	 * @return If the other object is a <code>Filter</code> object, then returns
	 *         <code>this.toString().equals(obj.toString()</code>;<code>false</code>
	 *         otherwise.
	 */
	public boolean equals(Object obj);

	/**
	 * Returns the hashCode for this <code>Filter</code> object.
	 * 
	 * @return The hashCode of the filter string; that is,
	 *         <code>this.toString().hashCode()</code>.
	 */
	public int hashCode();

	/**
	 * Filter with case sensitivity using a <code>Dictionary</code> object. The
	 * Filter is executed using the <code>Dictionary</code> object's keys and
	 * values. The keys are case sensitivley matched with the filter.
	 * 
	 * @param dictionary The <code>Dictionary</code> object whose keys are used in
	 *        the match.
	 * 
	 * @return <code>true</code> if the <code>Dictionary</code> object's keys and
	 *         values match this filter; <code>false</code> otherwise.
	 * 
	 * @since 1.3
	 */
	public boolean matchCase(Dictionary dictionary);
}