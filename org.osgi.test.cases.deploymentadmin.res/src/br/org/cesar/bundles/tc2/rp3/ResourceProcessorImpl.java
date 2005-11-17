/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ===========   ==============================================================
 * Jun 10, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 */
package br.org.cesar.bundles.tc2.rp3;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentSession.InstallSession;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingSessionResourceProcessor;

/**
 * @author Andre Assad
 *
 */
public class ResourceProcessorImpl implements BundleActivator, TestingSessionResourceProcessor {

	private ServiceRegistration sr;
	private DeploymentSession session;
    
    private String resourceName;
    private InputStream resourceStream;
    
    private boolean processed;
    private static boolean exceptionAtProcess;
    
    private boolean dropped;
    private static boolean exceptionAtDropped;

    private boolean droppedAllResources;
    private static boolean exceptionAtDropAllResources;

    private boolean prepared;
    private static boolean exceptionAtPrepare;

    private boolean commited;
    private static boolean exceptionAtCommit;

    private boolean rolledback;
    private static boolean exceptionAtRollback;

    private boolean cancelled;
    private static boolean exceptionAtCancel;
    
    private static boolean released = false;
    
    private boolean started;
    
    private boolean testDropped;

	public void start(BundleContext bc) throws Exception {
        if (InstallSession.EXCEPTION_AT_START)
            throw new Exception();

        Dictionary props = new Hashtable();
		props.put("service.pid", DeploymentConstants.PID_RESOURCE_PROCESSOR3);
		sr = bc.registerService(ResourceProcessor.class.getName(), this, props);
		
		System.out.println("Resource Processor started.");
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		
	}

	/**
	 * @return Returns private data area of the bundle.
	 */
	public File getDataFile(Bundle bundle) {
		return (bundle!=null)?session.getDataFile(bundle):null;
	}

	/**
	 * @return Returns the session target deployment package.
	 */
	public DeploymentPackage getTargetDeploymentPackage() {
		return session.getTargetDeploymentPackage();
	}

	/**
	 * @return Returns the session source deployment package.
	 */
	public DeploymentPackage getSourceDeploymentPackage() {
		return session.getSourceDeploymentPackage();
	}
	
	public void begin(DeploymentSession session) {
		this.session = session;
	}

    public void process(String arg0, InputStream arg1)
             {
        if (exceptionAtProcess)
            throw new RuntimeException();
        processed = true;
        resourceName = arg0;
        resourceStream = arg1;
    }

    public void dropped(String arg0)  {
        dropped = true;
        resourceName = arg0;
        if (testDropped) {
            waitForRelease(false);
        }
        if (exceptionAtDropped)
            throw new RuntimeException();
    }

    public void dropAllResources()  {
        if (exceptionAtDropAllResources)
            throw new RuntimeException();
        droppedAllResources = true;
    }

    public void prepare()  {
        if (exceptionAtPrepare)
            throw new RuntimeException();
        prepared = true;
    }

    public void commit() {
        if (exceptionAtCommit)
            throw new RuntimeException();
        commited = true;
    }

    public void rollback() {
        if (exceptionAtRollback)
            throw new RuntimeException();
        rolledback = true;
        InstallSession.ROLL_BACK = true;
    }

    public void cancel() {
        if (exceptionAtCancel)
            throw new RuntimeException();
        cancelled = true;
    }

    public boolean isProcessed() {
        return processed;
    }

    public boolean isDropped() {
        return dropped;
    }

    public boolean isDroppedAllResources() {
        return droppedAllResources;
    }

    public boolean isPrepared() {
        return prepared;
    }

    public boolean isCommited() {
        return commited;
    }

    public boolean isRolledback() {
        return rolledback;
    }

    public boolean isCancelled() {
        return cancelled;
    }
    
    public boolean isStarted() {
        return started;
    }

    public void reset() {
        cancelled = false;
        commited = false;
        droppedAllResources = false;
        dropped = false;
        prepared = false;
        processed = false;
        rolledback = false;
        // exceptions
        exceptionAtCancel = false;
        exceptionAtCommit = false;
        exceptionAtDropAllResources = false;
        exceptionAtDropped = false;
        exceptionAtPrepare = false;
        exceptionAtProcess = false;
        exceptionAtRollback = false;
        // shared statics
        InstallSession.EXCEPTION_AT_START = false;
        InstallSession.ROLL_BACK = false;
        // vars
        released = false;
        resourceName = null;
        resourceStream = null;
    }
    /**
     * @param exceptionAtCancel The exceptionAtCancel to set.
     */
    public void setException(int type) {
        switch (type) {
            case TestingSessionResourceProcessor.CANCEL: { exceptionAtCancel = true; break;}
            case TestingSessionResourceProcessor.COMMIT: { exceptionAtCommit = true; break;}
            case TestingSessionResourceProcessor.DROP_ALL_RESOURCES: { exceptionAtDropAllResources = true; break;}
            case TestingSessionResourceProcessor.DROPPED: { exceptionAtDropped = true; break;}
            case TestingSessionResourceProcessor.PREPARE: { exceptionAtPrepare = true; break;}
            case TestingSessionResourceProcessor.PROCESS: { exceptionAtProcess = true; break;}
            case TestingSessionResourceProcessor.ROLL_BACK: { exceptionAtRollback = true; break;}
            case TestingSessionResourceProcessor.START: { InstallSession.EXCEPTION_AT_START = true; break;} //unlikely to happen
            default: ; // do nothing
        }
    }
    
    public void setTest(int type) {
        switch (type) {
            case TestingSessionResourceProcessor.DROPPED: { testDropped = true; break;}
            default: ; // do nothing
        }
    }
    
    /**
     * @return Returns the resourceName.
     */
    public String getResourceName() {
        return resourceName;
    }
    /**
     * @return Returns the resourceStream.
     */
    public InputStream getResourceStream() {
        return resourceStream;
    }
    
    /**
     * 
     */
    public synchronized void waitForRelease(boolean waitAgain) {
        if (released) {
            if (waitAgain) {
                dropped = false;
                released = false;
            }
            System.out.println("Blocking Resource Processor released: "+System.currentTimeMillis());
            notifyAll();
        } else
            while (!released) {
                try {
                    wait(DeploymentConstants.TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
    
    /**
     * @param released The released to set.
     */
    public synchronized void setReleased(boolean released) {
        ResourceProcessorImpl.released = released;
    }
    /**
     * @return Returns the released.
     */
    public boolean isReleased() {
        return released;
    }
}
