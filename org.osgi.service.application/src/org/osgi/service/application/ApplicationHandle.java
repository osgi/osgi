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

package org.osgi.service.application;

import org.osgi.framework.*;

/** @modelguid {0967B96E-F06C-4EA1-96E7-3263670F4F49} */
public interface ApplicationHandle {
	
    /**
     * The application instance is running
     * @modelguid {8EBD44E3-883B-4515-8EEA-8469F6F16408}
     */
    public final static int RUNNING = 0;

    /**
     * The application instance is being suspended
     * @modelguid {029AC4AE-AFCA-4F5D-88D9-65819DD39B50}
     */
    public final static int SUSPENDING = 1;

    /**
     * The application instance has been suspended
     * @modelguid {2EA96AB2-D802-43F6-8162-00F3DEF1E44B}
     */
    public final static int SUSPENDED = 2;

    /**
     * The application instance is being resumed. Status 'resumed' is equivalent to status 'running'
     * @modelguid {9584DD96-7B92-40FA-A00A-5F2A3E78F49F}
     */
    public final static int RESUMING = 3;

    /**
     * The application instance is being stopped. Status 'stopped' is equivalent to status 'nonexistent'
     * @modelguid {1C7AC1F5-2833-46CA-A908-8FC4E797ACAC}
     */
    public final static int STOPPING = 4;

    /**
     * The application instance does not exist. Either an instance
     * with the ID is never created .
     * @modelguid {541E1C88-4DAD-47B1-AAB5-A523BF9AD01E}
     */
    public final static int NONEXISTENT = 5;    

    /**
     * Get the status (constants defined in the Application class) of the application instance
     * @modelguid {8C7D95E9-A8E2-40F1-9BFD-C55A5B80148F}
     */
    public int getAppStatus();
    
	/** @modelguid {A8CFA5DA-8F7E-49B7-BA5A-42EDDA6D6B59} */
    public ServiceReference getAppDescriptor();
    
	/** @modelguid {CEAB58E4-91B8-4E7A-AEEB-9C14C812E607} */
    public void destroyApplication() throws Exception;

	/** @modelguid {2C42D0DC-BC50-44C6-9B8E-687ABC58D281} */
    public void suspendApplication() throws Exception;

	/** @modelguid {E4CBC4A3-38F9-41D4-81D3-331D0AE5DCA9} */
    public void resumeApplication() throws Exception;    
}
