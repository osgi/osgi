/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.dmt;

/**
 * An implementation of this interface takes the responsibility over a
 * non-modifiable subtree of the DMT. In an OSGi environment such
 * implementations should be registered at the OSGi service registry specifying
 * the list of root node URIs in a <code>String</code> array in the
 * <code>dataRootURIs</code> registration parameter.
 */
public interface DmtReadOnlyDataPlugin extends DmtReadOnly {

    // TODO can we specify OTHER_ERROR more precisely?
    /**
     * This method is called to signal the start of a transaction when the first
     * reference is made within a <code>DmtSession</code> to a node which is
     * handled by this plugin. Session information is given as it is needed for
     * sending alerts back from the plugin.
     * 
     * @param subtreeUri the subtree which is locked in the current session,
     *        must not be <code>null</code>
     * @param session the session from which this plugin instance is accessed,
     *        must not be <code>null</code>
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>OTHER_ERROR</code>
     * @throws SecurityException if some underlying operation failed because of
     *         lack of permissions
     */
    void open(String subtreeUri, DmtSession session) throws DmtException;
}
