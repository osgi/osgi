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

package org.osgi.test.cases.startlevel.tbc;

import org.osgi.framework.*;
import org.osgi.service.startlevel.*;
import org.osgi.test.cases.util.*;

public class StartLevelControl extends DefaultTestBundleControl implements FrameworkListener, BundleListener
{

  private StartLevel sl;
  private boolean logStartLevelChanged = false;
  private int ibsl;
  private int origSl;
  private int sl_4;
  private int sl_5;
  private int sl_6;
  private int sl_10;
  private int sl_15;
  private int sl_20;
  
  private int SLEEP = 2000;
  
  private int TIMEOUT = 600000; // 10 min.
  private boolean isEventReceived;
  private boolean toNotify;
  private Object synch;
  private Bundle eventBundle;
  private int eventMask;

  String methods[] = {
		"testInitialBundleStartLevel",
		"testStartOrder",
		"testSetStartLevel",
		"testSetBundleStartLevel",
		"testPersistentlyStarted",
		"testEvents",
		"testSystemBundle",
		"testExceptionInActivator",
		"testActivator",
	};
	
	public String [] getMethods() { return methods; }
	
  public boolean checkPrerequisites()
  {
    return true;
  }

  public void prepare() throws Exception
  {
    String sleepTimeString = System.getProperty("osgi.tc.startlevel.sleeptime");
    int sleepTime = SLEEP;
    if (sleepTimeString != null) {
      try {
        sleepTime = Integer.parseInt(sleepTimeString);
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error while parsing sleep value! The default one will be used : "+SLEEP);
      }
      if (sleepTime < 200) {
        System.out.println("The sleep value is too low : "+sleepTime+" ! The default one will be used : "+SLEEP);
      } else {
        SLEEP = sleepTime;
      }
    }
    sleepTimeString = System.getProperty("osgi.tc.startlevel.timeout");
    if (sleepTimeString != null) {
      try {
        TIMEOUT = Integer.parseInt(sleepTimeString);
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error while parsing timeout value! The default one will be used : " + TIMEOUT);
      }
    }
    synch = new Object();
    eventMask = 0;
    eventBundle = null;
    toNotify = false;
    
    sl = (StartLevel)getService(StartLevel.class);
    ibsl = sl.getInitialBundleStartLevel();
    origSl = sl.getStartLevel();
    int min = ibsl > origSl ? ibsl : origSl;
    sl_4 = min + 4;
    sl_5 = min + 5;
    sl_6 = min + 6;
    sl_10 = min + 10;
    sl_15 = min + 15;
    sl_20 = min + 20;
    getContext().addFrameworkListener(this);
    getContext().addBundleListener(this);
  }

  public void unprepare() throws Exception {
    getContext().removeBundleListener(this);
    getContext().removeFrameworkListener(this);
    sl.setInitialBundleStartLevel(ibsl);
	  sl.setStartLevel(origSl);
  }
  public void setState() throws Exception {}
  public void clearState() throws Exception {}

