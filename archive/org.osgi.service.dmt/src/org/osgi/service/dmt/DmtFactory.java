package org.osgi.service.dmt;

/**
 * The DmtFactory interface is used to create DmtSession objects. The 
 * implementation of DmtFactory should register itself in the OSGi service
 * registry as a service. DmtFactory is the entry point for applications to
 * use the Dmt API. The <code>getTree</code> methods are used to open a session
 * on a specified subtree of the DMT. A typical way of usage:
 * <blockquote><pre>
 *     serviceRef = context.getServiceReference(DmtFactory.class.getName());
 *     DmtFactory factory = (DmtFactory) context.getService(serviceRef);
 *     DmtSession session = factory.getTree(new DmtPrincipal());
 *     session.createInteriorNode("./OSGi/cfg/mycfg");
 * </pre></blockquote>
 */
public interface DmtFactory {
    /**
     * Opens a DmtSession on the whole DMT as subtree. It is recommended to
     * use other forms of this method where operations are issued only on a 
     * specific subtree. This call is equivalent to the following: 
     * <code>getTree(principal, ".", DmtSession.LOCK_TYPE_AUTOMATIC)</code> 
     * @param principal The entity on whose behalf the data manipulation is 
     * performed
     * @return a DmtSession object on which DMT manipulations can be performed
     * @throws DmtException
     */
    DmtSession getTree(DmtPrincipal principal) throws DmtException;
    
    /**
     * Opens a DmtSession on a specific DMT subtree. This call is equivalent 
     * to the following: 
     * <code>getTree(principal, subtreeUri, DmtSession.LOCK_TYPE_AUTOMATIC)
     * </code> 
     * @param principal The entity on whose behalf the data manipulation is 
     * performed
     * @param subtreeUri The subtree on which DMT manipulations can be 
     * performed within the returned session
     * @return a DmtSession object on which DMT manipulations can be performed
     * @throws DmtException
     */
    DmtSession getTree(DmtPrincipal principal, String subtreeUri) 
        throws DmtException;
    
    /**
     * Opens a DmtSession on a specific DMT subtree using a specific locking
     * mode.  
     * @param principal The entity on whose behalf the data manipulation is 
     * performed
     * @param subtreeUri The subtree on which DMT manipulations can be 
     * performed within the returned session
     * @param lockMode One of the locking modes specified in DmtSession
     * @return a DmtSession object on which DMT manipulations can be performed
     * @throws DmtException
     */
    DmtSession getTree(DmtPrincipal principal, String subtreeUri, int lockMode) 
        throws DmtException;
}
