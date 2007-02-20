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

import java.util.ArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.test.cases.util.BundleEventCollector;
import org.osgi.test.cases.util.DefaultTestBundleControl;
import org.osgi.test.cases.util.FrameworkEventCollector;

public class StartLevelControl extends DefaultTestBundleControl {

  private StartLevel sl;
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
  private FrameworkEventCollector fec;
  private BundleEventCollector bec;

  String methods[] = {
		"testInitialBundleStartLevel",
		"testStartOrder",
		"testSetStartLevel",
		"testSetBundleStartLevel",
		"testPersistentlyStarted",
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

    fec = new FrameworkEventCollector(FrameworkEvent.STARTLEVEL_CHANGED);
    getContext().addFrameworkListener(fec);
    bec = new BundleEventCollector(BundleEvent.STARTED | BundleEvent.STOPPED);
    getContext().addBundleListener(bec);
  }

  public void unprepare() throws Exception {
	  getContext().removeFrameworkListener(fec);
	  getContext().removeBundleListener(bec);

	  sl.setInitialBundleStartLevel(ibsl);
	  sl.setStartLevel(origSl);
  }
  public void setState() throws Exception {
	  fec.clear();
	  bec.clear();
  }
  public void clearState() throws Exception {}

  public void testInitialBundleStartLevel() throws Exception
  {
    ArrayList expectedFrameworkEvents = new ArrayList();
    ArrayList expectedBundleStartEvents = new ArrayList();
    ArrayList expectedBundleStopEvents = new ArrayList();

    sl.setInitialBundleStartLevel(sl_20);
    assertEquals("getInitialBundleStartLevel", sl_20,  sl.getInitialBundleStartLevel());
    
    sl.setStartLevel(sl_10);
    expectedFrameworkEvents.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(),null));
    assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
    
    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    tb1.start();
    assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));
    
    sl.setInitialBundleStartLevel(sl_10);
    Bundle tb2 = getContext().installBundle(getWebServer() + "tb2.jar");
    tb2.start();
    expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED, tb2));
    assertEquals("Received bundle started event", bec.getComparator(), expectedBundleStartEvents, bec.getList(1, TIMEOUT));
    assertTrue("getState() = ACTIVE", inState(tb2, Bundle.ACTIVE));
    
    tb1.uninstall();
    tb2.stop();
    expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb2));
    assertEquals("Received bundle stopped event", bec.getComparator(), expectedBundleStopEvents, bec.getList(1, TIMEOUT));
    tb2.uninstall();
  }


  public void testStartOrder() throws Exception
  {
    ArrayList expectedFrameworkEvents = new ArrayList();
    ArrayList expectedBundleStartEvents = new ArrayList();
    ArrayList expectedBundleStopEvents = new ArrayList();

    sl.setInitialBundleStartLevel(sl_20);
    sl.setStartLevel(sl_10);
    expectedFrameworkEvents.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(),null));
    assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
    
    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");
    tb1.start();
    Bundle tb2 = getContext().installBundle(getWebServer() + "tb2.jar");
    tb2.start();
    try {
    	//start tb1 and tb2
    	sl.setStartLevel(sl_20);
    	assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
    	expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED, tb1));
    	expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED, tb2));
    	assertEquals("Received bundle started event", bec.getComparator(), expectedBundleStartEvents, bec.getListSorted(2, TIMEOUT));
    	
    	//stop tb2 and tb1
    	sl.setStartLevel(sl_10);
    	assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
    	expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb1));
    	expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb2));
    	assertEquals("Received bundle stopped event", bec.getComparator(), expectedBundleStopEvents, bec.getListSorted(2, TIMEOUT));
    	
    	//reverse the start order
    	sl.setBundleStartLevel(tb2, sl_15);
    	Thread.sleep(SLEEP);
    	
    	//start tb2 and tb1
    	sl.setStartLevel(sl_20);
    	assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
    	expectedBundleStartEvents.clear();
    	expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED, tb2));
    	expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED, tb1));
    	assertEquals("Received bundle started event", bec.getComparator(), expectedBundleStartEvents, bec.getList(2, TIMEOUT));
    	
    	//stop tb1 and tb2
    	sl.setStartLevel(sl_10);
    	assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
    	expectedBundleStopEvents.clear();
    	expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb1));
    	expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb2));
    	assertEquals("Received bundle stopped event", bec.getComparator(), expectedBundleStopEvents, bec.getList(2, TIMEOUT));
    } finally {
    	tb1.uninstall();
    	tb2.uninstall();
    }
  }

  public void testSetStartLevel() throws Exception
  {
    ArrayList expectedFrameworkEvents = new ArrayList();
    ArrayList expectedBundleStartEvents = new ArrayList();
    ArrayList expectedBundleStopEvents = new ArrayList();

    expectedFrameworkEvents.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(),null));

    sl.setInitialBundleStartLevel(sl_15);
    sl.setStartLevel(sl_10);
    assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));

    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");

    expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED, tb1));
    expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb1));
    try {
      sl.setStartLevel(sl_20);
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
      assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      sl.setStartLevel(sl_10);
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
      assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      tb1.start();
      assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      sl.setStartLevel(sl_20);
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
      assertTrue("getState() = ACTIVE", inState(tb1, Bundle.ACTIVE));
	  assertEquals("Received bundle started event", bec.getComparator(), expectedBundleStartEvents, bec.getList(1, TIMEOUT));

      sl.setStartLevel(sl_10);
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
      assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));
	  assertEquals("Received bundle stopped event", bec.getComparator(), expectedBundleStopEvents, bec.getList(1, TIMEOUT));

      tb1.stop();
      sl.setStartLevel(sl_20);
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
      assertTrue("getState() = INSTALLED | RESOLVED", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));
    } finally {
      tb1.uninstall();
    }
  }

  public void testSetBundleStartLevel() throws Exception
  {
	    ArrayList expectedFrameworkEvents = new ArrayList();
	    ArrayList expectedBundleStartEvents = new ArrayList();
	    ArrayList expectedBundleStopEvents = new ArrayList();

	    expectedFrameworkEvents.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(),null));

    sl.setInitialBundleStartLevel(sl_15);
    sl.setStartLevel(sl_10);
    assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));

    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");

    expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED, tb1));
    expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb1));
    try {
      sl.setBundleStartLevel(tb1, sl_5);
      Thread.sleep(SLEEP);
      assertTrue("Startlevel 10/5 stop", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      sl.setBundleStartLevel(tb1, sl_15);
      Thread.sleep(SLEEP);
      assertTrue("StartLevel 10/15 stop", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      tb1.start();
      assertTrue("StartLevel 10/15 start", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      sl.setBundleStartLevel(tb1, sl_5);
	  assertEquals("Received bundle started event", bec.getComparator(), expectedBundleStartEvents, bec.getList(1, TIMEOUT));
      assertTrue("StartLevel 10/5 start", inState(tb1, Bundle.ACTIVE));

      sl.setBundleStartLevel(tb1, sl_15);
	  assertEquals("Received bundle stopped event", bec.getComparator(), expectedBundleStopEvents, bec.getList(1, TIMEOUT));
      assertTrue("StartLevel 10/15 start", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));

      sl.setBundleStartLevel(tb1, sl_5);
	  assertEquals("Received bundle started event", bec.getComparator(), expectedBundleStartEvents, bec.getList(1, TIMEOUT));
      assertTrue("StartLevel 10/5 start", inState(tb1, Bundle.ACTIVE));
      
      tb1.stop();
	  assertEquals("Received bundle stopped event", bec.getComparator(), expectedBundleStopEvents, bec.getList(1, TIMEOUT));
      assertTrue("stop", inState(tb1, Bundle.INSTALLED | Bundle.RESOLVED));
    } finally {
      tb1.uninstall();
    }
  }

  public void testPersistentlyStarted() throws Exception
  {
	    ArrayList expectedFrameworkEvents = new ArrayList();
	    ArrayList expectedBundleStartEvents = new ArrayList();
	    ArrayList expectedBundleStopEvents = new ArrayList();

	    expectedFrameworkEvents.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(),null));

	    sl.setInitialBundleStartLevel(sl_15);
    assertEquals("setInitialBundleStartLevel", sl_15,  sl.getInitialBundleStartLevel());

    sl.setStartLevel(sl_10);
    assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));

    Bundle tb1 = getContext().installBundle(getWebServer() + "tb1.jar");

    expectedBundleStartEvents.add(new BundleEvent(BundleEvent.STARTED, tb1));
    expectedBundleStopEvents.add(new BundleEvent(BundleEvent.STOPPED, tb1));
    try {
      assertEquals("isBundlePersistentlyStarted", false, sl.isBundlePersistentlyStarted(tb1));

      tb1.start();
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

      sl.setStartLevel(sl_20);
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
	  assertEquals("Received bundle started event", bec.getComparator(), expectedBundleStartEvents, bec.getList(1, TIMEOUT));
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

      sl.setStartLevel(sl_10);
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
      assertEquals("Received bundle stopped event", bec.getComparator(), expectedBundleStopEvents, bec.getList(1, TIMEOUT));
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

      sl.setBundleStartLevel(tb1, sl_5);
	  assertEquals("Received bundle started event", bec.getComparator(), expectedBundleStartEvents, bec.getList(1, TIMEOUT));
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));

      sl.setBundleStartLevel(tb1, sl_15);
      assertEquals("Received bundle stopped event", bec.getComparator(), expectedBundleStopEvents, bec.getList(1, TIMEOUT));
      assertEquals("isBundlePersistentlyStarted", true, sl.isBundlePersistentlyStarted(tb1));
    } finally {
      tb1.uninstall();
    }
  }

  public void testSystemBundle() throws Exception
  {
    Bundle systemBundle = getContext().getBundle(0);
    assertEquals("getBundleStartLevel", 0,  sl.getBundleStartLevel(systemBundle));
    try {
      sl.setBundleStartLevel(systemBundle, 42);
      fail("got no IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
    }
  }


  public void testExceptionInActivator() throws Exception
  {
	    ArrayList expectedFrameworkEvents = new ArrayList();
	    ArrayList expectedFrameworkError1 = new ArrayList();
	    ArrayList expectedFrameworkError2 = new ArrayList();

	    FrameworkEventCollector fec2 = new FrameworkEventCollector(FrameworkEvent.STARTLEVEL_CHANGED|FrameworkEvent.ERROR);
	    getContext().addFrameworkListener(fec2);

	    expectedFrameworkEvents.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(),null));

	    sl.setInitialBundleStartLevel(sl_5);
    sl.setStartLevel(sl_10);
    assertEquals("Received start level changed event", fec2.getComparator(), expectedFrameworkEvents, fec2.getList(1, TIMEOUT));

    Bundle tb5 = getContext().installBundle(getWebServer() + "tb5.jar");
    expectedFrameworkError1.add(new FrameworkEvent(FrameworkEvent.ERROR, tb5, new BundleException("")));
    expectedFrameworkError2.add(new FrameworkEvent(FrameworkEvent.ERROR, tb5, new BundleException("")));
    expectedFrameworkError2.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(),null));


    try {
    	try {
    		tb5.start();
    		assertEquals("getState() = ACTIVE", true, inState(tb5, Bundle.ACTIVE));
    		
    	} catch (Exception e) {
    		fail("Unexpected exception: " + e.getMessage());
    	}
    	
      //FrameworkEvent.ERROR due to active startlevel change
      sl.setStartLevel(sl_4);
      assertEquals("Received framework events", fec2.getComparator(), expectedFrameworkError2, fec2.getList(2, TIMEOUT));
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb5, Bundle.INSTALLED | Bundle.RESOLVED));

      //no FrameworkEvent.ERROR
      sl.setStartLevel(sl_5);
      assertEquals("Received start level changed event", fec2.getComparator(), expectedFrameworkEvents, fec2.getList(1, TIMEOUT));
      assertEquals("getState() = ACTIVE", true, inState(tb5, Bundle.ACTIVE));

      
      //FrameworkEvent.ERROR due to bundle startlevel change
      sl.setBundleStartLevel(tb5, sl_6); 
      assertEquals("Received framework events", fec2.getComparator(), expectedFrameworkError1, fec2.getList(1, TIMEOUT));
      assertEquals("getState() = INSTALLED | RESOLVED", true, inState(tb5, Bundle.INSTALLED | Bundle.RESOLVED));
    } finally {
      tb5.uninstall();
      getContext().removeFrameworkListener(fec2);
    }
  }

  public void testActivator() throws Exception
  {
	    ArrayList expectedFrameworkEvents = new ArrayList();
	    ArrayList expectedTB3Events = new ArrayList();
	    ArrayList expectedTB4Events = new ArrayList();

	    expectedFrameworkEvents.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, getContext().getBundle(),null));

	    sl.setInitialBundleStartLevel(sl_5);
    sl.setStartLevel(sl_10);
    assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
    Bundle tb3 = getContext().installBundle(getWebServer() + "tb3.jar");
    expectedTB3Events.add(new BundleEvent(BundleEvent.STARTED, tb3));
    expectedTB3Events.add(new BundleEvent(BundleEvent.STOPPED, tb3));
    try {
      tb3.start();
  	assertEquals("Received bundle events", bec.getComparator(), expectedTB3Events, bec.getList(2, TIMEOUT));
      assertEquals("getBundleStartLevel", sl_15,  sl.getBundleStartLevel(tb3));
    } finally {
      tb3.uninstall();
    }

    Bundle tb4 = getContext().installBundle(getWebServer() + "tb4.jar");
    expectedTB4Events.add(new BundleEvent(BundleEvent.STARTED, tb4));
    expectedTB4Events.add(new BundleEvent(BundleEvent.STOPPED, tb4));
    expectedFrameworkEvents.clear();
    expectedFrameworkEvents.add(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, tb4,null));
    try {
      tb4.start();
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
      assertEquals("getStartLevel", sl_15,  sl.getStartLevel());

      tb4.stop();
      assertEquals("Received start level changed event", fec.getComparator(), expectedFrameworkEvents, fec.getList(1, TIMEOUT));
    	assertEquals("Received bundle events", bec.getComparator(), expectedTB4Events, bec.getList(2, TIMEOUT));
      assertEquals("getStartLevel", sl_10,  sl.getStartLevel());
    } finally {
      tb4.uninstall();
    }
  }

  boolean inState(Bundle b, int stateMask) {
		return (b.getState() & stateMask) != 0;
	}
}

