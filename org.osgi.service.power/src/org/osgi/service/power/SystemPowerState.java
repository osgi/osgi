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