package org.osgi.service.monitor;

/**
 * A Monitoring Job is a scheduled periodic update of a set of KPIs.
 * The job is a data structure that holds a non-empty list of KPI names,
 * an identification of the initiator of the job,
 * the frequency with which a measurement should be taken, and optionally the 
 * number of samples to be taken. The job can be started on the 
 * MonitorAdmin interface. Running the job (querying the KPIs and sending out
 * notifications on updates) is the task of the MonitorAdmin implementation.
 */
public interface MonitoringJob {
	
	/**
	 * Stops a Monitoring Job.
	 * In case of successful stopping of a job the MonitorAdmin should 
	 * generate the appropriate event. Note that a job can also stop 
	 * automatically in the case when all the KPIs it is observing go away.
	 */
	public void stop();
	
	/**
	 * Returns the identitifier of the principal who initiated the job. 
	 * This is set at the time when startJob() is called at the MonitorAdmin
	 * interface. This string holds the ServerID if the operation was initiated
	 * from a remote manager, or the bundle ID or UE ID in the local case.
	 * @return the ID of the initiator.
	 */
	public String getInitiator();
	
	/**
	 * Returns the list of KPI names which is the target of this measurement job.
	 * The MonitorAdmin will iterate through this list and query all KPIs when
	 * it's timer set by the job's frequency rate expires.
	 * @return the target list of the measurement job in 
	 * <Monitorable_ID>/<KPI_ID> format
	 */
	public String[] getKpiNames();
	
	/**
	 * Returns the delay (in minutes) between two samples.
	 * If this call returns N then the MonitorAdmin queries each KPI that 
	 * belongs to this job in every N minutes. The value 0 is not allowed.
	 * @return the delay (in minutes) between samples 
	 */
	public long getSchedule();
	
	/**
	 * Returns the number of how many times the MonitorAdmin will query the KPIs.
	 * After the measurement is started it will take 
	 * getReportCount()*getSchedule() time to finish unless it is explicitely
	 * stopped sooner. If the report count is 0 then the measurement will 
	 * not stop automatically, it can be stopped 
	 * only with the stop call.
	 * @return the number of measurements to be taken
	 */
	public int getReportCount();
	
}
