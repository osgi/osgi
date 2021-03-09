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
package org.osgi.test.cases.dmt.tc3.tbc.ConfigurationPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

class ConfigIdHandler {
    private static final String ID_MAP_KEY = "IDmap";
    
	private ServiceTracker<ConfigurationAdmin,ConfigurationAdmin>	configTracker;
	private ServiceTracker<LogService,LogService>					logTracker;
	private Hashtable<String,String>								idMap;
    
	ConfigIdHandler(
			ServiceTracker<ConfigurationAdmin,ConfigurationAdmin> configTracker,
			ServiceTracker<LogService,LogService> logTracker) {
        this.configTracker = configTracker;
        this.logTracker = logTracker;
		idMap = new Hashtable<>();
    }
    
	@SuppressWarnings({
			"rawtypes", "unchecked"
	})
	void updated(Dictionary<String, ? > properties)
			throws ConfigurationException {
        if (properties == null) {
			idMap = new Hashtable<>();
            return;
        }
        byte[] bytes = (byte[]) properties.get(ID_MAP_KEY);
        if (bytes == null) {
			idMap = new Hashtable<>();
            return;
        }
        ObjectInputStream stream;
        try {
            stream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            idMap = (Hashtable) stream.readObject();
        } catch (Exception e) {
            throw new ConfigurationException(ID_MAP_KEY,
                    "Unable to deserialize ID mapping table.", e);
        }
    }
    
    void update() throws IOException {
        ConfigurationAdmin ca = configTracker.getService();
        if(ca == null) {
            log(LogService.LOG_WARNING, "Cannot find Configuration Admin " +
                    "service, not persisting ID mapping.", null);
            return; 
        }

        Configuration config = ca.getConfiguration(
                ConfigPluginActivator.DMT_CONFIG_PLUGIN_SERVICE_PID);
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        new ObjectOutputStream(stream).writeObject(idMap);
        stream.flush();
        byte[] bytes = stream.toByteArray();
        stream.close();
        
		Dictionary<String,Object> properties = new Hashtable<>();
        properties.put(ID_MAP_KEY, bytes);
        config.update(properties);
    }

    String findMappedPidByNodeName(String nodeName) {
        return idMap.get(nodeName);
    }
    
    String getNodeNameForPid(String pid) {
		Iterator<Entry<String,String>> i = idMap.entrySet().iterator();
        while (i.hasNext()) {
			Entry<String,String> entry = i.next();
            if(pid.equals(entry.getValue()))
                return entry.getKey(); 
        }
		// Uri.mangle has been removed in DmtAdmin spec 2.0
		// TestCase needs update
//        return Uri.mangle(pid);
        return pid;
    }
    
    void removeMapping(String nodeName) throws IOException {
        if(idMap.remove(nodeName) != null)
            update();
    }
    
    // returns false if the nodeName or value already existed in the table
    boolean addMapping(String nodeName, String pid) throws IOException {
        if(idMap.containsKey(nodeName) || idMap.containsValue(pid))
            return false;
        
        idMap.put(nodeName, pid);
        update();
        return true;
    }

	void cleanupMap(Set<String> pids) throws IOException {
        boolean dirty = false;
		Iterator<Entry<String,String>> i = idMap.entrySet().iterator();
        while (i.hasNext())
			if (!pids.contains(i.next().getValue())) {
                i.remove();
                dirty = true;
            }
        if(dirty)
            update();
    }
    
    private boolean log(int severity, String message, Throwable throwable) {
        LogService logService = logTracker.getService();
        
        if (logService != null)
            logService.log(severity, message, throwable);
        
        return logService != null;
    }

    
    static boolean matchingId(String nodeName, String pid) {
		// Uri.mangle has been removed in DmtAdmin spec 2.0
		// TestCase needs update
//        return Uri.mangle(pid).equals(nodeName);
        return pid.equals(nodeName);
    }
}
