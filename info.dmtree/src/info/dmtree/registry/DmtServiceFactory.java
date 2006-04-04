/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
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
package info.dmtree.registry;

import info.dmtree.DmtAdmin;
import info.dmtree.notification.NotificationService;


/**
 * This class is the central access point for Device Management services.
 * Applications can use the static factory methods provided in this class to
 * obtain access to the different Device Management related services, such as
 * the DmtAdmin for manipulating the tree, or the Notification Service for
 * sending notifications to management servers.
 * <p>
 * These methods are not needed in an OSGi environment, clients should retrieve
 * the required service objects from the OSGi Service Registry. 
 */
public final class DmtServiceFactory {
    /**
     * A private constructor to suppress the default public constructor.
     */
    private DmtServiceFactory() {}
    
    /**
     * This method is used to obtain access to <code>DmtAdmin</code>, which
     * enables applications to manipulate the Device Management Tree.
     * 
     * @return a DmtAdmin service object
     */
    public static DmtAdmin getDmtAdmin() {
        return null;
    }

    /**
     * This method is used to obtain access to <code>NotificationService</code>,
     * which enables applications to send asynchronous notifications to
     * management servers.
     * 
     * @return a NotificationService service object
     */
    public static NotificationService getNotificationService() {
        return null;
    }
}
