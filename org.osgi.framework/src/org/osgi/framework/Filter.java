/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2000, 2002).
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

package org.osgi.framework;

import java.util.Dictionary;

/**
 * An RFC 1960-based Filter.
 * <p><tt>Filter</tt> objects can be created by calling
 * {@link BundleContext#createFilter} with the chosen filter string.
 * <p>A <tt>Filter</tt> object can be used numerous times to determine if the
 * match argument matches the filter string that was used to create the <tt>Filter</tt>
 * object.
 * <p>Some examples of LDAP filters are:
 *
 * <pre>
 *   &quot;(cn=Babs Jensen)&quot;
 *   &quot;(!(cn=Tim Howes))&quot;
 *   &quot;(&amp;(&quot; + Constants.OBJECTCLASS + &quot;=Person)(|(sn=Jensen)(cn=Babs J*)))&quot;
 *   &quot;(o=univ*of*mich*)&quot;
 * </pre>
 *
 * @version $Revision$
 * @since 1.1
 */

public interface Filter
{
	/**
	 * Filter using a service's properties.
	 * <p>The filter is executed using properties of the referenced service.
	 *
	 * @param reference The reference to the service whose properties are used in the match.
	 *
	 * @return <tt>true</tt> if the service's properties match this filter;
	 * <tt>false</tt> otherwise.
	 */
	public boolean match(ServiceReference reference);

	/**
	 * Filter using a <tt>Dictionary</tt> object.
	 * The Filter is executed using the <tt>Dictionary</tt> object's keys and values.
	 *
	 * @param dictionary The <tt>Dictionary</tt> object whose keys are used in the match.
	 *
	 * @return <tt>true</tt> if the <tt>Dictionary</tt> object's keys and values match this filter;
	 * <tt>false</tt> otherwise.
	 *
	 * @exception IllegalArgumentException If <tt>dictionary</tt> contains case
	 * variants of the same key name.
	 */
	public boolean match(Dictionary dictionary);

	/**
	 * Returns this <tt>Filter</tt> object's filter string.
	 * <p>The filter string is normalized by removing whitespace which does
	 * not affect the meaning of the filter.
	 *
	 * @return Filter string.
	 */
	public String toString();

	/**
	 * Compares this <tt>Filter</tt> object to another object.
	 *
	 * @param obj The object to compare against this <tt>Filter</tt> object.
	 *
	 * @return If the other object is a <tt>Filter</tt> object, then
	 * returns <tt>this.toString().equals(obj.toString()</tt>;
	 * <tt>false</tt> otherwise.
	 */
	public boolean equals(Object obj);

	/**
	 * Returns the hashCode for this <tt>Filter</tt> object.
	 *
	 * @return The hashCode of the filter string; that is, <tt>this.toString().hashCode()</tt>.
	 */
	public int hashCode();
}


