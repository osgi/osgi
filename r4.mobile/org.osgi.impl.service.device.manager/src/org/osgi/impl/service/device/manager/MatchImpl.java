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
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Device;
import org.osgi.service.device.DriverLocator;
import org.osgi.service.device.Match;

public class MatchImpl implements Match {
    static final int UNKNOWN = -1;

    private Activator act;

    private ServiceReference dev;

    private String id;

    private DriverRef ref;

    private int match;

    private Vector /* DriverLocator */dls;

    private Bundle b;

    MatchImpl(Activator act, ServiceReference dev, String id) {
        this.act = act;
        this.dev = dev;
        this.id = id;
        match = act.getCachedMatch(id, dev);
    }

    MatchImpl(Activator act, ServiceReference dev, DriverRef dr) {
        this.act = act;
        this.dev = dev;
        id = dr.id;
        ref = dr;
        match = act.getCachedMatch(id, dev);
    }

    boolean equals(String id) {
        return this.id.equals(id);
    }

    boolean connect(DriverRef dr) {
        if (id.equals(dr.id) && (ref == dr || ref == null)) {
            ref = dr;
            return true;
        }
        return false;
    }

    int compare(MatchImpl m) {
        int cmp = getMatchValue() - m.getMatchValue();
        if (cmp == 0)
            cmp = ref.ranking - m.ref.ranking;
        if (cmp == 0)
            cmp = ref.servid < m.ref.servid ? 1 : -1;
        return cmp;
    }

    void addDriverLocator(DriverLocator dl) {
        if (dls == null)
            dls = new Vector();
        dls.addElement(dl);
    }

    Bundle getBundle() {
        return b;
    }

    private boolean load() {
        String name = Activator.DYNAMIC_DRIVER_TAG + id;
        Bundle[] ba = act.bc.getBundles();
        if (ba != null) {
            for (int i = 0; i < ba.length; i++) {
                if (name.equals(ba[i].getLocation())) {
                    return false;
                }
            }
        }
        if (dls != null) {
            for (int i = 0; i < dls.size(); i++) {
                if (load1(name, (DriverLocator) dls.elementAt(i)))
                    return true;
            }
        } else {
            DriverLocator[] locs = act.locators;
            for (int i = 0; i < locs.length; i++) {
                if (load1(name, locs[i]))
                    return true;
            }
        }
        return false;
    }

    private boolean load1(String name, DriverLocator dl) {
        InputStream is = null;
        try {
            is = dl.loadDriver(id);
            b = act.installBundle(name, is);
            if (b != null)
                return true;
        } catch (Exception e) {
        } finally {
            try {
                is.close();
            } catch (Exception e1) {
            }
        }
        return false;
    }

    String attach() throws Exception {
        if (match > Device.MATCH_NONE && ref == null && b == null && load()) {
            return null;
        }
        match = Device.MATCH_NONE;
        return ref.drv.attach(dev);
    }

    public ServiceReference getDriver() {
        return ref.sr;
    }

    public int getMatchValue() {
        if (match == UNKNOWN) {
            if (ref != null || b != null) {
                try {
                    match = ref.drv.match(dev);
                    act.putCachedMatch(id, dev, match);
                } catch (Exception e) {
                    match = Device.MATCH_NONE;
                }
            } else if (!load()) {
                match = Device.MATCH_NONE;
            }
        }
        return match;
    }
}
