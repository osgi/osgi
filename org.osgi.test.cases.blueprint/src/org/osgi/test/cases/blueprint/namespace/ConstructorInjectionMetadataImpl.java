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

package org.osgi.test.cases.blueprint.namespace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.blueprint.reflect.*;


/**
 * A ConstructorInjectionMetadata implementation class used for
 * testing NamespaceHandler functions.  The blueprint
 * service implementation must be capable of receiving
 * different implementations of these classes back
 * without error, so we'll generally perform deep
 * copy operations and replace all of the interfaces
 * in the metadata with our replacement versions.
 * As long as we have a true implementation, this
 * should work ok.
 */
public class ConstructorInjectionMetadataImpl implements ConstructorInjectionMetadata {
    private List parameterSpecifications = new ArrayList();

    public ConstructorInjectionMetadataImpl() {

    }

    public ConstructorInjectionMetadataImpl(ConstructorInjectionMetadata source) {
        Iterator i = source.getParameterSpecifications().iterator();
        while (i.hasNext()) {
            parameterSpecifications.add(new ParameterSpecificationImpl((ParameterSpecification)i.next()));
        }
    }


    /**
     * The parameter specifications that determine which constructor to invoke
     * and what arguments to pass to it.
     *
     * @return an immutable list of ParameterSpecification, or an empty list if the
     * default constructor is to be invoked. The list is ordered by ascending parameter index.
     * I.e., the first parameter is first in the list, and so on.
     */
    public List getParameterSpecifications() {
        return parameterSpecifications;
    }

    public void addParameterSpecification(ParameterSpecification p) {
        parameterSpecifications.add(p);
    }

}

