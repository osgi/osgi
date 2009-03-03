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

package org.osgi.test.cases.blueprint.framework;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.test.cases.blueprint.services.ComponentTestInfo;


//only for the inner component injected from constructor-arg, the inner component must be ComponentTestInfo
//outer component should also be ComponentTestInfo
public class InnerComponentValidator extends MetadataValidator {

    // the outer component id
    protected String outerComponentId;
    // the inner component arg
    protected String innerComponentArg;
    // the classname of the component
    protected Class innerComponentClass;
    // a set of component properties we're expecting to find in the instance
    protected Dictionary innerComponentProps;

    public InnerComponentValidator(String outerComponentId, String innerComponentArg, Class innerComponentClass, Dictionary innerComponentProps) {
        super();
        this.outerComponentId = outerComponentId;
        this.innerComponentArg = innerComponentArg;
        this.innerComponentClass = innerComponentClass;
        this.innerComponentProps = innerComponentProps;
    }

    public void validate(BundleContext testContext) throws Exception {
        // ensure we have everything initialized
        super.validate(testContext);
        // validation is done by the metadata wrapper.
        Object outerComponent = moduleMetadata.getComponent(outerComponentId);
        if (outerComponent instanceof ComponentTestInfo){
            Hashtable outerComponentArgs = (Hashtable)((ComponentTestInfo)outerComponent).getProperty(ComponentTestInfo.COMPONENT_ARGUMENTS);
            Object innerComponent = outerComponentArgs.get(innerComponentArg);
            moduleMetadata.validateComponent(innerComponent, innerComponentClass, innerComponentProps);
        }else{
            fail("outer component is not ComponentTestInfo.");
        }
    }

}
