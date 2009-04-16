/*
 * $Id$
 *
 * Copyright (c) The OSGi Alliance (2009). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.webcontainer.tw2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;

import org.osgi.test.cases.webcontainer.util.Constants;
import org.osgi.test.cases.webcontainer.util.Event;
import org.osgi.test.cases.webcontainer.util.EventLogger;

public class TestServletRequestAttributeListener implements
        ServletRequestAttributeListener {

    @PostConstruct
    public void postConstruct() {
        EventLogger.logEvent(new Event(this.getClass().getName(), Constants.POSTCONSTRUCT, ""));
    }

    @PreDestroy
    public void preDestroy() {
        EventLogger.logEvent(new Event(this.getClass().getName(), Constants.PREDESTROY, ""));
    }
    
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        EventLogger.logEvent(new Event(this.getClass().getName(), "attributeAdded", srae.getName() + "-" + srae.getValue()));
    }

    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        EventLogger.logEvent(new Event(this.getClass().getName(), "attributeRemoved", srae.getName()));
    }

    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        EventLogger.logEvent(new Event(this.getClass().getName(), "attributeReplaced", srae.getName() + "-" + srae.getValue()));
    }

}
