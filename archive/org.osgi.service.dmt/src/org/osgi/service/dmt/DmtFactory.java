/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.dmt;

/**
 * The DmtFactory interface is used to create DmtSession objects. The
 * implementation of DmtFactory should register itself in the OSGi service
 * registry as a service. DmtFactory is the entry point for applications to use
 * the Dmt API. The <code>getTree</code> methods are used to open a session on
 * a specified subtree of the DMT. A typical way of usage: <blockquote>
 * 
 * <pre>
 * serviceRef = context.getServiceReference(DmtFactory.class.getName());
 * DmtFactory factory = (DmtFactory) context.getService(serviceRef);
 * DmtSession session = factory.getTree(null);
 * session.createInteriorNode(&quot;./OSGi/cfg/mycfg&quot;);
 * </pre>
 * 
 * </blockquote>
 */
public interface DmtFactory {
	/**
	 * Opens a DmtSession on the whole DMT as subtree. It is recommended to use
	 * other forms of this method where operations are issued only on a specific
	 * subtree. This call is equivalent to the following:
	 * <code>getTree(principal, ".", DmtSession.LOCK_TYPE_AUTOMATIC)</code>
	 * 
	 * @param principal the identifier of the remote server on whose behalf the
	 *        data manipulation is performed, or <code>null</code> for local
	 *        sessions
	 * @return a DmtSession object on which DMT manipulations can be performed
	 * @throws DmtException
	 */
	DmtSession getTree(String principal) throws DmtException;

	/**
	 * Opens a DmtSession on a specific DMT subtree. This call is equivalent to
	 * the following:
	 * <code>getTree(principal, subtreeUri, DmtSession.LOCK_TYPE_AUTOMATIC)
	 * </code>
	 * 
	 * @param principal the identifier of the remote server on whose behalf the
	 *        data manipulation is performed, or <code>null</code> for local
	 *        sessions
	 * @param subtreeUri The subtree on which DMT manipulations can be performed
	 *        within the returned session
	 * @return a DmtSession object on which DMT manipulations can be performed
	 * @throws DmtException
	 */
	DmtSession getTree(String principal, String subtreeUri) throws DmtException;

	/**
	 * Opens a DmtSession on a specific DMT subtree using a specific locking
	 * mode.
	 * 
	 * @param principal the identifier of the remote server on whose behalf the
	 *        data manipulation is performed, or <code>null</code> for local
	 *        sessions
	 * @param subtreeUri The subtree on which DMT manipulations can be performed
	 *        within the returned session
	 * @param lockMode One of the locking modes specified in DmtSession
	 * @return a DmtSession object on which DMT manipulations can be performed
	 * @throws DmtException
	 */
	DmtSession getTree(String principal, String subtreeUri, int lockMode)
			throws DmtException;
}
