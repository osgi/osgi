
package org.osgi.service.application;

/**
 * An application in the MEG environment.
 * @modelguid {379ACE10-6083-40B6-8CC5-05472A709FAA}
 */ 

public interface Application {

	/** @modelguid {92B6646C-CFEB-4886-A14C-F899FF0B20E4} */
 	public void startApplication() throws Exception;

	/** @modelguid {7842F838-7F84-439F-B4B0-D87F018EAECB} */
	public void stopApplication() throws Exception;

	/** @modelguid {31BAF0C4-489B-422E-976D-2EBC0ACAD105} */
	public void suspendApplication() throws Exception;

	/** @modelguid {650B63DB-629A-455C-A1A8-CB376014696F} */
	public void resumeApplication() throws Exception; 

}
