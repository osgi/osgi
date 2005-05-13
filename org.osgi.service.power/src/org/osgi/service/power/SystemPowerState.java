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
 * This class defines the standard System Power States that are specified 
 * in the MNCRS Java Application Level Power Management Specification. 
 * Please refer to the MNCRS Java Application Level Power Management 
 * Specification for a detailed explanation of the power states.
 * This list can be extended or completely changed because power state is an int
 * value and there is no direct dependency to this interface.
 * This interface defines the first set of possible values which is:
 * Full Power, PM Active, Sleep, Suspend and Off. 
 */
public interface SystemPowerState
{
    /**
     * System is shutdown and is turned off. 
     */ 
    public static final int OFF = 1;    

    /**
     * System is not operational, memory state is saved in high latency 
     * persistent storage that does not require power. 
     */
    public static final int SUSPEND = 2;

    /**
     * System is not operational, memory state is saved in low latency 
     * persistent storage that may require power. 
     */
    public static final int SLEEP = 3;
    
    /**
     * System is operational but power consumption is regulated by power 
     * management software. 
     */
    public static final int PM_ACTIVE = 4;
     
    /**
     * System is running at its maximum possible performance. 
     */
    public static final int FULL_POWER = 5;    
}