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
package org.osgi.impl.service.deploymentadmin;

import java.io.InputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.deploymentadmin.spi.ResourceProcessorException;
import org.osgi.util.tracker.ServiceTracker;

/*
 * The Deployment Admin service must execute all its operations, including calls 
 * for handling bundles and all calls that are forwarded to a Resource Processor
 * service, inside a doPrivileged block. This privileged block must use an
 * AccessControlContext object that limits the permissions to the security scope.
 * Wraps resource processor calls into doPrivileged calls with the got 
 * AccessControlContext.  
 */
public class WrappedResourceProcessor implements ResourceProcessor, Serializable {
    
    // the wrapped processor
    private final String                   pid;
    // the context of the calls
    private transient AccessControlContext ctx;
    // tracks RPs
    private static ServiceTracker          trackRp;
    
    // TODO after deserialize set the ctx

    public WrappedResourceProcessor(String pid, AccessControlContext ctx,
            ServiceTracker rpTracker) {
        this.pid = pid;
        this.ctx = ctx;
        trackRp = rpTracker;
    }
    
    void setCtx(AccessControlContext ctx) {
    	this.ctx = ctx;
    }
    
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
        	return true;
        if (!(obj instanceof WrappedResourceProcessor))
            return false;
        WrappedResourceProcessor other = (WrappedResourceProcessor) obj;
        return pid == other.pid;
    }
    
    public int hashCode() {
        return pid.hashCode();
    }
    
    public String toString() {
        return "[WrappedResourceProcessor pid: " + pid + "]";
    }
    
    String getPid() {
    	return pid;
    }
    
    /*
     * Finds a Resource processor to the given PID silently
     */
    static ResourceProcessor findProcessorSilent(String pid) {
    	try {
    		return findProcessor(pid);
    	} catch (RuntimeException e) {
			return null;
		}
    }
    
    /*
     * Finds a Resource processor to the given PID
     */
    static ResourceProcessor findProcessor(String pid) {
        ServiceReference[] refs = (ServiceReference[]) AccessController
                .doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return trackRp.getServiceReferences();
                    }
                });
        
        if (null == refs)
            throw new RuntimeException("Resource processor for pid " + pid + 
            	" is not found.");
            
        for (int i = 0; i < refs.length; i++) {
            ServiceReference ref = refs[i];
            String s_pid = (String) ref.getProperty(Constants.SERVICE_PID);
            if (pid.equals(s_pid))
                return (ResourceProcessor) trackRp.getService(ref);
        }
        
        throw new RuntimeException("Resource processor for pid " + pid + 
        	" is not found.");
    }

    public void begin(final DeploymentSession session) {
        if (null == ctx)
        	findProcessor(pid).begin(session);
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        findProcessor(pid).begin(session);
                        return null;
                    }
                }, ctx);
        }
    }

    public void process(final String name, final InputStream stream) throws ResourceProcessorException {
        if (null == ctx)
            findProcessor(pid).process(name, stream);
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws ResourceProcessorException {
                        	findProcessor(pid).process(name, stream);
	                        return null;
	                    }
                    }, ctx);
            }
            catch (PrivilegedActionException e) {
                throw (ResourceProcessorException) e.getException();
            }
        }
    }

    public void dropped(final String name) throws ResourceProcessorException {
        if (null == ctx)
            findProcessor(pid).dropped(name);
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws ResourceProcessorException {
                        	findProcessor(pid).dropped(name);
	                        return null;
	                    }
                    }, ctx);
            }
            catch (PrivilegedActionException e) {
                throw (ResourceProcessorException) e.getException();
            }
        }
    }

    public void dropAllResources() throws ResourceProcessorException {
        if (null == ctx)
            findProcessor(pid).dropAllResources();
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws ResourceProcessorException {
                        	findProcessor(pid).dropAllResources();
	                        return null;
	                    }
                    }, ctx);
            }
            catch (PrivilegedActionException e) {
                throw (ResourceProcessorException) e.getException();
            }
        }
    }

    public void prepare() throws ResourceProcessorException {
        if (null == ctx)
            findProcessor(pid).prepare();
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws ResourceProcessorException {
                        	findProcessor(pid).prepare();
	                        return null;
	                    }
                    }, ctx);
            }
            catch (PrivilegedActionException e) {
                throw (ResourceProcessorException) e.getException();
            }
        }
    }

    public void commit() {
        if (null == ctx)
            findProcessor(pid).commit();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        findProcessor(pid).commit();
                        return null;
                    }
                }, ctx);
        }
    }

    public void rollback() {
        if (null == ctx)
            findProcessor(pid).rollback();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        findProcessor(pid).rollback();
                        return null;
                    }
                }, ctx);
        }
    }

    public void cancel() {
        if (null == ctx)
            findProcessor(pid).cancel();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        findProcessor(pid).cancel();
                        return null;
                    }
                }, ctx);
        }
    }

	static void setRpTracker(ServiceTracker trackRp) {
		WrappedResourceProcessor.trackRp = trackRp; 	
	}

}
