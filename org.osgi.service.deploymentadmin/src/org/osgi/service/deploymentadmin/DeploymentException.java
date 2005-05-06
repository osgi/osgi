/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.service.deploymentadmin;

/**
 * Checked exception received when something fails during any deployment processes. 
 * Beside the exception message, a <code>DeploymentException</code> always contains 
 * an error code (one of the constants specified in this class), and may optionally 
 * contain the textual description of the error condition and a nested cause 
 * exception.<p>
 */
public class DeploymentException extends Exception {
    
    /**
     * The manifest is not the first file in the stream or bundles don't 
     * precede resource files.
     */
    public static final int CODE_ORDER_ERROR                = 0;
    
    /**
     * Missing mandatory manifest header.
     */
    public static final int CODE_MISSING_HEADER             = 1;
    
    /**
     * Syntax error in any manifest header. 
     */
    public static final int CODE_BAD_HEADER                 = 2;
    
    /**
     * Fix pack version range doesn't fit to the version of the target 
     * deployment package or the target deployment package of the fix pack 
     * doesn't exist.
     */
    public static final int CODE_MISSING_FIXPACK_TARGET     = 3;
    
    /**
     * A bundle in the deployment package is marked as DeploymentPackage-Missing 
     * but there is no such bundle in the target deployment package.
     */
    public static final int CODE_MISSING_BUNDLE             = 4;
    
    /**
     * A resource in the deployment package is marked as DeploymentPackage-Missing
     * but there is no such resource in the target deployment package.
     */
    public static final int CODE_MISSING_RESOURCE           = 5;
    
    /**
     * Bad deployment package signing.
     */
    public static final int CODE_SIGNING_ERROR              = 6;
    
    /**
     * Bundle symbolic name is not the same as defined by the deployment package 
     * manifest.
     */
    public static final int CODE_BUNDLE_NAME_ERROR          = 7;
    
    /**
     * Matched resource processor service is a customizer from another  
     * deployment package.
     */
    public static final int CODE_FOREIGN_CUSTOMIZER         = 8;
    
    /**
     * The <code>dropped(String resource)</code> method was called on the matched 
     * resource processor but the resource processor doesn't manage this resource.
     */
    public static final int CODE_NO_SUCH_RESOURCE           = 9;
    
    /**
     * Bundle with the same symbolic name alerady exists.
     */
    public static final int CODE_BUNDLE_SHARING_VIOLATION   = 10;
    
    /**
     * A side effect of any resource already exists.
     */
    public static final int CODE_RESOURCE_SHARING_VIOLATION = 11;
    
    /**
     * Resource processors are allowed to raise such exceptions that 
     * indicates that the processor is not able to commit the operations 
     * it made since the last call of <code>begin</code> method.
     */
    public static final int CODE_PREPARE                    = 12;
    
    /**
     * Other error condition.
     */
    public static final int CODE_OTHER_ERROR                = 13;

    private int       code;
    private String    message;
    private Throwable cause;

    /**
     * Create an instance of the exception.
     * @param code    The error code of the failure. Code should be one of the 
     *                predefined integer values (<code>CODE_X</code>). 
     * @param message Message associated with the exception
     * @param causes  the originating exception
     */
    public DeploymentException(int code, String message, Throwable cause) {
        this.code = code;
        this.message = message;
        this.cause = cause;
    }

    /**
     * Create an instance of the exception. Cause exception is implicitly set 
     * to null.
     * @param code The error code of the failure.  Code should be one of the 
     *             predefined integer values (<code>CODE_X</code>).
     * @param message Message associated with the exception
     */
    public DeploymentException(int code, String message) {
        this(code, message, null);
    }

    /**
     * Create an instance of the exception. Cause exception and message are 
     * implicitly set to null.
     * @param code The error code of the failure. Code should be one of the 
     *             predefined integer values (<code>CODE_X</code>).
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
        return message;
    }
}
