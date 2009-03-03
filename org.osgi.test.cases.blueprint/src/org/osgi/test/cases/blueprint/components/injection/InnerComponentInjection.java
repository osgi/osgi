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

package org.osgi.test.cases.blueprint.components.injection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;

public class InnerComponentInjection extends BaseTestComponent {

    public InnerComponentInjection() {
        super();
    }

    // Manually inject componentId
    public InnerComponentInjection(String componentId) {
        super(componentId);
        setArgumentValue("arg1", componentId);
    }

    public InnerComponentInjection(String componentId, ComponentTestInfo arg2) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
    }

    public InnerComponentInjection(String componentId, ComponentTestInfo arg2, ComponentTestInfo arg3) {
        super(componentId);
        setArgumentValue("arg1", componentId);
        setArgumentValue("arg2", arg2);
        setArgumentValue("arg3", arg3);
    }

    // Don't care about componentId. This injection form is used to validate values, not events
    public InnerComponentInjection(ComponentTestInfo arg1) {
        super();
        setArgumentValue("arg1", arg1);
    }

    public InnerComponentInjection(ComponentTestInfo arg1, ComponentTestInfo arg2) {
        super();
        setArgumentValue("arg1", arg1);
        setArgumentValue("arg2", arg2);
    }

    public InnerComponentInjection(List arg1) {
        super();
        setArgumentValue("argList", arg1, List.class);
    }

    public InnerComponentInjection(Set arg1) {
        super();
        setArgumentValue("argSet", arg1, Set.class);
    }

    public InnerComponentInjection(Map arg1) {
        super();
        setArgumentValue("argMap", arg1, Map.class);
    }


    //Property Setter
    public void setInnerComponent(ComponentTestInfo arg1) {
        this.setPropertyValue("innerComponent", arg1, ComponentTestInfo.class);
    }

}
