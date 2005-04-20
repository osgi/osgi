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

import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentSession;
import org.osgi.service.deploymentadmin.ResourceProcessor;

public class WrappedResourceProcessor implements ResourceProcessor {
    
    private final ResourceProcessor    rp;
    private final AccessControlContext ctx;

    public WrappedResourceProcessor(ResourceProcessor rp, AccessControlContext ctx) {
        this.rp = rp;
        this.ctx = ctx;
    }

    public void begin(final DeploymentSession session) {
        if (null == ctx)
            rp.begin(session);
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        rp.begin(session);
                        return null;
                    }
                }, ctx);
        }
    }

    public void process(final String name, final InputStream stream) throws DeploymentException {
        if (null == ctx)
            rp.process(name, stream);
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws DeploymentException {
                        	rp.process(name, stream);
	                        return null;
	                    }
                    }, ctx);
            }
            catch (PrivilegedActionException e) {
                throw (DeploymentException) e.getException();
            }
        }
    }

    public void dropped(final String name) throws DeploymentException {
        if (null == ctx)
            rp.dropped(name);
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws DeploymentException {
                        	rp.dropped(name);
	                        return null;
	                    }
                    }, ctx);
            }
            catch (PrivilegedActionException e) {
                throw (DeploymentException) e.getException();
            }
        }
    }

    public void dropAllResources() throws DeploymentException {
        if (null == ctx)
            rp.dropAllResources();
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws DeploymentException {
                        	rp.dropAllResources();
	                        return null;
	                    }
                    }, ctx);
            }
            catch (PrivilegedActionException e) {
                throw (DeploymentException) e.getException();
            }
        }
    }

    public void prepare() throws DeploymentException {
        if (null == ctx)
            rp.prepare();
        else {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction() {
	                    public Object run() throws DeploymentException {
                        	rp.prepare();
	                        return null;
	                    }
                    }, ctx);
            }
            catch (PrivilegedActionException e) {
                throw (DeploymentException) e.getException();
            }
        }
    }

    public void commit() {
        if (null == ctx)
            rp.commit();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        rp.commit();
                        return null;
                    }
                }, ctx);
        }
    }

    public void rollback() {
        if (null == ctx)
            rp.rollback();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        rp.rollback();
                        return null;
                    }
                }, ctx);
        }
    }

    public void cancel() {
        if (null == ctx)
            rp.cancel();
        else {
            AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        rp.cancel();
                        return null;
                    }
                }, ctx);
        }
    }

}
