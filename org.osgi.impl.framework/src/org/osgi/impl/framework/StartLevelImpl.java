/**
 * Copyright (c) 2002 Gatespace AB. All Rights Reserved.
 *
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.framework;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.service.startlevel.StartLevel;

public class StartLevelImpl implements StartLevel
{
    public final static String SPECIFICATION_VERSION = "1.0";

    private final static String START_LEVEL_FILE_NAME = "startlevel";

    int activeStartLevel;
    int requestedStartLevel;
    Object lock = new Object();

    private Framework framework;
    private List queue = new ArrayList();
    private Thread worker;

    /**
     * Contruct a new StartLevel service.
     *
     * @param fw Framework for this StartLevel service.
     */
    public StartLevelImpl(Framework fw)
    {
      framework = fw;
      activeStartLevel = 0;
      requestedStartLevel = 0;
    }

    //
    // StartLevel interface
    //

    /**
     * Get current start level.
     *
     * @see org.osgi.service.startlevel.StartLevel#getStartLevel
     */
    public int getStartLevel()
    {
      return activeStartLevel;
    }

    public void setStartLevel(final int startlevel)
    {
      framework.checkAdminPermission();
      if (startlevel < 1) throw new IllegalArgumentException("Start Level < 1");
      StartLevelJob job = new StartLevelJob() {
        public void execute() {
          requestedStartLevel = startlevel;
          while (activeStartLevel != requestedStartLevel) {
            synchronized (lock) {
              if (activeStartLevel < requestedStartLevel) {
                ++activeStartLevel;
                framework.bundles.startBundles(activeStartLevel);
              } else {
                framework.bundles.stopBundles(activeStartLevel);
                --activeStartLevel;
              }
            }
          }
          framework.listeners.frameworkEvent(new FrameworkEvent(FrameworkEvent.STARTLEVEL_CHANGED, new Object()));
        }
      };
      addJob(job);
    }

    /**
     * Get bundle start level.
     *
     * @see org.osgi.service.startlevel.StartLevel#getBundleStartLevel
     */
    public int getBundleStartLevel(Bundle bundle)
    {
      BundleImpl b = framework.bundles.getBundle(bundle.getBundleId());
      if (b == null) throw new IllegalArgumentException("No such bundle");
      return b.startLevel;
    }

    public void setBundleStartLevel(Bundle bundle, int startlevel)
    {
      framework.checkAdminPermission();
      if (bundle.getBundleId() == 0) throw new IllegalArgumentException("Illegal operation on System Bundle");
      if (startlevel < 1) throw new IllegalArgumentException("Bundle #" + bundle.getBundleId() + ": startlevel < 1");
      final BundleImpl b = framework.bundles.getBundle(bundle.getBundleId());
      if (b == null) throw new IllegalArgumentException("No such bundle");
      synchronized (lock) {
        b.startLevel = startlevel;
      }
      StartLevelJob job = new StartLevelJob() {
        void execute() {
          if (b.isPersistentlyStarted) {
            try {
              if (b.startLevel <= activeStartLevel) {
                b.start();
              } else {
                b.stop();
                b.setPersistentlyStarted(true);
              }
            } catch (BundleException be) {
               b.framework.listeners.frameworkError(b, be);
            }
          }
        }
      };
      addJob(job);
    }

    /**
     * Get initial bundle start level.
     *
     * @see org.osgi.service.startlevel.StartLevel#getInitialBundleStartLevel
     */
    public int getInitialBundleStartLevel()
    {
      return framework.bundles.getInitialBundleStartLevel();
    }

    /**
     * Set initial bundle start level.
     *
     * @see org.osgi.service.startlevel.StartLevel#setInitialBundleStartLevel
     */
    public void setInitialBundleStartLevel(int startlevel)
    {
      framework.checkAdminPermission();
      if (startlevel < 1) throw new IllegalArgumentException("Start Level < 1");
      framework.bundles.setInitialBundleStartLevel(startlevel);
    }

    /**
     * Check persistent state.
     *
     * @see org.osgi.service.startlevel.StartLevel#isBundlePersistentlyStarted
     */
    public boolean isBundlePersistentlyStarted(Bundle bundle)
    {
      BundleImpl b = framework.bundles.getBundle(bundle.getBundleId());
      if (b == null) throw new IllegalArgumentException("No such bundle");
      return b.isPersistentlyStarted;
    }

    //
    // Package methods
    //

    /**
     * Force the start level to a specific value.
     *
     * @param startlevel The forced start level.
     */
    void forceStartLevel(int startlevel)
    {
      synchronized (framework) {
        requestedStartLevel = startlevel;
        activeStartLevel = startlevel;
      }
    }

    //
    // Private methods
    //

    /**
     * Add a job to the job queue.
     *
     * @param job A StartLevelJob.
     */
    private synchronized void addJob(StartLevelJob job)
    {
      queue.add(job);
      if (worker == null) {
        worker = new Thread() {
          public void run() {
            while(true) {
              StartLevelJob job = getJob();
	      if (job == null) return;
              job.execute();
            }
          }
        };
        worker.setDaemon(false);
        worker.start();
      }
    }

    /**
     * Get a job from the job queue.
     *
     * @return A StartLevelJob or null if the queue is empty.
     */
    private synchronized StartLevelJob getJob()
    {
      if (queue.isEmpty()) {
	worker = null;
	return null;
      }
      return (StartLevelJob)queue.remove(0);
    }

    /**
     * Job template class.
     */
    private abstract class StartLevelJob
    {
      /**
       * Implement the job that shall be done.
       */
      abstract void execute();
    }
}
