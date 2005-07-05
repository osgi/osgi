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
 * An interface providing methods to open sessions and send alerts.
 * The implementation of
 * <code>DmtAdmin</code> should register itself in the OSGi service registry
 * as a service. <code>DmtAdmin</code> is the entry point for applications to
 * use the DMT API. The <code>getSession</code> methods are used to open a
 * session on a specified subtree of the DMT. A typical way of usage:
 * <pre>
 *  serviceRef = context.getServiceReference(
 *    DmtAdmin.class.getName());
 *  DmtAdmin factory = (DmtAdmin) 
 *    context.getService(serviceRef);
 *  DmtSession session = factory.getSession(
 *    &quot;./OSGi/cfg&quot;);
 *  session.createInteriorNode(
 *    &quot;./OSGi/cfg/mycfg&quot;);
 * </pre>
 */
public interface DmtAdmin {
    /**
     * Opens a <code>DmtSession</code> for local usage on a given subtree of
     * the DMT with non transactional write lock. This call is equivalent to the
     * following:
     * <code>getSession(null, subtreeUri, DmtSession.LOCK_TYPE_EXCLUSIVE)</code>
     * <p>
     * To access the whole tree in this session, use the &quot;.&quot; string or
     * <code>null</code> as subtree URI.
     * 
     * @param subtreeUri the subtree on which DMT manipulations can be performed
     *        within the returned session
     * @return a <code>DmtSession</code> object for the requested subtree
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code> if <code>subtreeUri</code> is
     *         <code>null</code> or syntactically invalid
     *         <li><code>TIMEOUT</code>
     */
    DmtSession getSession(String subtreeUri) throws DmtException;

    /**
     * Opens a <code>DmtSession</code> for local usage on a specific DMT
     * subtree with a given locking mode. This call is equivalent to the
     * following: <code>getSession(null, subtreeUri, lockMode)</code>
     * <p>
     * To access the whole tree in this session, use the &quot;.&quot; string or
     * <code>null</code> as subtree URI.
     * 
     * @param subtreeUri the subtree on which DMT manipulations can be performed
     *        within the returned session
     * @param lockMode one of the locking modes specified in
     *        <code>DmtSession</code>
     * @return a <code>DmtSession</code> object for the requested subtree
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code> if <code>subtreeUri</code> is
     *         <code>null</code> or syntactically invalid
     *         <li><code>OTHER_ERROR</code> if the lockMode is unknown
     *         <li><code>TIMEOUT</code>
     */
    DmtSession getSession(String subtreeUri, int lockMode)
        throws DmtException;

    /**
     * Opens a <code>DmtSession</code> on a specific DMT subtree using a
     * specific locking mode on behalf of a remote principal. If local
     * management applications are using this method then they should provide
     * <code>null</code> as the first parameter. Alternatively they can use
     * other forms of this method without providing a principal string. This
     * method is guarded by <code>DmtPrincipalPermission</code> in case of 
     * remote sessions.
     * <p>
     * To access the whole tree in this session, use the &quot;.&quot; string or
     * <code>null</code> as subtree URI.
     * 
     * @param principal the identifier of the remote server on whose behalf the
     *        data manipulation is performed, or <code>null</code> for local
     *        sessions
     * @param subtreeUri the subtree on which DMT manipulations can be performed
     *        within the returned session
     * @param lockMode one of the locking modes specified in
     *        <code>DmtSession</code>
     * @return a <code>DmtSession</code> object for the requested subtree
     * @throws DmtException with the following possible error codes
     *         <li><code>NODE_NOT_FOUND</code>
     *         <li><code>URI_TOO_LONG</code>
     *         <li><code>INVALID_URI</code> if <code>subtreeUri</code> is
     *         <code>null</code> or syntactically invalid
     *         <li><code>OTHER_ERROR</code> if the lockMode is unknown
     *         <li><code>TIMEOUT</code>
     * @throws SecurityException if the caller does not have the required
     *         <code>DmtPrincipalPermission</code> with a target matching the
     *         <code>principal</code> parameter
     * @see DmtPrincipalPermission
     */
    DmtSession getSession(String principal, String subtreeUri, int lockMode)
        throws DmtException;
    
