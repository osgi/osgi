/*
 * Copyright (c) OSGi Alliance 2002.
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.startlevel.tb3;

import org.osgi.framework.*;
import org.osgi.service.startlevel.*;
import org.osgi.test.service.*;

public class Activator implements BundleActivator
{

  ServiceReference loggerR;
  TestLogger logger;
  ServiceReference slR;
  StartLevel sl;

  public void start(BundleContext bc) 
  {
    loggerR = bc.getServiceReference(TestLogger.class.getName());
    if (loggerR == null) {
      System.out.println("tb3: failed to get TestLogger service reference");
      return;
    }
    logger = (TestLogger)bc.getService(loggerR);
    if (logger == null) {
      System.out.println("tb3: failed to get TestLogger service");
      return;
    }

    slR = bc.getServiceReference(StartLevel.class.getName());
    if (slR == null) {
      logger.log("tb3: failed to get StartLevel servce reference");
      return;
    }

    sl = (StartLevel)bc.getService(slR);
    if (sl == null) {
    	logger.log("tb3: failed to get StartLevel servce reference");
      return;
    }

    Bundle b = bc.getBundle();
    int bundleStartLevel = sl.getBundleStartLevel(b);
    sl.setBundleStartLevel(b, bundleStartLevel + 10);
  }

  public void stop(BundleContext bc)
  {
  }
}
