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
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

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
public class WrappedResourceProcessor implements ResourceProcessor {
    
    // the wrapped processor
    private final ServiceReference     rpRef;
    // the context of the calls
    private final AccessControlContext ctx;
    // tracks RPs
    private ServiceTracker rpTracker;
    // id
    private final String id;

    public WrappedResourceProcessor(ServiceReference rpRef, AccessControlContext ctx,
            ServiceTracker rpTracker) {
        this.rpRef = rpRef;
        this.ctx = ctx;
        this.rpTracker = rpTracker;
        this.id = "" + System.currentTimeMillis() + "_" + rpRef;
    }
    
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof WrappedResourceProcessor))
            return false;
        WrappedResourceProcessor other = (WrappedResourceProcessor) o;
        return id == other.id;
    }
    
    public int hashCode() {
        return id.hashCode();
    }
    
    public String toString() {
        return "WRP id: " + id;
    }
    
    private ResourceProcessor getRp() {
        return (ResourceProcessor) rpTracker.getService(rpRef);
    }

    public void begin(final DeploymentSession session) {
        if (null == ctx)
            getRp().begin(session);
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        getRp().begin(session);
                        return null;
                    }
                }, ctx);
        }
    }

    public void process(final String name, final InputStream stream) throws ResourceProcessorException {
        if (null == ctx)
            getRp().process(name, stream);
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws ResourceProcessorException {
                        	getRp().process(name, stream);
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
            getRp().dropped(name);
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws ResourceProcessorException {
                        	getRp().dropped(name);
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
            getRp().dropAllResources();
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws ResourceProcessorException {
                        	getRp().dropAllResources();
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
            getRp().prepare();
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws ResourceProcessorException {
                        	getRp().prepare();
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
            getRp().commit();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        getRp().commit();
                        return null;
                    }
                }, ctx);
        }
    }

    public void rollback() {
        if (null == ctx)
            getRp().rollback();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        getRp().rollback();
                        return null;
                    }
                }, ctx);
        }
    }

    public void cancel() {
        if (null == ctx)
            getRp().cancel();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        getRp().cancel();
                        return null;
                    }
                }, ctx);
        }
    }

}