  public void testInitialBundleStartLevel() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_20);
    assertEquals("getInitialBundleStartLevel", sl_20,  sl.getInitialBundleStartLevel());

    isEventReceived = false;
    sl.setStartLevel(sl_10);
    if (!isEventReceived(true)) return;

    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    tb1.start();
    assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    sl.setInitialBundleStartLevel(sl_10);
    Bundle tb2 = getContext().installBundle(getWebServer() + "tb2.jar");
    tb2.start();
    assertEquals("getState() = ACTIVE", true, inState(tb2, Bundle.ACTIVE));

    tb1.uninstall();
    tb2.stop();
    tb2.uninstall();
  }


  public void testStartOrder() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_20);
    isEventReceived = false;
    sl.setStartLevel(sl_10);
    if (!isEventReceived(true)) return;
    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    tb1.start();
    Bundle tb2 = getContext().installBundle(getWebServer() + "tb2.jar");
    tb2.start();
    try {
      //start tb1 and tb2
      isEventReceived = false;
      sl.setStartLevel(sl_20);
      if (!isEventReceived(true)) return;

      //stop tb2 and tb1
      isEventReceived = false;
      sl.setStartLevel(sl_10);
      if (!isEventReceived(true)) return;

      //reverse the start order
      sl.setBundleStartLevel(tb2, sl_15);
      Thread.sleep(SLEEP);

      //start tb2 and tb1
      isEventReceived = false;
      sl.setStartLevel(sl_20);
      if (!isEventReceived(true)) return;

      //stop tb1 and tb2
      isEventReceived = false;
      sl.setStartLevel(sl_10);
      if (!isEventReceived(true)) return;
    } finally {
      tb1.uninstall();
      tb2.uninstall();
    }
  }

  public void testSetStartLevel() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_15);
    isEventReceived = false;
    sl.setStartLevel(sl_10);
    if (!isEventReceived(true)) return;
    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    try {
      isEventReceived = false;
      sl.setStartLevel(sl_20);
      if (!isEventReceived(true)) return;
        assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      isEventReceived = false;
      sl.setStartLevel(sl_10);
      if (!isEventReceived(true)) return;
        assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      tb1.start();
        assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      isEventReceived = false;
      sl.setStartLevel(sl_20);
      if (!isEventReceived(true)) return;
        assertEquals("getState() = ACTIVE", true, inState(tb1, Bundle.ACTIVE));

      isEventReceived = false;
      sl.setStartLevel(sl_10);
      if (!isEventReceived(true)) return;
        assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      tb1.stop();
      isEventReceived = false;
      sl.setStartLevel(sl_20);
      if (!isEventReceived(true)) return;
        assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));
    } finally {
      tb1.uninstall();
    }
  }

  public void testSetBundleStartLevel() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_15);
    isEventReceived = false;
    sl.setStartLevel(sl_10);
    if (!isEventReceived(true)) return;
    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    try {
      sl.setBundleStartLevel(tb1, sl_5);
      Thread.sleep(SLEEP);
      assertEquals("Startlevel 10/5 stop", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      sl.setBundleStartLevel(tb1, sl_15);
      Thread.sleep(SLEEP);
      assertEquals("StartLevel 10/15 stop", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      tb1.start();
      assertEquals("StartLevel 10/15 start", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      isEventReceived = false;
      eventMask = BundleEvent.STARTED;
      eventBundle = tb1;
      sl.setBundleStartLevel(tb1, sl_5);
      if (!isEventReceived(false)) return;
      assertEquals("StartLevel 10/5 start", true, inState(tb1, Bundle.ACTIVE));

      isEventReceived = false;
      eventMask = BundleEvent.STOPPED | BundleEvent.UNRESOLVED;
      sl.setBundleStartLevel(tb1, sl_15);
      if (!isEventReceived(false)) return;
      assertEquals("StartLevel 10/15 start", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      isEventReceived = false;
      eventMask = BundleEvent.STARTED;
      sl.setBundleStartLevel(tb1, sl_5);
      if (!isEventReceived(false)) return;
      assertEquals("StartLevel 10/5 start", true, inState(tb1, Bundle.ACTIVE));
    } finally {
      eventBundle = null;
      tb1.stop();
      tb1.uninstall();
    }
  }

  public void testPersistentlyStarted() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_15);
    assertEquals("setInitialBundleStartLevel", sl_15,  sl.getInitialBundleStartLevel());

    isEventReceived = false;
    sl.setStartLevel(sl_10);
    if (!isEventReceived(true)) return;

    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    try {
      assertEquals("isBundlePersistentlyStarted", false, sl.isBundlePersistentlyStarted(tb1));

      tb1.start();
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

      isEventReceived = false;
      sl.setStartLevel(sl_20);
      if (!isEventReceived(true)) return;
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

      isEventReceived = false;
      sl.setStartLevel(sl_10);
      if (!isEventReceived(true)) return;
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

      isEventReceived = false;
      eventMask = BundleEvent.STARTED;
      eventBundle = tb1;
      sl.setBundleStartLevel(tb1, sl_5);
      if (!isEventReceived(false)) return;
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

      isEventReceived = false;
      eventMask = BundleEvent.STOPPED | BundleEvent.UNRESOLVED;
      sl.setBundleStartLevel(tb1, sl_15);
      if (!isEventReceived(false)) return;
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));
    } finally {
      eventBundle = null;
      tb1.uninstall();
    }
  }

  public void testEvents() throws Exception
  {
    logStartLevelChanged = true;
    isEventReceived = false;
    sl.setStartLevel(sl_10);
    if (!isEventReceived(true)) return;
    isEventReceived = false;
    sl.setStartLevel(sl_20);
    if (!isEventReceived(true)) return;
    logStartLevelChanged = false;
  }

  public void testSystemBundle() throws Exception
  {
    Bundle systemBundle = getContext().getBundle(0);
    assertEquals("getBundleStartLevel", 0,  sl.getBundleStartLevel(systemBundle));
    try {
      sl.setBundleStartLevel(systemBundle, 42);
      log("got no IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
    }
  }


  public void testExceptionInActivator() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_5);
    isEventReceived = false;
    sl.setStartLevel(sl_10);
    if (!isEventReceived(true)) return;
    Bundle tb5 = getContext().installBundle(getWebServer() + "tb5.jar");

    try {
      tb5.start();
        assertEquals("getState() = ACTIVE", true, inState(tb5, Bundle.ACTIVE));

    } catch (Exception e) {
    }
    try {
      //FrameworkEvent.ERROR due to active startlevel change
      isEventReceived = false;
      sl.setStartLevel(sl_4);
      if (!isEventReceived(true)) return;
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb5, Bundle.INSTALLED | Bundle.RESOLVED));

      //no FrameworkEvent.ERROR
      isEventReceived = false;
      sl.setStartLevel(sl_5);
      if (!isEventReceived(true)) return;
      assertEquals("getState() = ACTIVE", true, inState(tb5, Bundle.ACTIVE));

      
      //FrameworkEvent.ERROR due to bundle startlevel change
      isEventReceived = false;
      toNotify = true;
      sl.setBundleStartLevel(tb5, sl_6);
      if (!isEventReceived(true)) return;
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb5, Bundle.INSTALLED | Bundle.RESOLVED));
    } finally {
      tb5.uninstall();
      toNotify = false;
    }
  }

  public void testActivator() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_5);
    isEventReceived = false;
    sl.setStartLevel(sl_10);
    if (!isEventReceived(true)) return;
    Bundle tb3 = getContext().installBundle(getWebServer() + "tb3.jar");
    try {
      isEventReceived = false;
      eventMask = BundleEvent.STOPPED | BundleEvent.UNRESOLVED;
      eventBundle = tb3;
      tb3.start();
      if (!isEventReceived(false)) return;
      assertEquals("getBundleStartLevel", sl_15,  sl.getBundleStartLevel(tb3));
    } finally {
      eventBundle = null;
      tb3.uninstall();
    }

    Bundle tb4 = getContext().installBundle(getWebServer() + "tb4.jar");
    try {
      isEventReceived = false;
      tb4.start();
      if (!isEventReceived(true)) return;
      assertEquals("getStartLevel", sl_15,  sl.getStartLevel());

      isEventReceived = false;
      tb4.stop();
      if (!isEventReceived(true)) return;
      assertEquals("getStartLevel", sl_10,  sl.getStartLevel());
    } finally {
      tb4.uninstall();
    }
  }

  public void frameworkEvent(FrameworkEvent event)
  {
    switch (event.getType()) {
    case FrameworkEvent.ERROR:
    	if ( event.getThrowable() != null ) {
    		log("got framework event " + event.getType() + ": " + event.getThrowable().getClass().getName());
    		event.getThrowable().printStackTrace();
    	} else {
    		log("got framework event " + event.getType()  );
    	}
      synchronized (synch) {
        if (toNotify) {
          isEventReceived = true;
          synch.notify();
        }
      }
      break;
    case FrameworkEvent.STARTLEVEL_CHANGED:
      if (logStartLevelChanged)
        log("got framework event " + event.getType());
      synchronized (synch) {
        isEventReceived = true;
        synch.notify();
      }
      break;
    }
  }
	
  private boolean isEventReceived(boolean isFrameworkEvent) throws InterruptedException {
    synchronized (synch) {
      if (!isEventReceived) {
        synch.wait(TIMEOUT);
      }
      if (!isEventReceived) {
        if (isFrameworkEvent) {
          log("The framework event is not received. Maybe the timeout is not enough.");
        } else {
          log("The bundle event is not received. Maybe the timeout is not enough.");
        }
        return false;
      }
    }
    return true;
  }
  
  public void bundleChanged(BundleEvent e) {
    synchronized (synch) {
      if (eventBundle == e.getBundle() && ((eventMask & e.getType()) != 0)) {
        isEventReceived = true;
        eventMask = 0;
        synch.notify();
      }
    }
  }

  boolean inState(Bundle b, int stateMask) {
		return (b.getState() & stateMask) != 0;
	}
}