    /**
     * Creates a valid URI string from the given base URI and node name. The
     * base URI is assumed to be valid, while the node name is assumed
     * un-mangled.
     * <p>
     * Node name mangling is needed in the following cases:
     * <ul>
     * <li>if the name contains '/' or '\' characters
     * <li>if the length of the name exceeds the limit defined by the
     * implementation
     * </ul>
     * <p>
     * A node name that does not suffer from either of these problems is
     * guaranteed to remain unchanged by this method. Therefore the client may
     * skip the mangling if the node name is known to be valid (though it is
     * always safe to call this method).
     * <p>
     * The method returns a URI created by appending together the
     * <code>base</code> URI, the '/' separator (if <code>base</code> does
     * not already end with it), and the normalized <code>nodeName</code>.
     * Invalid node names are normalized in different ways, depending on the
     * cause. If the length of the name does not exceed the limit, but the name
     * contains '/' or '\' characters, then these are simply escaped by
     * inserting an additional '\' before each occurrence. If the length of the
     * name does exceed the limit, the following mechanism is used to normalize
     * it:
     * <ul>
     * <li>the SHA 1 digest of the name is calculated
     * <li>the digest is encoded with the base 64 algorithm
     * <li>all '/' characters in the encoded digest are replaced with '_'
     * <li>trailing '=' signs are removed
     * </ul>
     * <p>
     * If the <code>base</code> parameter is <code>null</code>, the
     * returned string contains only the normalized version of the
     * <code>nodeName</code> parameter, without any prefix.
     * 
     * @param base the URI to be used as the base of the returned URI, can be
     *        <code>null</code>
     * @param nodeName the node name to be mangled (if necessary), must not be
     *        <code>null</code> or empty
     * @return the URI containing the <code>base</code> prefix and the
     *         possibly mangled node name as the last segment
     * @throws NullPointerException if <code>nodeName</code> is
     *         <code>null</code>
     * @throws IllegalArgumentException if <code>nodeName</code> is empty
     */
    String mangle(String base, String nodeName);

    /**
     * Sends an alert to a named principal. If OMA DM is used as a management
     * protocol the principal name is server ID that corresponds to a DMT node
     * value in <code>./SyncML/DMAcc/x/ServerId</code>. It is the DmtAdmin's
     * responsibility to route the alert to the given principal.
     * <p>
     * In remotely initiated sessions the principal name corresponds to a remote
     * server ID, which can be obtained using the session's
     * {@link DmtSession#getPrincipal getPrincipal} call.
     * <p>
     * The principal name may be omitted if the client does not know the
     * principal name. Even in this case the routing might be possible if the
     * <code>DmtAdmin</code> is connected to only one protocol adapter, which
     * is connected to only one remote server.
     * <p>
     * In case the alert is an asynchronous response to a previous 
     * {@link DmtSession#execute(String, String, String) execute} command,
     * a correlation identifier can be specified to provide the association
     * between the exec and the alert.
     * 
     * @param principal the principal name which is the recepient of this alert,
     *        can be <code>null</code>
     * @param code the alert code, can be 0 if not needed
     * @param correlator optional field that contains the correlation identifier
     *        of an associated exec command, can be <code>null</code> if not
     *        needed
     * @param items the data of the alert items carried in this alert, can be
     *        <code>null</code> or empty if not needed
     * @throws DmtException with the following possible error codes
     *         <ul>
     *         <li><code>ALERT_NOT_ROUTED</code> when the alert can not be
     *         routed to the server
     *         <li><code>REMOTE_ERROR</code> in case of communication
     *         problems between the device and the server
     *         </ul>
     */
    void sendAlert(String principal, int code, String correlator, 
            DmtAlertItem[] items) throws DmtException;
}
