package org.osgi.service.monitor;

/**
 * A Monitorable can provide information about itself in the form of KPIs.
 * Instances of this interface should register themselves at the OSGi Service
 * registry. The MonitorAdmin listens to the registration of Monitorable
 * services, and makes the information they provide available also through the
 * Device Management Tree (DMT). The monitorable service is identified by its
 * PID string which must not contain a '/' character.
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
	 * KPI in the following format: [Monitorable_ID]"/"[KPI_name], for example
	 * MyApp/QueueSize. This name is guaranteed to be unique on the service
	 * platform and it is used in a MonitoringJob’s list of observed KPIs. The
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
	 */
	public KPI getKpi(String id);
}
