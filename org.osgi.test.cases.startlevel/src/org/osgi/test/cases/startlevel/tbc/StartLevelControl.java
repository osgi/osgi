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

public class StartLevelControl extends DefaultTestBundleControl implements FrameworkListener
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
  
  private int SLEEP = 1000;

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
  }

  public void unprepare() throws Exception {
	  sl.setInitialBundleStartLevel(ibsl);
	  sl.setStartLevel(origSl);
  }
  public void setState() throws Exception {}
  public void clearState() throws Exception {}

  public void testInitialBundleStartLevel() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_20);
    assertEquals("getInitialBundleStartLevel", sl_20,  sl.getInitialBundleStartLevel());

    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);

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
    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);
    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    tb1.start();
    Bundle tb2 = getContext().installBundle(getWebServer() + "tb2.jar");
    tb2.start();

    //start tb1 and tb2
    sl.setStartLevel(sl_20);
    Thread.sleep(SLEEP);

    //stop tb2 and tb1
    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);

    //reverse the start order
    sl.setBundleStartLevel(tb2, sl_15);
    Thread.sleep(SLEEP);

    //start tb2 and tb1
    sl.setStartLevel(sl_20);
    Thread.sleep(SLEEP);

    //stop tb1 and tb2
    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);

    tb1.uninstall();
    tb2.uninstall();
  }

  public void testSetStartLevel() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_15);
    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);
    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");

    sl.setStartLevel(sl_20);
    Thread.sleep(SLEEP);
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    tb1.start();
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    sl.setStartLevel(sl_20);
    Thread.sleep(SLEEP);
      assertEquals("getState() = ACTIVE", true, inState(tb1, Bundle.ACTIVE));

    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    tb1.stop();
    sl.setStartLevel(sl_20);
    Thread.sleep(SLEEP);
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    tb1.uninstall();
  }

  public void testSetBundleStartLevel() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_15);
    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);
    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");

    sl.setBundleStartLevel(tb1, sl_5);
    Thread.sleep(SLEEP*2);
    assertEquals("Startlevel 10/5 stop", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    sl.setBundleStartLevel(tb1, sl_15);
    Thread.sleep(SLEEP*2);
    assertEquals("StartLevel 10/15 stop", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    tb1.start();
    assertEquals("StartLevel 10/15 start", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    sl.setBundleStartLevel(tb1, sl_5);
    Thread.sleep(SLEEP*2);
    assertEquals("StartLevel 10/5 start", true, inState(tb1, Bundle.ACTIVE));

    sl.setBundleStartLevel(tb1, sl_15);
    Thread.sleep(SLEEP*2);
    assertEquals("StartLevel 10/15 start", true, inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

    sl.setBundleStartLevel(tb1, sl_5);
    Thread.sleep(SLEEP*2);
    assertEquals("StartLevel 10/5 start", true, inState(tb1, Bundle.ACTIVE));

    tb1.stop();
    tb1.uninstall();
  }

  public void testPersistentlyStarted() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_15);
    assertEquals("setInitialBundleStartLevel", sl_15,  sl.getInitialBundleStartLevel());

    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);

    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    assertEquals("isBundlePersistentlyStarted", false, sl.isBundlePersistentlyStarted(tb1));

    tb1.start();
    assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

    sl.setStartLevel(sl_20);
    Thread.sleep(SLEEP);
    assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);
    assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

    sl.setBundleStartLevel(tb1, sl_5);
    Thread.sleep(SLEEP*2);
    assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

    sl.setBundleStartLevel(tb1, sl_15);
    Thread.sleep(SLEEP*2);
    assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

    tb1.uninstall();
  }

  public void testEvents() throws Exception
  {
    logStartLevelChanged = true;
    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP*2);
    sl.setStartLevel(sl_20);
    Thread.sleep(SLEEP*2);
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
    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);
    Bundle tb5 = getContext().installBundle(getWebServer() + "tb5.jar");

    try {
      tb5.start();
        assertEquals("getState() = ACTIVE", true, inState(tb5, Bundle.ACTIVE));

    } catch (Exception e) {
    }

    //FrameworkEvent.ERROR due to active startlevel change
    sl.setStartLevel(sl_4);
    Thread.sleep(SLEEP);
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb5, Bundle.INSTALLED | Bundle.RESOLVED));

    //no FrameworkEvent.ERROR
    sl.setStartLevel(sl_5);
    Thread.sleep(SLEEP);
      assertEquals("getState() = ACTIVE", true, inState(tb5, Bundle.ACTIVE));

    
    //FrameworkEvent.ERROR due to bundle startlevel change
    sl.setBundleStartLevel(tb5, sl_6);
    Thread.sleep(SLEEP*2);
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb5, Bundle.INSTALLED | Bundle.RESOLVED));

    tb5.uninstall();
  }

  public void testActivator() throws Exception
  {
    sl.setInitialBundleStartLevel(sl_5);
    sl.setStartLevel(sl_10);
    Thread.sleep(SLEEP);
    Bundle tb3 = getContext().installBundle(getWebServer() + "tb3.jar");
    tb3.start();
    Thread.sleep(SLEEP*2);
    assertEquals("getBundleStartLevel", sl_15,  sl.getBundleStartLevel(tb3));
    tb3.uninstall();

    Bundle tb4 = getContext().installBundle(getWebServer() + "tb4.jar");
    tb4.start();
    Thread.sleep(SLEEP);
    assertEquals("getStartLevel", sl_15,  sl.getStartLevel());

    tb4.stop();
    Thread.sleep(SLEEP);
    assertEquals("getStartLevel", sl_10,  sl.getStartLevel());

    tb4.uninstall();
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
      break;
    case FrameworkEvent.STARTLEVEL_CHANGED:
      if (logStartLevelChanged)
        log("got framework event " + event.getType());
      break;
    }
  }
	

	boolean inState(Bundle b, int stateMask) {
		return (b.getState() & stateMask) != 0;
	}
}

