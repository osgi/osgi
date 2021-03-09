/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.device.manager;

import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.device.DriverLocator;
import org.osgi.service.device.Match;

public class MatchImpl implements Match {
    static final int UNKNOWN = -1;

    private Activator act;

	private ServiceReference< ? >	dev;

    private String id;

    private DriverRef ref;

    private int match;

	private List<DriverLocator>		dls;

    private Bundle b;

	MatchImpl(Activator act, ServiceReference< ? > dev, String id) {
        this.act = act;
        this.dev = dev;
        this.id = id;
        match = act.getCachedMatch(id, dev);
    }

	MatchImpl(Activator act, ServiceReference< ? > dev, DriverRef dr) {
        this.act = act;
        this.dev = dev;
        id = dr.id;
        ref = dr;
        match = act.getCachedMatch(id, dev);
    }

	boolean equals(@SuppressWarnings("hiding") String id) {
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
			dls = new Vector<>();
		dls.add(dl);
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
				if (load1(name, dls.get(i)))
                    return true;
            }
        } else {
			List<DriverLocator> locs = act.locators;
			for (int i = 0; i < locs.size(); i++) {
				if (load1(name, locs.get(i)))
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
			// ignore
        } finally {
            try {
                is.close();
            } catch (Exception e1) {
				// ignore
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

	@Override
	public ServiceReference<Driver> getDriver() {
        return ref.sr;
    }

	@Override
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
