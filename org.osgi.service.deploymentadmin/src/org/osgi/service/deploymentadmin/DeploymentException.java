/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.deploymentadmin;

/**
 * Checked exception received when something fails during any deployment
 * processes. Beside the exception message, a <code>DeploymentException</code>
 * always contains an error code (one of the constants specified in this class),
 * and may optionally contain the textual description of the error condition and
 * a nested cause exception.
 * <p>
 */
public class DeploymentException extends Exception {

	/**
	 * Missing mandatory manifest header.
	 */
	public static final int	CODE_MISSING_HEADER				= 1;

	/**
	 * Syntax error in any manifest header.
	 */
	public static final int	CODE_BAD_HEADER					= 2;

	/**
	 * Fix pack version range doesn't fit to the version of the target
	 * deployment package or the target deployment package of the fix pack
	 * doesn't exist.
	 */
	public static final int	CODE_MISSING_FIXPACK_TARGET		= 3;

	/**
	 * A bundle in the deployment package is marked as DeploymentPackage-Missing
	 * but there is no such bundle in the target deployment package.
	 */
	public static final int	CODE_MISSING_BUNDLE				= 4;

	/**
	 * A resource in the deployment package is marked as
	 * DeploymentPackage-Missing but there is no such resource in the target
	 * deployment package.
	 */
	public static final int	CODE_MISSING_RESOURCE			= 5;

	/**
	 * Bad deployment package signing.
	 */
	public static final int	CODE_SIGNING_ERROR				= 6;

	/**
	 * Bundle symbolic name is not the same as defined by the deployment package
	 * manifest.
	 */
	public static final int	CODE_BUNDLE_NAME_ERROR			= 7;

	/**
	 * Matched resource processor service is a customizer from another
	 * deployment package.
	 */
	public static final int	CODE_FOREIGN_CUSTOMIZER			= 8;

	/**
	 * The <code>dropped(String resource)</code> method was called on the
	 * matched resource processor but the resource processor doesn't manage this
	 * resource.
	 */
	public static final int	CODE_NO_SUCH_RESOURCE			= 9;

	/**
	 * Bundle with the same symbolic name alerady exists.
	 */
	public static final int	CODE_BUNDLE_SHARING_VIOLATION	= 10;

	/**
	 * A side effect of any resource already exists.
	 */
	public static final int	CODE_RESOURCE_SHARING_VIOLATION	= 11;

	/**
	 * Resource processors are allowed to raise such exceptions that indicates
	 * that the processor is not able to commit the operations it made since the
	 * last call of <code>begin</code> method.
	 */
	public static final int	CODE_PREPARE					= 12;

	/**
	 * The Resource Processor service with the given PID (see
	 * <code>Resource-Processor</code> manifest header) is not found.
	 */
	public static final int	CODE_PROCESSOR_NOT_FOUND		= 13;

	/**
	 * The manifest is not the first file in the stream or bundles don't precede
	 * resource files.
	 */
	public static final int	CODE_ORDER_ERROR				= 14;

	/**
	 * When a client requests a new session with an install or uninstall
	 * operation, it must block that call until the earlier session is
	 * completed. The Deployment Admin service must throw a Deployment Exception
	 * with this code when the session can not be created after an appropriate
	 * time out period.
	 */
	public static final int	CODE_TIMEOUT					= 15;

	/**
	 * It is only a hint that one or more bundles couldn't be started. However
	 * the Deployment Package install/update was not rolled back.
	 */
	public static final int	CODE_BUNDLE_START				= 16;

	/**
	 * Other error condition.
	 */
	public static final int	CODE_OTHER_ERROR				= 0;

	private int				code;
	private String			message;
	private Throwable		cause;

	/**
	 * Create an instance of the exception.
	 * 
	 * @param code The error code of the failure. Code should be one of the
	 *        predefined integer values (<code>CODE_X</code>).
	 * @param message Message associated with the exception
	 * @param causes the originating exception
	 */
	public DeploymentException(int code, String message, Throwable cause) {
		this.code = code;
		this.message = message;
		this.cause = cause;
	}

	/**
	 * Create an instance of the exception. Cause exception is implicitly set to
	 * null.
	 * 
	 * @param code The error code of the failure. Code should be one of the
	 *        predefined integer values (<code>CODE_X</code>).
	 * @param message Message associated with the exception
	 */
	public DeploymentException(int code, String message) {
		this(code, message, null);
	}

	/**
	 * Create an instance of the exception. Cause exception and message are
	 * implicitly set to null.
	 * 
	 * @param code The error code of the failure. Code should be one of the
	 *        predefined integer values (<code>CODE_X</code>).
	 */
	public DeploymentException(int code) {
		this(code, null, null);
	}

	/**
	 * @return Returns the cause.
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * @return Returns the code.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		if (getCause() != null)
			return message + " : " + getCause().getMessage();
		else
			return message;
	}
}
