/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.meg.demo.app.events;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.application.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.*;
import org.osgi.service.monitor.KPI;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.UpdateListener;
import java.io.*;
import java.util.Dictionary;
import java.util.Hashtable;

public class TestApplication 
        extends MEGApplication 
		implements ManagedService, Monitorable {
    
    private static final String	PARAMETER_NAME = "TestParameter";
	static final String SERVICE_PID = 
        "org.osgi.meg.demo.app.events.TestApplication";
    
    private MEGApplicationContext context;
	private String fileName;
    private String testParameter;
    
    private ServiceRegistration reg = null;
	private UpdateListener updateListener = null;

	public TestApplication(MEGApplicationContext context) {
		super(context);
        
        this.context = context;
        testParameter = "<unset>";
        
        if (context.getLaunchArgs() != null)
            fileName = (String) context.getLaunchArgs().get("TestResult");
        else
            fileName = null;
	}

	public void startApplication() throws Exception {
		writeResult("START");

        Hashtable config = new Hashtable();
        config.put("service.pid", SERVICE_PID);
        reg = Activator.context.registerService(
                new String[] { ManagedService.class.getName(),
                		       Monitorable.class.getName() }, this, config);
        
        updateListener = (UpdateListener) 
		    context.getServiceObject(UpdateListener.class.getName(), null);
	}

	public void stopApplication() throws Exception {
		writeResult("STOP");
        
		reg.unregister();
        context.ungetServiceObject(updateListener);
	}

	public void suspendApplication() throws Exception {
		writeResult("SUSPEND");
	}

	public void resumeApplication() throws Exception {
		writeResult("RESUME");
	}

	public void channelEvent(ChannelEvent event) {
	}

	public void writeResult(String result) {
		try {
			if (fileName == null)
				return;
			File file = new File(fileName);
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(result.getBytes());
			stream.close();
		} catch (IOException e) {
		}
	}

	public void updated(Dictionary properties) throws ConfigurationException {
        if(properties == null)
        	return;
        
        String testParameterOld = testParameter;
        
        Object param = properties.get(PARAMETER_NAME);
        if(!(param instanceof String))
            testParameter = "<unset>";
        else
            testParameter = (String) param;
        
        if(testParameter != testParameterOld)
            updateListener.updated(getTestParameterKpi());
	}

	public String[] getKpiNames() {
		return new String[] { PARAMETER_NAME };
	}

	public String[] getKpiPaths() {
		return new String[] { SERVICE_PID + "/" + PARAMETER_NAME };
	}

	public KPI[] getKpis() {
		return new KPI[] { getTestParameterKpi() };
	}

	public KPI getKpi(String id) throws IllegalArgumentException {
        if(matchingId(id, PARAMETER_NAME))
			return getTestParameterKpi();
        
        throw new IllegalArgumentException("KPI '" + id + 
                "' not found in Monitorable '" + SERVICE_PID + "'.");
	}

	public boolean notifiesOnChange(String id) throws IllegalArgumentException {
		return true;
	}

	public boolean resetKpi(String id) throws IllegalArgumentException {
		return false;
	}

    private KPI getTestParameterKpi() {
        return new KPI(SERVICE_PID, PARAMETER_NAME, 
                "Value of the configuration item of the same name.", 
                KPI.CM_SI, testParameter);
    }

    private boolean matchingId(String id, String name) {
        return name.equals(id) || (SERVICE_PID + '/' + name).equals(id);
    }
}
