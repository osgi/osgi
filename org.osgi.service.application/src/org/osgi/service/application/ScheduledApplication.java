package org.osgi.service.application;

/**
 * A scheduled application contains the information of future registered
 * applications.
 * @modelguid {625F9BD1-19D5-4EFA-A000-AE8FE1B5593A}
 */
public interface ScheduledApplication {

	/** @modelguid {A505D4C7-198B-4D7F-BB35-B72F70B97561} */
    public String getEventType();

	/** @modelguid {55C3C652-E899-48F2-BDBE-6F75EBB6518F} */
    public ApplicationDescriptor getApplicationDescriptor();

	/** @modelguid {5EA8CD87-9BA8-47A3-86A9-F2CBDFD24D2A} */
    public void remove();
}