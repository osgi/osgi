/*
 * ============================================================================
 * Copyright (c) 2005, Gatespace Telematics
 * All rights reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */

package org.osgi.impl.service.device.manager;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.device.DriverLocator;
import org.osgi.service.device.DriverSelector;
import org.osgi.service.device.Match;
import org.osgi.service.log.LogService;

public class Activator extends Thread implements BundleActivator,
        ServiceListener, BundleListener, FrameworkListener {

    // NB: These constants should be configurable

    private static final long LONG_LIFE = 15 * 60 * 1000;

    private static final long SHORT_LIFE = 5 * 60 * 1000;

    private static final long REAP_INTERVAL = 1 * 60 * 1000;

    static final String DYNAMIC_DRIVER_TAG = "__DD_";

    private static final String LOG_FILTER = "(objectClass="
            + LogService.class.getName() + ")";

    private static final String DEVICE_FILTER = "(|(objectClass="
            + Device.class.getName() + ")(DEVICE_CATEGORY=*))";

    private static final String DRIVER_FILTER = "(objectClass="
            + Driver.class.getName() + ")";

    private static final String SELECTOR_FILTER = "(objectClass="
            + DriverSelector.class.getName() + ")";

    private static final String LOCATOR_FILTER = "(objectClass="
            + DriverLocator.class.getName() + ")";

    private static final String GLOBAL_FILTER = "(|" + LOG_FILTER
            + DEVICE_FILTER + DRIVER_FILTER + SELECTOR_FILTER + LOCATOR_FILTER
            + ")";

    BundleContext bc;

    private boolean active;

    private boolean quit;

    private Filter isLog;

    private Filter isDevice;

    private Filter isDriver;

    private Filter isSelector;

    private Filter isLocator;

    private Vector /* DriverRef */drivers = new Vector();

    private ServiceReference[] locatorRefs;

    DriverLocator[] locators;

    private ServiceReference selectorRef;

    private DriverSelector selector;

    private ServiceReference logRef;

    private LogService log;

    private Hashtable /* Integer->MatchValue */cache = new Hashtable();

    private Hashtable /* ServiceReference->ServiceReference */newDevices = new Hashtable(
            20);

    private Hashtable /* Bundle->Long */tempDrivers = new Hashtable(10);

    private long reapTime;

    public Activator() {
        super("DeviceManager");
    }

    public void start(BundleContext bc) throws Exception {
        this.bc = bc;

        isLog = bc.createFilter(LOG_FILTER);
        isDevice = bc.createFilter(DEVICE_FILTER);
        isDriver = bc.createFilter(DRIVER_FILTER);
        isSelector = bc.createFilter(SELECTOR_FILTER);
        isLocator = bc.createFilter(LOCATOR_FILTER);

        startService(LOG_FILTER);

        start();

        bc.addFrameworkListener(this);
        Bundle b = bc.getBundle(0);
        if (b.getState() == Bundle.ACTIVE)
            activate();
        else
            info("Passive start");
    }

    public void stop(BundleContext bc) {
        info("Stopping");
        quit = true;
        synchronized (this) {
            notifyAll();
        }
    }

    private synchronized void activate() throws Exception {

        if (active)
            return;
        active = true;
        info("Activating");

        bc.addBundleListener(this);

        // Thanks to TID for combining the (buggy)
        // separate listeners into one
        startService(GLOBAL_FILTER);
    }

    public void frameworkEvent(FrameworkEvent e) {
        try {
            if (e.getType() == FrameworkEvent.STARTED)
                activate();
        } catch (Exception e1) {
        }
    }

    private void startService(String filter) throws Exception {
        bc.addServiceListener(this, filter);
        ServiceReference[] sra = bc.getServiceReferences(null, filter);
        if (sra != null) {
            for (int i = 0; i < sra.length; i++) {
                try {
                    serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED,
                            sra[i]));
                } catch (Exception e) {
                }
            }
        }
    }

    public void serviceChanged(ServiceEvent e) {
        ServiceReference sr = e.getServiceReference();
        if (isDevice.match(sr)) {
            switch (e.getType()) {
            case ServiceEvent.REGISTERED:
                info("device found, " + showDevice(sr));
                touchDevice(sr);
                break;
            case ServiceEvent.MODIFIED:
                // We should most likely not do anything here
                // removeSRCachedMatch(sr);
                // touchDevice(sr);
                break;
            case ServiceEvent.UNREGISTERING:
                info("device lost, " + showDevice(sr));
                removeSRCachedMatch(sr);
                break;
            }
        }
        if (isDriver.match(sr)) {
            try {
                switch (e.getType()) {
                case ServiceEvent.REGISTERED:
                    info("driver found, " + showDriver(sr));
                    driverAppeared(sr);
                    touchAllDevices();
                    break;
                case ServiceEvent.MODIFIED:
                    // We should probably not do anything here
                    // driverGone(sr);
                    // driverAppeared(sr);
                    break;
                case ServiceEvent.UNREGISTERING:
                    info("driver lost, " + showDriver(sr));
                    driverGone(sr);
                    touchAllDevices();
                    break;
                }
            } catch (Exception e1) {
            }
        }
        if (isSelector.match(sr)) {
            try {
                bc.ungetService(selectorRef);
            } catch (Exception e1) {
            }
            selector = null;
            try {
                selectorRef = bc.getServiceReference(DriverSelector.class
                        .getName());
                selector = (DriverSelector) bc.getService(selectorRef);
            } catch (Exception e1) {
            }
        }
        if (isLocator.match(sr)) {
            try {
                for (int i = 0; i < locatorRefs.length; i++) {
                    bc.ungetService(locatorRefs[i]);
                }
            } catch (Exception e1) {
            }
            locatorRefs = null;
            locators = null;
            try {
                locatorRefs = bc.getServiceReferences(DriverLocator.class
                        .getName(), null);
                locators = new DriverLocator[locatorRefs.length];
                for (int i = 0; i < locatorRefs.length; i++) {
                    locators[i] = (DriverLocator) bc.getService(locatorRefs[i]);
                }
            } catch (Exception e1) {
            }
        }
        if (isLog.match(sr)) {
            try {
                bc.ungetService(logRef);
            } catch (Exception e1) {
            }
            try {
                logRef = bc.getServiceReference(LogService.class.getName());
                log = (LogService) bc.getService(logRef);
            } catch (Exception e1) {
            }
        }
    }

    private void driverAppeared(ServiceReference sr) {
        try {
            for (int i = 0; i < drivers.size(); i++) {
                DriverRef dr = (DriverRef) drivers.elementAt(i);
                if (dr.sr == sr)
                    return;
            }
            DriverRef dr = new DriverRef();
            try {
                dr.ranking = ((Integer) sr
                        .getProperty(org.osgi.framework.Constants.SERVICE_RANKING))
                        .intValue();
            } catch (Exception e) {
            }
            dr.servid = ((Long) sr
                    .getProperty(org.osgi.framework.Constants.SERVICE_ID))
                    .longValue();
            dr.id = (String) sr
                    .getProperty(org.osgi.service.device.Constants.DRIVER_ID);
            dr.sr = sr;
            if (dr.id != null)
                dr.drv = (Driver) bc.getService(sr);
            else
                error("ignoring driver without id " + showDriver(sr));
            if (dr.drv != null)
                drivers.addElement(dr);
        } catch (Exception e) {
        }
    }

    private void driverGone(ServiceReference sr) {
        try {
            for (int i = 0; i < drivers.size(); i++) {
                DriverRef dr = (DriverRef) drivers.elementAt(i);
                if (dr.sr == sr) {
                    drivers.removeElementAt(i);
                    return;
                }
            }
        } catch (Exception e) {
        }
    }

    public void bundleChanged(BundleEvent e) {
        if (e.getType() == BundleEvent.UNINSTALLED) {
            tempDrivers.remove(e.getBundle());
        }
    }

    private void info(String msg) {
        try {
            log.log(LogService.LOG_INFO, msg);
        } catch (Exception e) {
            System.err.println("[Device Manager] info: " + msg);
        }
    }

    private void error(String msg) {
        try {
            log.log(LogService.LOG_ERROR, msg);
        } catch (Exception e) {
            System.err.println("[Device Manager] ERROR: " + msg);
        }
    }

    public void run() {
        while (!quit) {
            boolean sleep = true;
            try {
                ServiceReference dev = (ServiceReference) newDevices.keys()
                        .nextElement();
                newDevices.remove(dev);
                sleep = false;
                handleDevice(dev);
            } catch (Exception e) {
            }

            if (!sleep)
                continue;

            long now = System.currentTimeMillis();
            if (now >= reapTime) {
                reapDrivers();
                reapTime = now + REAP_INTERVAL;

                // NB: Should also clean out old matches every now
                // and then based on some kind of LRU scheme
            }
            synchronized (this) {
                if (!quit)
                    try {
                        wait(reapTime - now);
                    } catch (Exception e) {
                    }
            }
        }
    }

    private void handleDevice(ServiceReference dev) {
        if (isUsed(dev))
            return;

        Dictionary props = collectProperties(dev);

        Vector /* MatchImpl */matches = new Vector();

        // Populate matches with driver locator recommendations
        DriverLocator[] dla = locators;
        if (dla != null) {
            for (int i = 0; i < dla.length; i++) {
                try {
                    DriverLocator dl = dla[i];
                    String[] dria = dl.findDrivers(props);
                    for (int j = 0; j < dria.length; j++) {
                        String dri = dria[j];
                        MatchImpl m = null;
                        for (int k = 0; k < matches.size(); k++) {
                            m = (MatchImpl) matches.elementAt(k);
                            if (m.equals(dri))
                                break;
                            m = null;
                        }
                        if (m == null) {
                            m = new MatchImpl(this, dev, dri);
                            matches.addElement(m);
                        }
                        m.addDriverLocator(dl);
                    }
                } catch (Exception e) {
                }
            }
        }

        for (;;) {
            // Add current drivers to matches
            for (int i = 0; i < drivers.size(); i++) {
                DriverRef dr = (DriverRef) drivers.elementAt(i);
                MatchImpl m = null;
                for (int k = 0; k < matches.size(); k++) {
                    m = (MatchImpl) matches.elementAt(k);
                    if (m.connect(dr))
                        break;
                    m = null;
                }
                if (m == null) {
                    m = new MatchImpl(this, dev, dr);
                    matches.addElement(m);
                }
            }

            int n = 0;
            boolean loading = false;

            // Count good matches and trigger loading
            for (int i = 0; i < matches.size(); i++) {
                MatchImpl m = (MatchImpl) matches.elementAt(i);
                int match = m.getMatchValue();
                if (match == MatchImpl.UNKNOWN)
                    loading = true;
                else if (match > Device.MATCH_NONE)
                    n++;
            }

            // If not finished loading continue
            if (loading)
                continue;

            // If nothing matches we're done
            if (n == 0) {
                tellNotFound(dev);
                return;
            }

            // Filter out good matches and select default best match
            MatchImpl best = null;
            Match[] sel = new Match[n];
            n = 0;
            for (int i = 0; i < matches.size(); i++) {
                MatchImpl m = (MatchImpl) matches.elementAt(i);
                if (m.getMatchValue() > Device.MATCH_NONE) {
                    sel[n++] = m;
                    if (best == null || best.compare(m) < 0)
                        best = m;
                }
            }

            // Maybe use driver selector instead
            DriverSelector ds = selector;
            if (ds != null) {
                int ix = ds.select(dev, sel);
                if (ix == DriverSelector.SELECT_NONE) {
                    tellNotFound(dev);
                    return;
                }
                try {
                    best = (MatchImpl) sel[ix];
                } catch (Exception e) {
                }
            }

            // If attach succeeds we're done
            String ref = null;
            try {
                ref = best.attach();
            } catch (Exception e) {
                error("failed attach " + showDriver(best.getDriver()) + " -> "
                        + showDevice(dev));
                continue;
            }

            if (best.getMatchValue() > Device.MATCH_NONE) {
                // Just loaded, go around and pick up the driver ref
                continue;
            }

            if (ref == null) {
                info("attached " + showDriver(best.getDriver()) + " -> "
                        + showDevice(dev));
                Bundle b = best.getBundle();
                if (b != null)
                    updateLife(b, LONG_LIFE);
                return;
            }

            info(showDriver(best.getDriver()) + " refers to " + ref);

            // Append the referred match
            MatchImpl m = null;
            for (int i = 0; i < matches.size(); i++) {
                m = (MatchImpl) matches.elementAt(i);
                if (m.equals(ref))
                    break;
                m = null;
            }
            if (m == null) {
                m = new MatchImpl(this, dev, ref);
                matches.addElement(m);
            }
        }
    }

    private void updateLife(Bundle b, long t) {
        tempDrivers.put(b, new Long(System.currentTimeMillis() + t));
    }

    private boolean isUsed(ServiceReference sr) {
        Bundle[] ba = sr.getUsingBundles();
        if (ba != null) {
            for (int i = 0; i < ba.length; i++) {
                Bundle b = ba[i];
                try {
                    for (int j = 0; j < drivers.size(); j++) {
                        DriverRef dr = (DriverRef) drivers.elementAt(j);
                        if (dr.sr.getBundle() == b)
                            return true;
                    }
                } catch (Exception e) {
                    return true;
                }
            }
        }
        return false;
    }

    private void reapDrivers() {
        long now = System.currentTimeMillis();
        Bundle[] ba = bc.getBundles();
        if (ba != null) {
            for (int i = 0; i < ba.length; i++) {
                try {
                    Bundle b = ba[i];
                    if (b.getLocation().startsWith(DYNAMIC_DRIVER_TAG)) {
                        Long expire = (Long) tempDrivers.get(b);
                        boolean inUse = false;

                        ServiceReference[] sra = b.getServicesInUse();
                        if (sra != null) {
                            for (int j = 0; j < sra.length; j++) {
                                if (isDevice.match(sra[j])) {
                                    inUse = true;
                                    break;
                                }
                            }
                        }

                        if (inUse) {
                            updateLife(b, LONG_LIFE);
                        } else if (expire == null) {
                            updateLife(b, SHORT_LIFE);
                        } else if (expire.longValue() < now) {
                            info("uninstalling " + b.getLocation());
                            b.uninstall();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void touchDevice(ServiceReference dev) {
        if (newDevices.put(dev, dev) == null) {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    private void touchAllDevices() {
        boolean added = false;
        try {
            ServiceReference[] sra = bc.getServiceReferences(null,
                    DEVICE_FILTER);
            if (sra != null) {
                for (int i = 0; i < sra.length; i++) {
                    ServiceReference dev = sra[i];
                    if (newDevices.put(dev, dev) == null)
                        added = true;
                }
            }
        } catch (Exception e) {
        }
        if (added) {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    private void tellNotFound(ServiceReference dev) {
        // NB: Should we avoid repeating the call to the same device?

        info("no driver for " + showDevice(dev));
        Object d = null;
        try {
            d = bc.getService(dev);
            ((Device) d).noDriverFound();
        } catch (Exception e) {
        } finally {
            try {
                bc.ungetService(dev);
            } catch (Exception e1) {
            }
        }
    }

    private Dictionary /* String->Object */collectProperties(
            ServiceReference sr) {
        Dictionary props = new Hashtable();
        String[] keys = sr.getPropertyKeys();
        if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                props.put(key, sr.getProperty(key));
            }
        }
        return props;
    }

    Bundle installBundle(String name, InputStream is) {
        Bundle b = null;
        try {
            info("installing " + name);
            b = bc.installBundle(name, is);
            b.start();
            updateLife(b, SHORT_LIFE);
            return b;
        } catch (Exception e) {
            error("failed to install " + name);
            try {
                b.uninstall();
            } catch (Exception e1) {
            }
            return null;
        } finally {
            try {
                is.close();
            } catch (Exception e1) {
            }
        }
    }

    void removeSRCachedMatch(ServiceReference sr) {

        // NB: Index to speed up this process?

        String pid = null;
        try {
            pid = (String) sr
                    .getProperty(org.osgi.framework.Constants.SERVICE_PID);
        } catch (Exception e) {
        }
        if (pid != null)
            return;
        for (Enumeration e = cache.keys(); e.hasMoreElements();) {
            Integer k = (Integer) e.nextElement();
            MatchValue mv0 = (MatchValue) cache.get(k);
            MatchValue mv = mv0;
            while (mv != null) {
                if (mv.dev != sr) {
                    // Keep it
                    mv0 = mv;
                    mv = mv.next;
                } else if (mv == mv0) {
                    // Update hash table entry
                    mv0 = mv = mv.next;
                    if (mv != null)
                        cache.put(k, mv);
                    else
                        cache.remove(k);
                } else {
                    // Link past this one
                    mv = mv.next;
                    mv0.next = mv;
                }
            }
        }
    }

    int getCachedMatch(String drvid, ServiceReference dev) {
        MatchValue mv = findMatch(drvid, dev, false);
        return mv != null ? mv.match : MatchImpl.UNKNOWN;
    }

    void putCachedMatch(String drvid, ServiceReference dev, int match) {
        MatchValue mv = findMatch(drvid, dev, true);
        mv.match = match;
    }

    private MatchValue findMatch(String drvid, ServiceReference dev,
            boolean create) {
        String pid = null;
        try {
            pid = (String) dev
                    .getProperty(org.osgi.framework.Constants.SERVICE_PID);
        } catch (Exception e) {
        }
        int k1 = pid != null ? pid.hashCode() : dev.hashCode();
        int k2 = drvid.hashCode();
        Integer key = new Integer(k1 + k2);
        MatchValue mv0 = (MatchValue) cache.get(key);
        MatchValue mv = mv0;
        while (mv != null) {
            if (drvid.equals(mv.drvid) && pid != null ? pid.equals(mv.pid)
                    : dev == mv.dev)
                return mv;
            mv = mv.next;
        }

        if (!create)
            return null;

        mv = new MatchValue();
        mv.next = mv0;
        mv.key = key;
        mv.drvid = drvid;
        mv.pid = pid;
        if (pid == null)
            mv.dev = dev;
        cache.put(key, mv);
        return mv;
    }

    private String showDevice(ServiceReference sr) {
        StringBuffer sb = new StringBuffer();
        Object o = sr
                .getProperty(org.osgi.service.device.Constants.DEVICE_CATEGORY);
        if (o instanceof String) {
            sb.append(o);
        } else if (o instanceof String[]) {
            String[] dca = (String[]) o;
            for (int i = 0; i < dca.length; i++) {
                if (i > 0)
                    sb.append('_');
                sb.append(dca[i]);
            }
        }
        o = sr.getProperty(org.osgi.framework.Constants.SERVICE_ID);
        if (o != null)
            sb.append(o);
        Bundle b = sr.getBundle();
        if (b != null) {
            sb.append('/');
            sb.append(b.getBundleId());
        }
        return sb.toString();
    }

    private String showDriver(ServiceReference sr) {
        StringBuffer sb = new StringBuffer();
        String s = (String) sr
                .getProperty(org.osgi.service.device.Constants.DRIVER_ID);
        sb.append(s != null ? s : "driver");
        Bundle b = sr.getBundle();
        if (b != null) {
            sb.append('/');
            sb.append(b.getBundleId());
        }
        return sb.toString();
    }
}
