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
package org.osgi.service.monitor;

/**
 * A Monitorable can provide information about itself in the form of KPIs.
 * Instances of this interface should register themselves at the OSGi Service
 * registry. The MonitorAdmin listens to the registration of Monitorable
 * services, and makes the information they provide available also through the
 * Device Management Tree (DMT). The monitorable service is identified by its
 * PID string which must not contain the Reserved characters described in 2.2 of
 * RFC-2396 (URI Generic Syntax).
 * <p>
 * A Monitorable may optionally support sending notifications when the status of
 * its KPIs change.
 */
public interface Monitorable {
	/**
	 * Returns the list of KPI identifiers published by this Monitorable. A KPI
	 * name is unique within the scope of a Monitorable. The array contains the
	 * elements in no particular order.
	 * 
	 * @return the name of KPIs published by this object
	 */
	public String[] getKpiNames();

	/**
	 * Returns the list of long KPI names published by this Monitorable. This
	 * name is the combination of the ID of the Monitorable and the name of the
	 * KPI in the following format: [Monitorable_ID]/[KPI_name], for example
	 * MyApp/QueueSize. This name is guaranteed to be unique on the service
	 * platform and it is used in a MonitoringJob�s list of observed KPIs. The
	 * array contains the elements in no particular order.
	 * 
	 * @return the 'fully qualified' name of KPIs published by this object
	 */
	public String[] getKpiPaths();

	/**
	 * Returns all the KPI objects published by this Monitorable instance. The
	 * KPIs will hold the values taken at the time of this method call. The
	 * array contains the elements in no particular order.
	 * 
	 * @return the KPI objects published by this Monitorable instance
	 */
	public KPI[] getKpis();

	/**
	 * Returns the KPI object addressed by its identifier. The KPI will hold the
	 * value taken at the time of this method call.
	 * 
	 * @param id the identifier of the KPI. The method returns the same KPI
	 *        regardless of whether the short or long ID is used.
	 * @return the KPI object
	 * @throws IllegalArgumentException if the path is invalid or points to a
	 *         non existing KPI
	 */
	public KPI getKpi(String id) throws IllegalArgumentException;

	/**
	 * Tells whether the KPI provider is able to send instant notifications when
	 * the given KPI changes. If the Monitorable supports sending change updates
	 * it must notify the UpdateListener when the value of the KPI changes. The
	 * Monitorable finds the UpdateListener service through the Service
	 * Registry.
	 * 
	 * @param id the identifier of the KPI. The method works the same way
	 *        regardless of whether the short or long ID is used.
	 * @return <code>true</code> if the Monitorable can send notification when
	 *         the given KPI chages, <code>false</code> otherwise.
	 * @throws IllegalArgumentException if the path is invalid or points to a
	 *         non existing KPI
	 */
	public boolean notifiesOnChange(String id) throws IllegalArgumentException;

	/**
	 * Issues a request to reset a given KPI. Depending on the semantics of the
	 * KPI this call may or may not succeed: it makes sense to reset a counter
	 * to its starting value, but e.g. a KPI of type String might not have a
	 * meaningful default value. Note that for numeric KPIs the starting value
	 * may not necessarily be 0. Resetting a KPI triggers a monitor event.
	 * 
	 * @param id the identifier of the KPI. The method works the same way
	 *        regardless of whether the short or long ID is used.
	 * @return <code>true</code> if the Monitorable could successfully reset
	 *         the given KPI, <code>false</code> otherwise
	 * @throws IllegalArgumentException if the path is invalid or points to a
	 *         non existing KPI
	 */
	public boolean resetKpi(String id) throws IllegalArgumentException;
}
