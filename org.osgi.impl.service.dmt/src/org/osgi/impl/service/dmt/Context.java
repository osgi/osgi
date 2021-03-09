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

package org.osgi.impl.service.dmt;

import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.dispatcher.Dispatcher;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Context {
    private BundleContext bc;
	private Hashtable<Class< ? >,ServiceTracker< ? , ? >>	trackers;
    
    private Dispatcher dispatcher = null;

    Context(BundleContext bc) {
        this.bc = bc;
		trackers = new Hashtable<>();

        // make the root plugin known to the dispatcher
		@SuppressWarnings({
				"rawtypes", "unchecked"
		})
		ServiceReference<Object> rootPlugin = (ServiceReference) registerRootPlugin();
		getPluginDispatcher().addingService(rootPlugin);
        
        // start tracking 
        getPluginDispatcher().open();
    }
    
	private ServiceReference<DataPlugin> registerRootPlugin() {
    	Hashtable<String, String[]> props = new Hashtable<String, String[]>();
    	props.put( "dataRootURIs", new String[] { "." });
		ServiceRegistration<DataPlugin> reg = bc.registerService(
				DataPlugin.class, new RootPlugin(getPluginDispatcher()), props);
    	return reg.getReference();
    }
    
    
    BundleContext getBundleContext() {
        return bc;
    }
    
    // cannot be used for tracking multiple classes or using a customizer
	<T> ServiceTracker<T,T> getTracker(Class<T> trackedClass) {
		@SuppressWarnings("unchecked")
		ServiceTracker<T,T> tracker = (ServiceTracker<T,T>) trackers
				.get(trackedClass);
        if(tracker == null) // create tracker if it does not exist yet
            tracker = openTracker(trackedClass, null);
        return tracker;
    }
    
    /**
     * SD: Bundlefest: replaced it by recursive dispatcher that supports mount-points
     * @return
     */
	synchronized Dispatcher getPluginDispatcher() {
        if(dispatcher == null) { // create plugin tracker if it DNE yet
            try {
	            String filter = "(|(objectClass=org.osgi.service.dmt.spi.DataPlugin)" +
	            					"(objectClass=org.osgi.service.dmt.spi.ExecPlugin))";
				dispatcher = new Dispatcher(this.bc, bc.createFilter(filter));
            } catch (InvalidSyntaxException e) {
                // cannot happen
                System.err.println("Internal error, invalid filter string. ");
                e.printStackTrace();
            }
        }
        return dispatcher;
    }
    
    void close() {
		Iterator<ServiceTracker< ? , ? >> i = trackers.values().iterator();
        while (i.hasNext())
			i.next().close();
    }

    // Find a better place for this method...
    void log(int severity, String message, Throwable throwable) {
        System.out.print("Log entry | Serverity: " + severity + 
                " Message: " + message + " Throwable: ");
        if(throwable == null)
            System.out.println("null");
        else
            throwable.printStackTrace(System.out);

        LogService logService = 
            getTracker(LogService.class).getService();
        
        if (logService != null)
            logService.log(severity, message, throwable);
    }
    
	private <T> ServiceTracker<T,T> openTracker(Class<T> trackedClass,
			ServiceTrackerCustomizer<T,T> customizer) {
        
		ServiceTracker<T,T> tracker = new ServiceTracker<>(bc, trackedClass,
                customizer);
        tracker.open();
        trackers.put(trackedClass, tracker);
        
        return tracker;
    }
}
