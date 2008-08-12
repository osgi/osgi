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
package org.osgi.impl.service.metatype2;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @version $Revision$
 */
public class Logger {
  
  private ServiceTracker logTracker;
  
  public Logger(BundleContext bc) {
    logTracker = new ServiceTracker(bc, LogService.class.getName(), null);
    logTracker.open();
  }
  
  public void close() {
    logTracker.close();
  }
  
  public void logDebug(String message) {
    log(null, LogService.LOG_DEBUG, message, null);
  }
  
  public void logError(String message) {
    log(null, LogService.LOG_ERROR, message, null);
  }
  
  public void logError(String message, Throwable exception) {
    log(null, LogService.LOG_ERROR, message, exception);
  }
  
  public void logError(String message, ServiceReference reference) {
    log(reference, LogService.LOG_ERROR, message, null);
  }
  
  public void logError(String message, ServiceReference reference, Throwable exception) {
    log(reference, LogService.LOG_ERROR, message, exception);
  }
  
  private void log(ServiceReference reference, int level, String message, Throwable exception) {
    LogService logService = (LogService)logTracker.getService();
    
    logService.log(reference, level, message, exception);
  }
}
