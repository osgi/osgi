package org.osgi.service.monitor;

/**
 * A MonitorAdmin implementation handles KPI query requests and measurement 
 * job control requests.
 * 
 */
public interface MonitorAdmin {

    /**
     * Returns a KPI addressed by its ID in [Monitorable_ID]/[KPI_ID] format.
     * This is a convenience feature for the cases when the full path
     * of the KPI is known. KPIs can also be obtained by querying the
     * list of Monitorable services from the service registry and then querying
     * the list of KPI names from the Monitorable services.
     * @param path the full path of the KPI in [Monitorable_ID]/[KPI_ID] format
     * @return the KPI object
     * @throws IllegalArgumentException if the path is invalid or points to a 
     * non existing KPI
     */
    public KPI getKPI(String path) throws IllegalArgumentException;

    /**
     * Starts a Monitoring Job with the parameters provided.
     * In case of successful starting of a job the MonitorAdmin  
     * generates the appropriate event. If the list of KPI names contains
     * a non existing KPI or the schedule or count parameters are invalid then
     * IllegalArgumentException is thrown.
     * @param kpis list of KPIs to be monitored. The KPI names must be 
     * given in [Monitorable_PID]/[KPI_ID] format
     * @param schedule the time in minutes between two measurements. The value 0
     * is not allowed
     * @param count the number of measurements to be taken or 0 if the 
     * measurement must run infinitely
     * @return the successfully started job object
     * @throws IllegalArgumentException
     */
    public MonitoringJob startJob(String[] kpis, 
                                  int schedule, int count) 
        throws IllegalArgumentException;

    /**
     * Returns the list of currently running Monitoring Jobs.
     * @return the list of running jobs
     */
    public MonitoringJob[] getRunningJobs();

}
