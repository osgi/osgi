/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
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
 */
package org.osgi.meglet;

import org.osgi.service.application.*;


/**
 * This service represents a Meglet instance. It is a specialization of the
 * application handle and provides features specific to the Meglet model.
 */
public abstract class MegletHandle extends ApplicationHandle {

	/**
	 * The Meglet instance is suspended.
	 * 
	 * @modelguid {8EBD44E3-883B-4515-8EEA-8469F6F16408}
	 */
	public  final static int 		SUSPENDED = 2;

	/**
	 * Suspends the Melet instance. It calls the associated Meglet instance's
	 * stop() method and passes a non-null output stream as a parameter. It must
	 * preserve the contents of the output stream written by the Meglet instance
	 * even across device restarts. The same content must be provided to a
	 * resuming Meglet instance.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "manipulate"
	 *             ApplicationAdminPermission for the corresponding application.
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 */
	public abstract void suspend() throws Exception;

	/**
	 * Resumes the Meglet instance. It calls the associated Meglet instance's
	 * start() method and passes a non-null input stream as a parameter. It must
	 * have the same contents that was saved by the suspending Meglet instance.
	 * The same startup arguments must also be passed to the resuming Meglet
	 * instance that was passed to the first starting instance.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "manipulate"
	 *             ApplicationAdminPermission for the corresponding application.
	 * @throws IllegalStateException
	 *             if the Meglet handle is unregistered
	 */
	public abstract void resume() throws Exception;
}
