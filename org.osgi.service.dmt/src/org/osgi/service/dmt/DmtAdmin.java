/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.service.dmt;

/**
 * An interface providing methods to open sessions and send alerts. The 
 * implementation of <code>DmtAdmin</code> should register itself in the OSGi 
 * service registry as a service. <code>DmtAdmin</code> is the entry point for 
 * applications to use the DMT API. The <code>getSession</code> methods are used
 * to open a session on a specified subtree of the DMT. A typical way of usage:
 * <pre>
 *  serviceRef = context.getServiceReference(DmtAdmin.class.getName());
 *  DmtAdmin factory = (DmtAdmin) context.getService(serviceRef);
 *  DmtSession session = factory.getSession(&quot;./OSGi/Configuration&quot;);
 *  session.createInteriorNode(&quot;./OSGi/Configuration/my.table&quot;);
 * </pre>
 * <p>
 * The methods for opening a session take a node URI (the session root) as 
 * parameter. All segments of the given URI must be within the segment length
 * limit of the implementation, and the special characters '/' and '\' must be
 * escaped (preceded by a '\').  Any string can be converted to a valid URI
 * segment using the {@link #mangle(String)} method.
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
     * @throws DmtException with the following possible error codes:
     *         <ul>
     *         <li><code>URI_TOO_LONG</code> if <code>subtreeUri</code> or a
     *         segment of it is too long, or if it has too many segments
     *         <li><code>INVALID_URI</code> if <code>subtreeUri</code> is
     *         syntactically invalid
     *         <li><code>NODE_NOT_FOUND</code> if <code>subtreeUri</code>
     *         specifies a non-existing node
     *         <li><code>TIMEOUT</code> if the operation timed out because of
     *         another ongoing session
     *         <li><code>COMMAND_FAILED</code> for unspecified errors
     *         encountered while attempting to complete the command
     *         </ul>
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
     * @throws DmtException with the following possible error codes:
     *         <ul>
     *         <li><code>URI_TOO_LONG</code> if <code>subtreeUri</code> or a
     *         segment of it is too long, or if it has too many segments
     *         <li><code>INVALID_URI</code> if <code>subtreeUri</code> is
     *         syntactically invalid
     *         <li><code>NODE_NOT_FOUND</code> if <code>subtreeUri</code>
     *         specifies a non-existing node
     *         <li><code>FEATURE_NOT_SUPPORTED</code> if atomic sessions are
     *         not supported by the implementation and <code>lockMode</code> 
     *         requests an atomic session
     *         <li><code>TIMEOUT</code> if the operation timed out because of
     *         another ongoing session
     *         <li><code>COMMAND_FAILED</code> if <code>lockMode</code> is
     *         unknown, or some unspecified error is encountered while 
     *         attempting to complete the command
     *         </ul>
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
     * @throws DmtException with the following possible error codes:
     *         <ul>
     *         <li><code>URI_TOO_LONG</code> if <code>subtreeUri</code> or a
     *         segment of it is too long, or if it has too many segments
     *         <li><code>INVALID_URI</code> if <code>subtreeUri</code> is
     *         syntactically invalid
     *         <li><code>NODE_NOT_FOUND</code> if <code>subtreeUri</code>
     *         specifies a non-existing node
     *         <li><code>FEATURE_NOT_SUPPORTED</code> if atomic sessions are
     *         not supported by the implementation and <code>lockMode</code> 
     *         requests an atomic session
     *         <li><code>TIMEOUT</code> if the operation timed out because of
     *         another ongoing session
     *         <li><code>COMMAND_FAILED</code> if <code>lockMode</code> is
     *         unknown, or some unspecified error is encountered while 
     *         attempting to complete the command
     *         </ul>
     * @throws SecurityException if the caller does not have the required
     *         <code>DmtPrincipalPermission</code> with a target matching the
     *         <code>principal</code> parameter
     */
    DmtSession getSession(String principal, String subtreeUri, int lockMode)
        throws DmtException;
    
    /**
     * Returns a node name that is valid for the tree operation methods, based
     * on the given node name. This transformation is not idempotent, so it must
     * not be called with a parameter that is the result of a previous
     * <code>mangle</code> method call.
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
     * The method returns the normalized <code>nodeName</code> as described
     * below. Invalid node names are normalized in different ways, depending on
     * the cause. If the length of the name does not exceed the limit, but the
     * name contains '/' or '\' characters, then these are simply escaped by
     * inserting an additional '\' before each occurrence. If the length of the
     * name does exceed the limit, the following mechanism is used to normalize
     * it:
     * <ul>
     * <li>the SHA 1 digest of the name is calculated
     * <li>the digest is encoded with the base 64 algorithm
     * <li>all '/' characters in the encoded digest are replaced with '_'
     * <li>trailing '=' signs are removed
     * </ul>
     * 
     * @param nodeName the node name to be mangled (if necessary), must not be
     *        <code>null</code> or empty
     * @return the normalized node name that is valid for tree operations
     * @throws NullPointerException if <code>nodeName</code> is
     *         <code>null</code>
     * @throws IllegalArgumentException if <code>nodeName</code> is empty
     * @see DmtSession#mangle
     */
    String mangle(String nodeName);

    /**
     * Sends an alert to a named principal. If OMA DM is used as a management
     * protocol the principal name is server ID that corresponds to a DMT node
     * value in <code>./SyncML/DMAcc/x/ServerId</code>. It is the DmtAdmin's
     * responsibility to route the alert to the given principal using the
     * <code>RemoteAlertSender</code> services.
     * <p>
     * In remotely initiated sessions the principal name identifies the remote
     * server that created the session, this can be obtained using the session's
     * {@link DmtSession#getPrincipal getPrincipal} call.
     * <p>
     * The principal name may be omitted if the client does not know the
     * principal name. Even in this case the routing might be possible if the
     * Dmt Admin finds an appropriate default destination (for example if it is 
     * only connected to one protocol adapter, which is only connected to one 
     * management server).
     * <p>
     * Since sending the alert and receiving acknowledgment for it is 
     * potentially a very time-consuming operation, alerts are sent
     * asynchronously.  This method should attempt to ensure that the alert can
     * be sent successfully, and should throw an exception if it detects any 
     * problems.  If the method returns without error, the alert is accepted for 
     * sending and the implementation must make a best-effort attempt to 
     * deliver it.
     * <p>
     * In case the alert is an asynchronous response to a previous 
     * {@link DmtSession#execute(String, String, String) execute} command,
     * a correlation identifier can be specified to provide the association
     * between the execute and the alert.
     * <p>
     * In order to send an alert using this method, the caller must have an
     * <code>AlertPermission</code> with a target string matching the specified
     * principal name.  If the <code>principal</code> parameter is 
     * <code>null</code> (the principal name is not known), the target of the
     * <code>AlertPermission</code> must be &quot;*&quot;. 
     * 
     * @param principal the principal name which is the recepient of this alert,
     *        can be <code>null</code>
     * @param code the alert code, can be 0 if not needed
     * @param correlator optional field that contains the correlation identifier
     *        of an associated exec command, can be <code>null</code> if not
     *        needed
     * @param items the data of the alert items carried in this alert, can be
     *        <code>null</code> or empty if not needed
     * @throws DmtException with the following possible error codes:
     *         <ul>
     *         <li><code>ALERT_NOT_ROUTED</code> when the alert can not be
     *         routed to the given principal
     *         <li><code>REMOTE_ERROR</code> in case of communication
     *         problems between the device and the destination
     *         <li><code>COMMAND_FAILED</code> for unspecified errors
     *         encountered while attempting to complete the command
     *         </ul>
     * @throws SecurityException if the caller does not have the required
     *         <code>AlertPermission</code> with a target matching the
     *         <code>principal</code> parameter, as described above
     */
    void sendAlert(String principal, int code, String correlator, 
            AlertItem[] items) throws DmtException;
}
