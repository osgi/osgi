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
 * The device power states are defined according to the ACPI specification. 
 * This is mainly because in the ACPI specification, Intel and Microsoft have 
 * already established an industry standard for USB, PCI, FireWireTM1394 and 
 * other various devices, and the power management characteristics for these 
 * buses and devices are also part of the standard. In addition, the set of 
 * ACPI device power states is also generic enough to support legacy devices. 
 * This list can be extended or completely changed because only int values are
 * used in the rest of the specification, there is no direct dependency to this
 * interface. 
 * This interface proposes a first set of values which is: D0, D1, D2 and D3. 
 */
public interface DevicePowerState 
{
   
    /**
     * Device is fully on, completely active and responsive to application 
     * requests. Devices in this state consume the most power but have maximum 
     * performance. Devices are expected to continuously remember all relevant 
     * device context when operating in the D0 state.
     */
    public static final int D0 = 1;
    
    /**
     * Device is being power managed. The meaning of D1 state is device 
     * specific. Device may respond to application requests, but the response 
     * time may not be instantaneous. In general, D1 is expected to consume 
     * less power than D0 state and preserve more device context than D2. 
     * This state may not be defined by many devices.
     */
    public static final int D1 = 2;
    
    /**
     * Device is being power managed. The meaning of D2 state is device specific. 
      * Device may respond to application requests but the response time may not 
      * be instantaneous, and may have higher response latency than the D1 state. 
      * In general, D2 is expected to consume less power than D1 and preserve less 
      * device context than D1. This state may not be defined by many devices.
     */
    public static final int D2 = 3;
    
    /**
     * Device is fully off, there is no power to the device. When a device 
     * enters the D3 state, device context is lost. Device is no longer 
     * responsive to application requests. When the device is powered back on, 
     * OS software needs to re-initialize the device before it can service 
     * application requests.
     */
    public static final int D3 = 4;    
}
