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
 * A collection of constants describing the possible formats of a DMT node.
 */
public interface DmtDataType {
	/**
	 * The node holds an integer value. Note that this does not correspond to
	 * the Java <code>int</code> type, OMA DM integers are unsigned.
	 */
	static final int	INTEGER	= 1;
	/**
	 * The node holds an OMA DM <code>chr</code> value.
	 */
	static final int	STRING	= 2;
	/**
	 * The node holds an OMA DM <code>bool</code> value.
	 */
	static final int	BOOLEAN	= 3;
	/**
	 * The node holds an OMA DM <code>binary</code> value. The value of the
	 * node corresponds to the Java <code>byte[]</code> type.
	 */
	static final int	BINARY	= 4;
	/**
	 * The node holds an OMA DM <code>xml</code> value.
	 */
	static final int	XML		= 5;
	/**
	 * The node holds an OMA DM <code>null</code> value. This corresponds to
	 * the Java <code>null</code> type.
	 */
	static final int	NULL	= 6;
	/**
	 * The node is an internal node.
	 */
	static final int	NODE	= 7;
}
