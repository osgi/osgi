/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.power;

/**
 * Thrown when a power management related error is encountered. Different
 * errors can be identified by the error code. Method <code>getErrCode()</code> 
 * can be used to retrieve the error that causes the exception. Error code
 * ILLEGAL_STATE_TRANSITION_REQUEST, STATE_TRANSITION_FAILURE, must be used by
 * <code>DevicePower</code> or <code>SystemPower</code> services to indicate 
 * power state related errors. Error code KEEP_CURRENT_STATE should be used 
 * by the PowerStateEvent listener when the listener wants to oppose the current 
 * state change.
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