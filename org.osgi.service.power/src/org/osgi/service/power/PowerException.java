/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
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

package org.osgi.service.power;

/**
 * Thrown when a power management related error is encountered. Different
 * errors can be identified by the error code. Method getErrCode can be used to
 * retrieve the error that causes the exception. Error code
 * ILLEGAL_STATE_TRANSITION_REQUEST, STATE_TRANSITION_FAILURE, must be used by
 * <code>DevicePower</code> or <code>SystemPower</code> services to indicate power state related errors. Error
 * code KEEP_CURRENT_STATE should be used by the PowerStateEvent listener when
 * the listener wants to oppose the current state change event.
 */
public class PowerException extends Exception
{
    /**
     * Indicates that the power state change request is illegal
     */
    public static final int ILLEGAL_STATE_TRANSITION_REQUEST = 0;
    /**
     * Indicates that the power state change failed
     */
    public static final int STATE_TRANSITION_FAILURE = 1;
    /**
     * Indicates that the power state change is opposed
     */
    public static final int KEEP_CURRENT_STATE = 2;
    private int error;

    /**
     * Creates a new <code>PowerException</code> object with an error code.
     * 
     * @param error the error code that is used to to create the exception
     * object.
     * @throws IllegalArgumentException An <code>IllegalArgumentException</code> 
     * is thrown for an unrecognized error code.
     */
    public PowerException(int error) throws IllegalArgumentException
    {
        this(error, null);
    }

    /**
     * Create a new <code>PowerException</code> object with an error code and a
     * detailed message.
     * 
     * @param error the error code that is used to create the exception object.
     * @param msg the detailed message
     * @throws IllegalArgumentException An <code>IllegalArgumentException</code> 
     * is thrown for an unrecognized error code.
     */
    public PowerException(int error, String msg)
            throws IllegalArgumentException
    {
        super(msg);
        checkError(error);
        this.error = error;
    }

    /**
     * Returns the exception error code.
     * 
     * @return the error code that is used to construct the exception object.
     */
    public int getErrorCode()
    {
        return error;
    }

    /**
     * Check if the parameter is a valid error constant
     * 
     * @param err an error code
     * @throws IllegalArgumentException thrown for an unrecognized error code.
     */
    private void checkError(int error) throws IllegalArgumentException
    {
        if (error < ILLEGAL_STATE_TRANSITION_REQUEST
                || error > KEEP_CURRENT_STATE)
        {
            throw new IllegalArgumentException();
        }
    }
}