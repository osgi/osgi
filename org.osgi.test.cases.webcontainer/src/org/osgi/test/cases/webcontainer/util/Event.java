/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.webcontainer.util;

/**
 * @version $Rev$ $Date$
 */
public class Event {
    private String className;
    private String methodName;
    private String desp;
    private long time;

    public Event(String className, String methodName, String desp) {
        super();
        this.className = className;
        this.methodName = methodName;
        this.desp = desp;
        this.setTime();
    }
    
    public Event(String className, String methodName, String desp, long time) {
        super();
        this.className = className;
        this.methodName = methodName;
        this.desp = desp;
        this.time = time;
    }
    
    public Event(String className, String methodName) {
        super();
        this.className = className;
        this.methodName = methodName;
        this.setTime();
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }
    
    public void setTime() {
        this.time = System.currentTimeMillis();
    }
    
    public long getTime() {
        return this.time;
    }
}
